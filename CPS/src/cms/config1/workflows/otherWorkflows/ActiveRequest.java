package cms.config1.workflows.otherWorkflows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import misc.TmpOut;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cms.dataClasses.AuthenSpec;
import cms.dataClasses.Errors;
import cms.dataClasses.InputMapSpec;
import cms.dataClasses.OutputDataSpec;
import cms.dataClasses.PMap;
import cms.dataClasses.QuerySpec;
import cms.dataClasses.ValidatedIncidentSpec;
import cms.serviceInterfaces.AuthenticationService;
import cms.serviceInterfaces.DatabaseService;
import cms.serviceInterfaces.ErrorService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;
import wFramework.base.work.CompletionStatus.CompletionFlag;

public class ActiveRequest extends Work {

	@Override
	public String getName() {
		return "active-request";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "The query could not be processed.";
	}
	
	@Override
	public Class[] getGuaranteedProducts(){
		return new Class[]{OutputDataSpec.class};
	}
	private static BasicDBObject convertTimeQueryObj(Map.Entry<String, Object> entry, boolean isEndTime){
		BasicDBObject query=new BasicDBObject();
		ArrayList<BasicDBObject> inner=new ArrayList<BasicDBObject>();
		Map<String, Object> timeMap = (Map<String, Object>)entry.getValue();
		boolean hasBeforeAfter=timeMap.containsKey("after")||timeMap.containsKey("before");
		if(timeMap.containsKey("after"))
			inner.add(new BasicDBObject(entry.getKey(),
					new BasicDBObject("$gt",timeMap.get("after"))));
		if(timeMap.containsKey("before"))
			inner.add(new BasicDBObject(entry.getKey(),
					new BasicDBObject("$lt",timeMap.get("before"))));
		if(isEndTime&&
				timeMap.containsKey("allowIncomplete")&&
				(boolean)timeMap.get("allowIncomplete")){
			ArrayList<BasicDBObject> outer=new ArrayList<BasicDBObject>();
			outer.add(new BasicDBObject("completeTime",null));
			if(hasBeforeAfter)
				outer.add(new BasicDBObject("$and",inner));
			query.put("$or",outer);
		}else{
			query.put("$and",inner);
		}
		return query;
	}
	private static BasicDBObject convertToQueryObj(Map<String, Object> queryMap){
		BasicDBObject query=new BasicDBObject();
		ArrayList<BasicDBObject> conditions=new ArrayList<BasicDBObject>();
		for (Map.Entry<String, Object> entry : queryMap.entrySet()){
			if (entry.getKey().equals("_id")){
				ArrayList<ObjectId> ids=new ArrayList<ObjectId>();
				for (String s:(ArrayList<String>)entry.getValue()){
					ids.add(new ObjectId(s));
				}
				conditions.add(new BasicDBObject(entry.getKey(),new BasicDBObject("$in",ids)));
			}else if(Arrays.asList("startTime","timeStamp").contains(entry.getKey())){
				conditions.add(convertTimeQueryObj(entry,false));
			}else if(entry.getKey().equals("completeTime")){
				conditions.add(convertTimeQueryObj(entry,true));
			}else if(entry.getKey().equals("isLatest")){
				conditions.add(new BasicDBObject(entry.getKey(),entry.getValue()));
			}else{
				conditions.add(new BasicDBObject(entry.getKey(),new BasicDBObject("$in",entry.getValue())));
			}
		}
		query.put("$and", conditions);
		return query;		
	}
	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		Map<String, Object> map = (Map<String, Object>) ctx.getUnnamed(InputMapSpec.class);
		while(true){
			if(!map.containsKey("query")) break;
			
			Map<String, Object> queryMap = (Map<String, Object>) map.get("query");
			
			QuerySpec querySpec = new QuerySpec();
			if(!querySpec.validate(queryMap, ctx, config))break;
			
			BasicDBObject query=convertToQueryObj(queryMap);
			DatabaseService ds = (DatabaseService)config.getService(DatabaseService.class);
			DBCollection incCol = ds.getDBCollection("incident");
			DBCursor cursor=incCol.find(query);
			
			AuthenticationService as=(AuthenticationService) config.getService(AuthenticationService.class);
			
			ArrayList<Map<String, Object>> output = new ArrayList<Map<String,Object>>();
			while (cursor.hasNext()) {
				Map<String,Object> inc;
				try{
					inc=PMap.toMap((DBObject)cursor.next());
				}catch(Exception e){
					e.printStackTrace();
					return new CompletionStatus(CompletionStatus.CompletionFlag.Failure);
				}
				PMap.trimIncident(inc, as.hasPriviledge("incident.reporter.read", ctx, config));
				output.add(inc);
			}
			ctx.putUnnamed(OutputDataSpec.class, output);
			return null;
		}
		// GOTO_HEAD
		ErrorService es = (ErrorService)config.getService(ErrorService.class);
		es.reportError(Errors.newQueryError(), ctx, config);
		return new CompletionStatus(CompletionFlag.Failure);
	}

}
