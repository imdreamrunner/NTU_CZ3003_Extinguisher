package cms.dataClasses.oldStuff;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class Operator {
	private String username;
	private String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public ObjectId save(DB db) throws JsonProcessingException{
		DBCollection coll=db.getCollection("operator");
    	ObjectMapper mapper = new ObjectMapper();
    	String jsonStr=mapper.writeValueAsString(this);
    	DBObject dbObject = (DBObject) JSON.parse(jsonStr);
    	WriteResult wr=coll.insert(dbObject);
    	return (ObjectId) wr.getField("_id");
	}
	
}
