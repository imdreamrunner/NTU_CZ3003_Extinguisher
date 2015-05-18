package misc;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.bson.types.ObjectId;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.CompletionStatus.CompletionFlag;
import cms.config1.workflows.otherWorkflows.ActiveRequest;
import cms.dataClasses.Errors;
import cms.dataClasses.InputMapSpec;
import cms.serviceInterfaces.DatabaseService;
import cms.serviceInterfaces.ErrorService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class RTest {
	private static boolean validate(Map<String, Object> queryMap){
		return true;
	}
	private static BasicDBObject convertTimeQueryObj(Map.Entry<String, Object> entry, boolean isEndTime){
		BasicDBObject query=new BasicDBObject();
		ArrayList<BasicDBObject> inner=new ArrayList<BasicDBObject>();
		inner.add(new BasicDBObject(entry.getKey(),new BasicDBObject("$gt",((Map)entry.getValue()).get("after"))));
		inner.add(new BasicDBObject(entry.getKey(),new BasicDBObject("$lt",((Map)entry.getValue()).get("before"))));
		if(isEndTime){
			ArrayList<BasicDBObject> outer=new ArrayList<BasicDBObject>();
			outer.add(new BasicDBObject("endTime",null));
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
			}else{
				conditions.add(new BasicDBObject(entry.getKey(),new BasicDBObject("$in",entry.getValue())));
			}
		}
		query.put("$and", conditions);
		return query;		
	}

	public static void main(String argsp[]){
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		try {
			map = mapper.readValue(new File("src/misc/query.json"), Map.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Map<String, Object> queryMap = (Map<String, Object>) map.get("query");
		
		BasicDBObject query=convertToQueryObj(queryMap);
		MongoClient cli=null;
    	try {
			cli=new MongoClient("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	DB db=cli.getDB("mydb");
		DBCollection incCol = db.getCollection("incident");
		DBCursor cursor=incCol.find(query);
		System.out.println(query);
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		return;
	}

}
