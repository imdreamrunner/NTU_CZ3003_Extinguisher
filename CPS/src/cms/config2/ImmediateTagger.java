package cms.config2;

import java.util.ArrayList;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import cms.dataClasses.PMap;
import cms.dataClasses.ValidatedIncidentSpec;
import cms.serviceInterfaces.DatabaseService;
import cms.serviceInterfaces.ErrorService;
import cms.serviceInterfaces.NotifierService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;

public class ImmediateTagger extends Work {


	@Override
	public String getName() {
		return "incident tagging";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "incident tagging failed";
	}
	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{ValidatedIncidentSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		Map<String, Object> validInc = (Map)ctx.getUnnamed(ValidatedIncidentSpec.class);
		NotifierService ns = (NotifierService)config.getService(NotifierService.class);
		DatabaseService ds = (DatabaseService)config.getService(DatabaseService.class);
		ErrorService es = (ErrorService)config.getService(ErrorService.class);
		DBCollection sysConfigCol = ds.getDBCollection("configs");
		DBObject result = sysConfigCol.findOne(new BasicDBObject("name", "immediateNotif"));
		if(result==null){
			es.reportProblem("Couldn't find immediateNotif's entry in configs", ctx, config);
			return null;
		}
		Map<String, Object> immediateNotif;
		try{
			immediateNotif= PMap.toMap(result);
		}catch(Exception e){
			e.printStackTrace();
			es.reportProblem("Couldn't find immediateNotif's entry in configs", ctx, config);
			return null;
		}
		ArrayList arr = (ArrayList)immediateNotif.get("value");
		String combined_tag = (String)validInc.get("type")+"_" +(Integer)validInc.get("level");
		if(arr.contains(combined_tag))
			ns.addTag("immediateNotif", ctx, config);
		return null;
	}

}
