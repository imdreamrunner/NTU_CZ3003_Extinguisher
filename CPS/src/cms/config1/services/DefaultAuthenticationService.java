package cms.config1.services;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.service.Service;
import wFramework.base.service.SystemService;

import cms.dataClasses.PMap;
import cms.serviceInterfaces.*;


public class DefaultAuthenticationService implements cms.serviceInterfaces.AuthenticationService{
	private static class Operator{
		public final String id;
		public final String priviledges[];
		public Operator(String ID, String priv[]){
			id = ID; priviledges = priv;
		}
	}
	private Object key = new Object();

	@Override
	public boolean authenticate(String id, String pwd, Context ctx, Configuration config) throws Exception {
		// TODO Auto-generated method stub
		try{
		DBCollection opCol = ((DatabaseService)config.getService(DatabaseService.class)).
				getDBCollection("operator");
		DBObject query = new BasicDBObject("_id", id).
									append("password", pwd);
		DBObject opObj = opCol.findOne(query);
		if(opObj==null) return false;
		
		// FIXME priviledges probably isn't String[]
		ArrayList<String> privs = (ArrayList<String>) opObj.get("priviledges");
		Operator op = new Operator(id, privs.toArray(new String[]{}));
		ctx.putUnnamed(key, op);
		return true;
		
		}catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public boolean hasPriviledge(String s, Context ctx, Configuration config) {
		if(!ctx.hasUnnamed(key))
			return false;
		Operator op = (Operator)ctx.getUnnamed(key);
		for(int i = 0; i<op.priviledges.length; i++){
			if(s.equals(op.priviledges[i]))
				return true;
		}
		return false;
	}
	@Override
	public String getOperatorId(Context ctx, Configuration config) {
		// TODO need to validate if service resource exists
		return ((Operator)ctx.getUnnamed(key)).id;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
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

	/*
	 * Authentication Service is completely stateless, we don't use the things below
	 */
	@Override
	public boolean startup() {
		return true;		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean transitToThisStarts(Service oldService) {
		return true;
	}

	@Override
	public void transitToThisEnds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transitToAnotherEnds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSystemServiceHandler(SystemService service) {
		// TODO Auto-generated method stub
		
	}	
}
