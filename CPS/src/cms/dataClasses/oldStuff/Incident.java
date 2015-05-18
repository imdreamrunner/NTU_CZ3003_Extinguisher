package cms.dataClasses.oldStuff;

import java.io.IOException;
import java.util.*;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class Incident {
	private Incident parent;
	private String type;
	private ArrayList<Location> location;
	private int level;
	private Calendar startTime;
	private Calendar completeTime;
	private boolean isValid;
	private Operator operator;
	private Reporter reporter;
	private String remark;
	public Incident getParent() {
		return parent;
	}
	public void setParent(Incident parent) {
		this.parent = parent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<Location> getLocation() {
		return location;
	}
	public void setLocation(ArrayList<Location> location) {
		this.location = location;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public Calendar getStartTime() {
		return startTime;
	}
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	public Calendar getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(Calendar completeTime) {
		this.completeTime = completeTime;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	public Reporter getReporter() {
		return reporter;
	}
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/*
	public ObjectId save(DB db) throws IOException{
		DBCollection incidentColl=db.getCollection("incident");
		DBCollection operatorColl=db.getCollection("operator");
		
    	ObjectMapper mapper = new ObjectMapper();
    	
    	String jsonStr=mapper.writeValueAsString(this);
    	JsonNode rootNode=mapper.readValue(jsonStr, JsonNode.class);
    	BasicDBObject query=new BasicDBObject("username",this.operator.getUsername());
    	DBCursor cursor=operatorColl.find(query);
    	ObjectId operatorId;
    	if (cursor.hasNext()){
    		operatorId=(ObjectId) cursor.next().get("_id");
    	}else{
    		operatorId=this.operator.save(db);
    	}
    	((ObjectNode)rootNode).remove("operator");
    	((ObjectNode)rootNode).put("operator",operatorId.toString());
    	mapper.writeValue(jsonStr, rootNode);
	}
	*/
}

