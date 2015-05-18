package wFramework.base.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import wFramework.base.NamedObjContainer;
import wFramework.base.service.Service;
import wFramework.base.work.Work;
import wFramework.base.work.WorkGroup;


// we use partial config to build up all the way to a full, immutable configuration
public class PartialConfig
{	
	public NamedObjContainer workGroupCtors;
	public NamedObjContainer workGroups;
	public NamedObjContainer works;
	public NamedObjContainer workflows;
	
	public ArrayList<Class> services;
	public HashMap<Class, Class> serviceProviders;
	
	private boolean error = false;
	
	public PartialConfig(){
		workGroupCtors = new NamedObjContainer();
		workGroups = new NamedObjContainer();
		works = new NamedObjContainer();
		workflows = new NamedObjContainer();
		services = new ArrayList<Class>();
		serviceProviders = new HashMap<Class, Class>();
	}
	
	public boolean hasError(){return error;}
	
	public void registerWorkGroupCtor(String name, WorkGroup.WorkGroupConstructor ctor){
		workGroupCtors.put(name, ctor);
	}
	public void registerWorkGroup(String name, WorkGroup wg){
		workGroups.put(name, wg);
	}
	public void registerWork(String name, Work work){
		works.put(name, work);
	}
	public void registerWorkflow(String name, Work work){
		workflows.put(name, work);
	}
	
	public void registerService(Class service){
		if(!Service.class.isAssignableFrom(service)){
			reportErr("Trying to register "+service.getName()+", but it's not a service.");
			return;
		}
		if(!services.contains(service))
			services.add(service);
	}
	public void registerServiceProvider(Class service, Class provider){
		// check if service is registered
		if(!services.contains(service)){
			reportErr("Service '"+service.getName()+"' has not been registered yet.");
			return;
		}
		// check if another provider exists
		if(serviceProviders.containsKey(service)){
			reportErr("Service '"+service.getName()+"' has more than one registered provider.");
			return;
		}
		// we have confirmed that service has been registered, and that a provider isn't provided yet
		// check if implementation is of the proper class
		if(!service.isAssignableFrom(provider))
		{
			reportErr("The service provider ("+provider.getName()+") is not an implementation of "+service.getName());
			return;
		}
		// put provider online
		serviceProviders.put(service, provider);
	}

	// get all provider classes for all registered service classes
	// used by SystemService to instantiate services
	public Set<Entry<Class, Class>> getServiceProviders(){
		return serviceProviders.entrySet();
	}
	public HashMap<Class, Class> getServiceProviderMap(){
		return serviceProviders;
	}
	private void reportErr(String err){
		error = true;
		System.out.println("Configuration error: "+err);
	}
	
}