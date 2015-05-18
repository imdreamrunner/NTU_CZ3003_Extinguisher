package wFramework.base.service;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;


public interface SystemService extends Service {

	// trivial service stuffs
	
	// pathToConfig is the path to config-service provider. We use it to generate the initial
	// configuration
	public void systemStartup(String pathToConfig);
	public void systemShutdown();
	public boolean loadNewConfig(String pathToConfig);
	public void launchWorkflow(String workName, Context initContext);
	public SystemServiceHandle getSystemServiceHandle();
	public Configuration getCurrentConfig();
}
