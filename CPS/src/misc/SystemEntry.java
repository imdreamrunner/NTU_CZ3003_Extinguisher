package misc;


import wFramework.base.service.SystemService;

public class SystemEntry {
	public static void main(String[] args)
	{
		if(args.length<2){
			System.out.println("Usage: java SystemEntry name1 name2");
			System.out.println(" name1: name of ConfigManager class");
			System.out.println(" name2: name of ConfigBuilder class");
			return;
		}
		
		System.out.println("CPS bootstrap working...");
		
		/*
		 * 1. load ConfigManager at specified path
		 * 2. ConfigManager loads config-module (stateless service)
		 * 3. config-module configures ConfigurationManager
		 * 4. ConfigManager starts up services in the first configuration
		 * 
		 * only step 1 is done here.
		 */
		SystemService cm;
		try{
			ClassLoader loader = SystemEntry.class.getClassLoader();
			Class<SystemService> c = (Class<SystemService>) loader.loadClass(args[0]);
			cm = (SystemService) c.newInstance();
		}
		catch(Exception e){
			System.out.println("ConfigManager cannot be loaded. Exiting.");
			e.printStackTrace();
			return;
		}
		System.out.println("CPS bootstrap done. Passing control to ConfigManager");
		cm.systemStartup(args[1]);
	}
}
