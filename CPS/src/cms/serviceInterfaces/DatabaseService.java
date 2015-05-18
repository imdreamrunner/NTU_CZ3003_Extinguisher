package cms.serviceInterfaces;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import wFramework.base.service.Service;

public interface DatabaseService extends Service{
	
	/*
	 * how should the service look like? it does query, and that's it, right?
	 * I honestly don't know how they should look like for now, just a place holder here
	 */
	public DBCollection getDBCollection(String name);
	public DB getDB();
}
