package cms.config1.services;

import misc.TmpOut;
import wFramework.base.service.Service;
import wFramework.base.service.SystemService;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import cms.serviceInterfaces.*;


public class DefaultDatabaseService implements DatabaseService{

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canReplace(String version) {
		// TODO Auto-generated method stub
		return false;
	}

	private MongoClient client;
	private DB db;
	private String hostname="localhost";
	private String dbname = "testcms";
	@Override
	public boolean startup() {
		TmpOut.pln("database service starting...");
		// TODO we need a way to configure which addr to connect to
		try{
		client = new MongoClient(hostname);
		db = client.getDB(dbname);
		return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		client.close();
	}

	@Override
	public boolean transitToThisStarts(Service oldService) {
		return startup();
	}


	@Override
	public void transitToThisEnds() {}
	@Override
	public void transitToAnotherEnds() {
		shutdown();
		
	}

	@Override
	public void receiveSystemServiceHandler(SystemService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DBCollection getDBCollection(String name) {
		return db.getCollection(name);
	}

	@Override
	public DB getDB() {
		return db;
	}

}
