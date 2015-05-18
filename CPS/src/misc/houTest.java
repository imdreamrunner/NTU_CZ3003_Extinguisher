package misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.bson.types.ObjectId;

import cms.dataClasses.PMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.mongodb.*;
import com.mongodb.util.JSON;

public class houTest {
	public static void dbTest1(){
		try{
		// create PMap
		String s = "{\"_id\":\"houyunqing\", \"password\":\"123\"}";
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> operator = mapper.readValue(s, Map.class);
		// connect to db
		MongoClient client = new MongoClient("localhost");
		DB db = client.getDB("testcms");
		DBCollection opCol = db.getCollection("operator");
		// query database
		s = mapper.writeValueAsString(operator);
		s = "{priviledges:{$in:['delete','operator.create']}}";
		TmpOut.pln("Query object: "+s);
		DBObject dbo = (DBObject) JSON.parse(s);
		DBCursor ddd = opCol.find(dbo);
		//DBCursor ddd = opCol.find(dbo);
		if(ddd==null){
			TmpOut.pln("no operator found");
		}else{
			TmpOut.pln(ddd.toString());
			TmpOut.pln(ddd.size()+"");
			while(ddd.hasNext()){
				DBObject i =ddd.next(); 
				TmpOut.pln(i.toString());
				Map<String, Object> imap = PMap.toMap(i); 
				TmpOut.pln(imap.get("priviledges").getClass().toString());
				TmpOut.pln(PMap.toDBObject(imap).toString());
			}
		}
		TreeSet<String> ts = new TreeSet<String>();
		ts.add("delete");
		ts.add("operator.create");
		BasicDBList list = new BasicDBList();
		list.addAll(new ArrayList(ts));
		DBObject inClause = new BasicDBObject("$in", list);
		DBObject query = new BasicDBObject("priviledges", inClause);
		TmpOut.pln(query.toString());
		ddd = opCol.find(query);
		
		TmpOut.pln(ddd.toString());
		TmpOut.pln(ddd.size()+"");
		while(ddd.hasNext()){
			DBObject i =ddd.next(); 
			TmpOut.pln(i.toString());
			Map<String, Object> imap = PMap.toMap(i); 
			TmpOut.pln(imap.get("priviledges").getClass().toString());
			TmpOut.pln(PMap.toDBObject(imap).toString());
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void dbTest2(){
		try{
			MongoClient client = new MongoClient("localhost");
			DB db = client.getDB("testcms");
			DBCollection incCol = db.getCollection("incident");
			DBObject query = new BasicDBObject();
			query.put("_id", new ObjectId("532ac7e484ae30bd1d89ffa0"));
			DBObject result= incCol.findOne(query);
			TmpOut.pln(result.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void DateFormatTest(){
		String origin = "{\"time\":null}";
		try{
		Map<String, Object> map = PMap.fromString(origin);
		TmpOut.pln(map.containsKey("time")+"");
		TmpOut.pln((map.get("time")==null)+"");
		//TmpOut.pln(map.get("time").getClass().toString());
		/*
		 * test result shows that a Date() object is of java.java.Long class
		 * A null value has a key, but its get(key) result is null.
		 */
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public static void httpClientTest1(){
		Response httpResponse;
		try {
			httpResponse = new Request("http://www.google.com")
			.getResource();
			TmpOut.pln(httpResponse.getBody());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static void main(String args[]){
		dbTest2();
	}
}
