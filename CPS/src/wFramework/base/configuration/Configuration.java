package wFramework.base.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import wFramework.base.NamedObjContainer;
import wFramework.base.service.Service;
import wFramework.base.work.Work;

public class Configuration {
	/*
	 * We have a few things in a configuration
	 * A. Work-related
	 * 		WorkGroupCtors (*, needed only for text-based configuration)
	 * 		WorkGroups
	 * 		Works
	 * 
	 * B. Service-related
	 * 		a list of (ServiceName, ServiceInterface, ServiceProvider)
	 */
	private NamedObjContainer workflows;
	private HashMap<Class, Service> serviceInstances;
	
	
	
	public Configuration(PartialConfig pconfig, HashMap<Class, Service> serviceInstances)
			throws Exception
	{
		if(pconfig.hasError()){
			throw new Exception("Configuration contains error and cannot be built.");
		}
		this.workflows = pconfig.workflows;
		this.serviceInstances = serviceInstances;
	}
	
	public boolean hasService(Class service){
		return serviceInstances.containsKey(service);
	}
	// return an instance of the specified service
	public Service getService(Class service){
		// TODO error checking??
		return serviceInstances.get(service);
	}
	public boolean hasTopLevelWork(String name){
		return workflows.containsKey(name);
	}
	public Work getTopLevelWork(String name){
		return (Work)workflows.get(name);
	}
	
	private int counter;
	// increment() and decrement() are run when instance is created/destroyed
	// TODO we can use an interface to manage counter, so as to reduce blocking caused by "synchronized"
	public void increment(){
		synchronized(this){counter++;}
	}
	public void decrement(){
		synchronized(this){counter--;}
		// TODO we can do cleanup here.
	}
	public int getCounter(){
		return counter;
	}
	
	// this is a hack. Seriously how do we do access control in java??
	public HashMap<Class, Service> getServiceInstances(){
		return this.serviceInstances;
	}
}
