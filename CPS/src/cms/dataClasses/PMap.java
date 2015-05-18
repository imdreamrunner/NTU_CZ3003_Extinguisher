package cms.dataClasses;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.ClassSpec;
import wFramework.base.work.Spec;

public class PMap extends HashMap<String, Object>{
	
	public static final ObjectMapper mapper = new ObjectMapper();
	public static DBObject toDBObject(Map<String, Object> map) throws JsonProcessingException{
		return (DBObject)JSON.parse(mapper.writeValueAsString(map));
	}
	public static Map<String, Object> fromString(String input) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(input, Map.class);
	}
	public static Map<String, Object> toMap(DBObject dbo) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(dbo.toString(), Map.class);
	}
	
	public static String toString(Map<String, Object> map) throws JsonProcessingException{
		return mapper.writeValueAsString(map);
	}
	public static String toStrig(ArrayList al) throws JsonProcessingException{
		return mapper.writeValueAsString(al);
	}
	public static class SimpleAttrSpec implements Spec{
		public final String name;
		public final Class type;
		public SimpleAttrSpec(String name, Class type){
			this.name = name;
			this.type = type;
		}
		@Override
		public boolean validate(Object target, Context ctx, Configuration config) {
			Map map = (Map)target;
			return (map.containsKey(name)) &&
					(type.isInstance(map.get(name)));
		}
	}
	public static class PMapSpecNode{
		/*
		 * there are two types of validation we do
		 * 1. check named node exists, and that the node conforms to particular spec
		 * 2. check named node does not exist
		 */
		public final String name;
		public final Spec spec;
		public final boolean checkAbsence;
		// for first type of check
		public PMapSpecNode(String name, Spec spec){
			this.name = name;
			this.spec = spec;
			this.checkAbsence = false;
		}
		// for second type of check
		public PMapSpecNode(String name){
			this.name = name;
			this.spec = null;
			this.checkAbsence = true;
		}
	}

	public static ClassSpec stringSpec = new ClassSpec(String.class);
	public static ClassSpec mapSpec = new ClassSpec(Map.class);
	public static class PMapSpec implements Spec{
		public final PMapSpecNode[] nodes;
		public PMapSpec(PMapSpecNode nodes[]){
			this.nodes = nodes;
		}
		@Override
		public boolean validate(Object target, Context ctx, Configuration config) {
			// self-validate first
			if(!(target instanceof Map))
				return false;
			// then invoke validators for sub-objects
			Map<String, Object> map = (Map<String, Object>)target;
			for(int i = 0; i<nodes.length; i++){
				// second type of check. see PMapSpecNode
				if(nodes[i].checkAbsence){
					if(map.containsKey(nodes[i].name))
						return false;
				}
				// first type of check
				else{
					// if the named node does not exist
					if(!map.containsKey(nodes[i].name))
						return false;
					// if the named node validation fails
					if(!nodes[i].spec.validate(map.get(nodes[i].name), ctx, config))
						return false;
				}
			}
			return true;
		}
	}
	// some PMapSpec use this
	public static Spec fakeSpec = new Spec(){
		public boolean validate(Object target, Context ctx, Configuration config) {
			return true;
		}
	};
	public static void trimIncident(DBObject inc, boolean keepReporter){
		String id = (String)((DBObject)inc.get("_id")).get("$oid");
		inc.put("_id", id);
		if(!keepReporter){
			inc.removeField("reporter");
		}
	}
	public static void trimIncident(Map<String, Object> inc, boolean keepReporter){
		String id = (String)((Map)inc.get("_id")).get("$oid");
		inc.put("_id", id);
		if(!keepReporter){
			inc.remove("reporter");
		}
	}
	public static Map<String, Object> shallowCopy(Map<String, Object> source){
		Map<String, Object> result = new HashMap<String, Object>();
		Iterator<Entry<String, Object>> it = source.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Object> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
