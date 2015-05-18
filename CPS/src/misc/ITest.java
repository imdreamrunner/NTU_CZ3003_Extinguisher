package misc;

import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import cms.dataClasses.oldStuff.Incident;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.mongodb.*;
import com.mongodb.util.*;

public class ITest {
	public static void main(String args[]) throws JsonProcessingException{
		
		MongoClient cli=null;
    	try {
			cli=new MongoClient("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	DB db=cli.getDB("mydb");
    	DBCollection coll=db.getCollection("incident");
    	
    	ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    	Incident inc=null;
    	Map<String, Object> mmm = null;
    	try {
			mmm = mapper.readValue(new File("src/misc/i.json"), Map.class);
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
    	String jsonStr=mapper.writeValueAsString(mmm);
    	DBObject dbObject = (DBObject) JSON.parse(jsonStr);
    	coll.insert(dbObject);
    }
}
