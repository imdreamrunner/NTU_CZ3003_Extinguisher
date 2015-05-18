package wFramework.serviceImpl1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.Iterator;
import java.util.Map.Entry;

import misc.TmpOut;


import wFramework.base.Context;
import wFramework.base.configuration.ConfigBuilder;
import wFramework.base.configuration.Configuration;
import wFramework.base.configuration.PartialConfig;
import wFramework.base.service.Service;
import wFramework.base.service.SystemService;
import wFramework.base.service.SystemServiceHandle;
import wFramework.base.work.Work;




public class DefaultSystemService implements SystemService{
	@Override
	public String getVersion() { return "0.1.0"; }
	@Override
	public boolean canReplace(String version) {
		// cannot replace anything
		return false;
	}
	@Override
	public boolean transitToThisStarts(Service oldService) {
		return false;
	}

	@Override
	public void transitToThisEnds() {
	}
	@Override
	public void transitToAnotherEnds() {
	}
	/*
	 *  We don't use the default startup() and shutdown() as this is a special service
	 *  that is itself in charge of system shutdown and startup
	 */
	@Override
	public boolean startup() {
		return true;
	}
	
	@Override
	public void shutdown() {
		// neither do we use this, we use systemShutdown instead
	}
	

	/*
	 * Our implemented stuffs go here
	 */
	
	private Configuration oldConfig;
	private Configuration currentConfig;
	
	SystemServiceHandle fakeHandle;
	
	@Override
	public void systemShutdown() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void systemStartup(String pathToConfig) {
		fakeHandle = new SystemServiceHandle(this);
		PartialConfig pconfig;
		// load ConfigBuilder
		try{
			pconfig = buildPConfig(pathToConfig);
		}catch(Exception e){
			e.printStackTrace();
			TmpOut.pln("Initial configuration failed, system exits.");
			return;
		}
		// create service instances
		/*
		 * here are the steps:
		 * 1. look for SystemService target
		 * 		if doesn't match this DefaultSystemService, we reject this startup config
		 * 2. start the services one by one, barring the system service
		 * 3. when all services are started, mark system as ready
		 */
		/*
		 * steps for service replacement:
		 * 1. mark new config as pending
		 * 2. get past point B in transition
		 * 3. mark new config as ready, divert new traffic to new config
		 * 4. get past point C in transition
		 * 5. clean up old configuration
		 */
		Map<Class, Class> providerMap= pconfig.getServiceProviderMap();
		// 1 deal with systemservice first
		if(!providerMap.containsKey(SystemService.class)){
			TmpOut.pln("SystemService has not been specified in the given configuration.");
			return;
		}
		if(providerMap.get(SystemService.class)!=this.getClass()){
			TmpOut.pln("SystemService specified in configuration does not match the one given to the bootstrap.");
			return;
		}
		// 2 start the other services
		// iterate over the registered providers one by one, and put into 
		// serviceInstance hashmap, which we need to create Configuration
		HashMap<Class, Service> serviceInstances = new HashMap<Class, Service>();
		Iterator<Entry<Class, Class>> it = pconfig.getServiceProviders().iterator();
		while(it.hasNext()){
			Entry<Class, Class> entry = it.next();
			// check if it's SystemService
			if(entry.getKey() == SystemService.class){
				serviceInstances.put(entry.getKey(), this);
				continue;
			}
			Service s;
			// try to start the services
			try
			{
				s = (Service)entry.getValue().newInstance();
				s.receiveSystemServiceHandler(getSystemServiceHandle());
				s.startup();
				serviceInstances.put(entry.getKey(), s);
			}
			// TODO shutdown already started services
			catch(Exception e)
			{
				TmpOut.pln("Service "+entry.getKey().getName()+" cannot be instantiated. Implementation: "+
						entry.getValue().getName());
				e.printStackTrace();
				return;
			}
		}
		// 3. create configuration, mark system as ready
		try 
		{
			currentConfig = new Configuration(pconfig, serviceInstances);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return;
		}
		/*
		// 2.5 start the services
		Iterator<Entry<Class, Service>> itt = serviceInstances.entrySet().iterator();
		// FIXME we cannot start services in any random order. Need to be able to specify order
		// of service startup
		while(itt.hasNext()){
			Entry<Class, Service> entry = itt.next();
			if(entry.getKey() == SystemService.class) continue;
			entry.getValue().receiveSystemServiceHandler(getSystemServiceHandle());
			entry.getValue().startup();
		}
		currentServiceInstances = serviceInstances;
		*/
		
		// stay infinitely long on network socket, until told to shutdown
	}
	
	private PartialConfig buildPConfig(String pathToConfig) throws Exception{

		ConfigBuilder cb;
		
		// load config builder
		ClassLoader loader = SystemService.class.getClassLoader();
		Class c = loader.loadClass(pathToConfig);
		cb = (ConfigBuilder) c.newInstance();
		// build config
		PartialConfig pconfig = new PartialConfig();
		cb.build(pconfig, this);
		return pconfig;
	}
	private static class TransitionPair{
		public Class serviceInterface;
		public Service oldInstance;
		public Service newInstance;
		public TransitionPair(Class interf, Service oldInst, Service newInst){
			this.serviceInterface = interf;
			oldInstance = oldInst;
			newInstance = newInst;
		}
	}
	private ArrayList<Service> droppedServices;
	private ArrayList<TransitionPair> replacedServices;
	
	/*
	 * We use 2 boolean variables for re-configuration
	 * 1. inTransition: 
	 * 	used to inform an attempt to reconfigure that another reconfiguration is in progress
	 * 2. shouldCheckCleanup
	 * 	used to inform workflow launcher that it should check cleanup when a workflow ends
	 * 
	 * 				 A			 B			  C
	 * inTransition: =========================
	 * shouldCheckCleanup:       =============
	 * 
	 * No 2 threads can enter loadNewConfig() because loadnewconfig is synchronized method
	 * At A: a new config has been created and currentConfig = newConfig has been done, so inTransition is set to true
	 * At B: the update to currentConfig is guaranteed to have become globally visible, so shouldCheckCleanup is set to true
	 * At C: cleanup is done, both inTransition and shouldCheckCleanup are set to fasle
	 * 
	 */
	private boolean inTransition = false;
	private boolean shouldCheckCleanup = false;
	private final Object cleanupLock = new Object();
	
	private void checkAndCleanOldConfig(){
		if(oldConfig.getCounter()!=0) 
			return;
		
		synchronized (cleanupLock){
			if(!inTransition)
				return;
			for(int i = 0; i<droppedServices.size(); i++)
				droppedServices.get(i).shutdown();
			for(int i = 0; i<replacedServices.size(); i++){
				TransitionPair tp = replacedServices.get(i);
				tp.oldInstance.transitToAnotherEnds();
				tp.newInstance.transitToThisEnds();
			}
			
			droppedServices = null;
			replacedServices = null;
			oldConfig = null;
			inTransition = false;
			shouldCheckCleanup = false;
			TmpOut.pln("Cleanup for old configuration is complete.");
		}
	}
	
	/*
	 * FIXME currently we have no tolerance for replacement failure.
	 * Look for area around 3.a. in loadNewConfig()
	 * We're signalling transition before we have validated that all replacements can be done
	 * This is a dangerous thing, relying on the reconfigurator's expertise for the system
	 * to perform transition correctly.
	 * 
	 * We can improve it to have fail-safe. But not gonna do it
	 **/
	@Override
	public synchronized boolean loadNewConfig(String pathToConfig) {
		/*
		 * reconfigure system.
		 * 
		 * steps:
		 * 0. make sure a previous transition is not in-progress
		 * 1. load config builder, build pconfig
		 * 2. validate pconfig
		 * 3. manage service replacement etc.
		 * 		3.a. check service replaceability
		 * 4. create and apply new config
		 * 5. cleanup when old config counter reaches zero
		 */
		// 0. we cannot load a new config if old config hasn't been cleared yet
		if(inTransition){
			TmpOut.pln("A previous reconfiguration has not completed. Another reconfiguration cannot be done now.");
			return false;
		}
		// 1. load ConfigBuilder and create pconfig
		Configuration newConfig;
		PartialConfig pconfig;
		try{
			pconfig = buildPConfig(pathToConfig);
		}catch(Exception e){
			e.printStackTrace();
			TmpOut.pln("New configuration at "+pathToConfig+" cannot be loaded.");
			return false;
		}
		// 2. validate config (workproduct dependency and service dependency)
		// TODO low priority, though. Currently this hasError() is a naive check
		if(pconfig.hasError()){
			TmpOut.pln("Configuration at "+pathToConfig+" has errors and cannot be used.");
			return false;
		}
		
		// 3. manage service replacement
		/*
		 * Several possible ways a service may be changed:
		 * A. dropped: 	no longer needed
		 * B. replaced: replaced by new provider
		 * C. kept: 	left as is
		 * D. newed: 	newly created service
		 * 
		 * 				reconfig-time					cleanup-time
		 * dropped:		nothing							shutdown()						
		 * replaced:	newInstance(), signalB()		signalC()
		 * 				add new to instances
		 * kept:		add old to instances
		 * newed:		newInstance(), startup()
		 * 				add new to instances
		 * 
		 * we look through each and every service in
		 * 1. current config
		 * 2. new config
		 * 
		 */
		HashMap<Class, Service> currentInstances = currentConfig.getServiceInstances();
		HashMap<Class, Class> newServices = pconfig.getServiceProviderMap();
		HashMap<Class, Service> newInstances = new HashMap<Class, Service>();
		

		/* 
		 * We keep a record of the dropped services and replaced services only, because they
		 * are the only ones that need action at cleanup time. newed and kept services only 
		 * need action at reconfig time
		 */
		//ArrayList<Service> newed 	= new ArrayList<Service>();
		//ArrayList<Service> kept 	= new ArrayList<Service>();
		ArrayList<Service> dropped 	= new ArrayList<Service>();
		// for service transition, we need to register the new and old instance together
		// for cleanup at a later time. see cleanupOldConfig()
		ArrayList<TransitionPair> replaced = new ArrayList<TransitionPair>();

		// we look for dropped, replaced and kept services first
		Iterator<Entry<Class, Service>> oldit = currentInstances.entrySet().iterator();
		while(oldit.hasNext()){
			Entry<Class, Service> entry = oldit.next();
			Class serviceInterface = entry.getKey();
			Service oldInstance= entry.getValue();
			// dropped
			if(newServices.containsKey(serviceInterface)==false){
				dropped.add(oldInstance);
				continue;
			}
			// kept
			if(newServices.get(serviceInterface)==oldInstance.getClass()){
				//kept.add(oldInstance);
				newInstances.put(serviceInterface, oldInstance);
			}
			// replaced
			else{
				// 1 create an instance for the new provider
				// 2 then signal transfer 
				Service newInstance;
				try{
				newInstance = (Service)newServices.get(serviceInterface).newInstance();
				// 3.a replaceability check
				if(!newInstance.canReplace(oldInstance.getVersion())){
					TmpOut.pln("Service "+serviceInterface.getName()+"of version "+oldInstance.getVersion()+
							" cannot be replaced by another version "+ newInstance.getVersion());
					return false;
				}
				}catch(Exception e){
					TmpOut.pln("Service "+serviceInterface.getName()+" cannot be instantiated. Implementation: "+
							newServices.get(oldInstance).getName());
					e.printStackTrace();
					return false;
				}

				newInstance.receiveSystemServiceHandler(getSystemServiceHandle());
				newInstance.transitToThisStarts(oldInstance);
				TransitionPair tp = new TransitionPair(serviceInterface, oldInstance, newInstance);
				replaced.add(tp);
				newInstances.put(serviceInterface, newInstance);
			}
		}
		this.replacedServices = replaced;
		this.droppedServices = dropped;
		// now we look for newed services
		Iterator<Entry<Class, Class>> newit = newServices.entrySet().iterator();
		while(newit.hasNext()){
			Entry<Class, Class> entry = newit.next();
			Class serviceInterface = entry.getKey();
			Class newServiceClass  = entry.getValue();
			if(!currentInstances.containsKey(serviceInterface)){
				Service newInstance;
				try{
				newInstance = (Service)newServiceClass.newInstance();
				}catch(Exception e){
					TmpOut.pln("Service "+serviceInterface.getName()+" cannot be instantiated. Implementation: "+
							newServiceClass.getName());
					e.printStackTrace();
					return false;
				}
				newInstance.receiveSystemServiceHandler(getSystemServiceHandle());
				newInstance.startup();
				//newed.add(newInstance);
				newInstances.put(serviceInterface, newInstance);
			}
		}
		/*
		 *  at this point, old instances from the kept group and new instances from the
		 *  newed and replaced group have been placed in newInstances. We are ready to create
		 *  a new configuration
		 */

		
		// 4. build and apply new config
		try {
			newConfig = new Configuration(pconfig, newInstances);
		} catch (Exception e) {
			TmpOut.pln("Configuration at "+pathToConfig+" has errors and cannot be used.");
			e.printStackTrace();
			return false;
		}
		oldConfig = currentConfig;
		currentConfig = newConfig;
		inTransition = true;

		// 5. cleanup
		/*
		 * the problem with this is that, while we're working on building a new config, and have
		 * confirmed that the current config's counter is zero, and decide to replace it, in the middle
		 * another workflow may arrive and increment the current config's counter, which makes cleanup
		 * dangerous.
		 * 
		 * cleanup checker				workflow launcher
		 * 								grab current config as active config
		 * (apply new config)
		 * check old counter is zero
		 * 								launch and increment counter (old)
		 * call cleanup (oldcounter!=0)
		 * 
		 * solution? put put cleanup checker and workflow launcher in the same lock
		 * 
		 * We only have one workflow launcher, but 2 cleanup checker invocations:
		 * 1. when a new configuration is just built
		 * 2. when finishing a workflow and decrementing its counter 
		 */
		class WaitForFlush extends TimerTask{
			@Override
			public void run() {
				shouldCheckCleanup = true;
				checkAndCleanOldConfig();
			}
		}
		Timer t = new Timer();
		t.schedule(new WaitForFlush(), 1000);
		return true;
	}
	
	
	@Override
	public void launchWorkflow(String workName, Context initCtx) {
		// if currentConfig is null, system is not ready for workflow launch
		if(currentConfig==null)
			return;
		/*
		 * 1. find named workflow
		 * 2. increment config counter
		 * 3. execute sequentially
		 * 4. decrement config counter
		 * 5. check if config should be cleaned up
		 */ 
		Work workflow;
		// we make a copy of currentConfig, because currentConfig may be changed
		// while we're running the workflow. But we don't want to increment one config
		// and then decrement another
		
		Configuration activeConfig = currentConfig;
		
		try{
		// 1 find workflow
		if(!activeConfig.hasTopLevelWork(workName)){
			TmpOut.pln("Received invalid request for Workflow: "+workName+", request dropped.");
			return;
		}else
			TmpOut.pln("Launching Workflow: "+workName);
		
		// 2 and 3
		workflow = activeConfig.getTopLevelWork(workName);
		// if initial context is not given, we must create one
		if(initCtx==null)initCtx = new Context();
		activeConfig.increment();
		workflow.runInContext(initCtx, activeConfig);
		
		}catch(Exception e){
			TmpOut.pln("A workflow named "+workName+" exceptioned at top level.");
			e.printStackTrace();
		}
		// 4
		activeConfig.decrement();
		// 5
		if(shouldCheckCleanup)
			checkAndCleanOldConfig();
	}
	@Override
	public SystemServiceHandle getSystemServiceHandle() {
		return fakeHandle;
	}
	@Override
	public void receiveSystemServiceHandler(SystemService service) {
		// we're the bloody system service itself, we need not receive anything
	}
	@Override
	public Configuration getCurrentConfig() {
		return currentConfig;
	}

}
