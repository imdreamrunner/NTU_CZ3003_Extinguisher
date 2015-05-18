package cms.serviceInterfaces;

import java.util.Map;

import wFramework.base.*;
import wFramework.base.configuration.Configuration;
import wFramework.base.service.Service;

public interface NotifierService extends Service{
	
	/*
	 * Just placeholder. should provide:
	 * 1. register observer
	 * 2. notify(Incident, tags)
	 */
	public boolean registerObserver(Map<String, Object> obs, String[] tags, Context ctx, Configuration config);
	public void addTag(String tagName, Context ctx, Configuration config);
	public void enque(Context ctx, Configuration config);
}
