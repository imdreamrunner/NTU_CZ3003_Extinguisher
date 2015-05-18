package wFramework.base.service;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;


/*
 * SystemService is a special service. It is the "parent" of all other services and it 
 * manages all other services. Services can be swapped in and out, so could SystemService.
 * But because of the special role the SystemService plays, it isn't so easy to swap it.
 * 
 * We can't just put a reference to it in a Context, because some Service can operate without
 * a Context. Context is request-specific, and some services operate in the absence of requests.
 * The NetworkService, for example, needs to be able to locate the SystemService when a request
 * arrives and before a Context is created for it, so that it can ask SystemService to create
 * an instance and a Context for the request.
 * 
 * To solve this problem, we use an indirection to SystemService. All Services that need to access
 * SystemService in the absence of a Context should hold on to a fake SystemService, i.e.
 * SystemServiceHandle. This handle is guaranteed to remain constant throughout system execution.
 * But the real SystemService it wraps around may be changed when we swap in a different SystemService.
 */
public class SystemServiceHandle implements SystemService{

	private SystemService realService;
	public SystemServiceHandle(SystemService realService){
		this.realService = realService;
	}
	public void resetRealService(SystemService oldService, SystemService newService){
		if(oldService == realService)
			realService = newService;
	}
	
	@Override
	public String getVersion() {
		return realService.getVersion();
	}

	@Override
	public boolean canReplace(String version) {
		return realService.canReplace(version);
	}

	@Override
	public boolean startup() {
		return realService.startup();
	}

	@Override
	public void shutdown() {
		realService.shutdown();
	}

	@Override
	public boolean transitToThisStarts(Service oldService) {
		return realService.transitToThisStarts(oldService);
	}

	@Override
	public void transitToThisEnds() {
		realService.transitToThisEnds();
	}

	@Override
	public void transitToAnotherEnds() {
		realService.transitToAnotherEnds();
	}

	@Override
	public void systemStartup(String pathToConfig) {
		realService.systemStartup(pathToConfig);
	}

	@Override
	public void systemShutdown() {
		realService.systemShutdown();
	}

	@Override
	public boolean loadNewConfig(String pathToConfig) {
		return realService.loadNewConfig(pathToConfig);
	}

	@Override
	public void receiveSystemServiceHandler(SystemService service) {
		realService.receiveSystemServiceHandler(service);
	}

	@Override
	public SystemServiceHandle getSystemServiceHandle() {
		return realService.getSystemServiceHandle();
	}

	@Override
	public void launchWorkflow(String workName, Context initContext) {
		realService.launchWorkflow(workName, initContext);		
	}
	@Override
	public Configuration getCurrentConfig() {
		return realService.getCurrentConfig();
	}

}
