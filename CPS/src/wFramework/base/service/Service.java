package wFramework.base.service;

import java.util.Map;


public interface Service {
	
	/* 
	 * we use version string to do some basic verification for service transition
	 * A service provider can only replace a limited set of providers of the same service 
	 */
	public String getVersion();
	public boolean canReplace(String version);
	
	// called during system startup, service sets up resources
	public boolean startup();
	
	// called at system shutdown, close all resources owned by this service
	public void shutdown();
	
	/*
	 * Apart from clean startup and shutdown, we also support service "transition"
	 * 
	 * old service lifeline:    ===========================
	 * new service lifeline:                        ============================
	 * labels:                  A                   B     C                    D
	 * 
	 * A represents system startup
	 * B occurs when a new version of the same service is configured
	 * C occurs when all instances of the old configuration have been closed
	 * D represents system shutdown
	 * 
	 * A: old service's startup() is called
	 * B: old service's transitToAnotherStarts() [note: this is no longer used. see AAXA below]
	 *    new service's transitToThisStarts()
	 * C: old service's transitToAnotherEnds();
	 *    new service's transitToThisEnds();
	 * D: new service's shutdown()
	 * 
	 * In case peaceful transition cannot exist, i.e. new and old provider cannot co-exist,
	 * at point B all requests to instantiate the new config will be stalled, and can proceed
	 * only when C has been reached (for all the services that cannot peacefully transit)
	 * 
	 */
	/*
	 * TODO
	 * we need a way to handle transition failure. Transitions can fail. How to recover?
	 * Ask the original service provider to resume its operation? Or just cry and give up?
	 */
	public boolean transitToThisStarts(Service oldService);
	/*
	 * AAXA: we do no use transitToAnotherStarts, here's why:
	 * when a service X transits to service Y, Y's transitToThisStarts will be called,
	 * and the old service instance will be passed in as parameter
	 * 
	 * It is Y's responsibility to inform X that a transition has started
	 * 
	 * X and Y have to implement their own interfaces for transition. We don't specify how
	 * it is to be done.
	 * 
	 */
	//public void transitToAnotherStarts();
	
	public void transitToThisEnds();
	public void transitToAnotherEnds();
	
	public void receiveSystemServiceHandler(SystemService service);
}
