package cms.dataClasses;

import java.util.ArrayList;
import java.util.Map;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.ClassNullSpec;
import wFramework.base.work.ClassSpec;
import wFramework.base.work.Spec;
import cms.dataClasses.PMap.PMapSpec;
import cms.dataClasses.PMap.PMapSpecNode;
import cms.serviceInterfaces.DatabaseService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class QuerySpec implements Spec{
	private static class ALClassSpec implements Spec{
		private Class c;
		public ALClassSpec(Class c){
			this.c = c;
		}
		@Override
		public boolean validate(Object target, Context ctx, Configuration config) {
			if(target==null)return true;
			if(!(target instanceof ArrayList))
				return false;
			ArrayList al = (ArrayList)target;
			for(int i = 0; i<al.size(); i++){
				if(!c.isInstance((al.get(i))))
					return false;
			}
			return true;
		}	
	}
	private static class QueryTimeSpec implements Spec{
		/*
		private static PMapSpec validator = new PMap.PMapSpec(
			new PMapSpecNode[]{
				new PMap.PMapSpecNode("before",		new ClassNullSpec(Long.class)),
				new PMap.PMapSpecNode("after",		new ClassNullSpec(Long.class)),
				}	
		);
		*/
		@Override
		public boolean validate(Object target, Context ctx, Configuration config) {
			if(!(target instanceof Map))
				return false;
			Map map = (Map)target;			
			/*
			if(!validator.validate(map, ctx, config))
				return false;
				*/
			int count = 0;
			if(map.containsKey("before")){
				count++;
				if(!QueryEndTimeSpec.checkTime(map.get("before")))
					return false;
			}
			if(map.containsKey("after")){
				count++;
				if(!QueryEndTimeSpec.checkTime(map.get("after")))
					return false;
			}
			if (count==0)
				return false;
			return true;
		}
		
	}
	private static class QueryEndTimeSpec implements Spec{
		/*
		private static PMapSpec validator = new PMap.PMapSpec(
			new PMapSpecNode[]{
				new PMap.PMapSpecNode("before",		new ClassNullSpec(Long.class)),
				new PMap.PMapSpecNode("after",		new ClassNullSpec(Long.class)),
				new PMap.PMapSpecNode("allowIncomplete",new ClassNullSpec(Boolean.class))
				}	
		);
		*/
		public static boolean checkTime(Object o){
			return o.getClass()==Long.class || o.getClass() == Integer.class;
		}
		@Override
		public boolean validate(Object target, Context ctx, Configuration config) {
			if(!(target instanceof Map))
				return false;
			Map map = (Map)target;
			/*
			if(!validator.validate(map, ctx, config))
				return false;
				*/

			int count = 0;
			if(map.containsKey("before")){
				count++;
				if(!checkTime(map.get("before")))
					return false;
			}
			if(map.containsKey("after")){
				count++;
				if(!checkTime(map.get("after")))
					return false;
			}
			if(map.containsKey("allowIncomplete")){
				count++;
				if(map.get("allowIncomplete").getClass()!=Boolean.class)
					return false;
			}
			if (count==0)
				return false;
			return true;
		}
		
	}
	private static Spec stringSpec = new ClassSpec(String.class);
	private static Spec stringNullSpec = new ClassNullSpec(String.class);
	private static Spec dateSpec = new ClassNullSpec(Long.class);
	private static Spec mapSpec = new ClassSpec(Map.class);

	private static PMapSpecNode nodes[] = new PMapSpecNode[]{
	new PMap.PMapSpecNode("_id",		new ALClassSpec(String.class)),
	new PMap.PMapSpecNode("initialId",	new ALClassSpec(String.class)),			
	new PMap.PMapSpecNode("type", 		new ALClassSpec(String.class)),
	new PMap.PMapSpecNode("level", 		new ALClassSpec(Integer.class)),
	new PMap.PMapSpecNode("parent", 	new ALClassSpec(String.class)), 
	new PMap.PMapSpecNode("operator",	new ALClassSpec(String.class)),
	new PMap.PMapSpecNode("isLatest",	new ClassNullSpec(Boolean.class)),

	new PMap.PMapSpecNode("startTime", 	new QueryTimeSpec()),
	new PMap.PMapSpecNode("completeTime", 	new QueryEndTimeSpec()),
	new PMap.PMapSpecNode("timeStamp",	new QueryTimeSpec())
	};
	
	@Override
	public boolean validate(Object target, Context ctx, Configuration config) {
		if(!(target instanceof Map))
			return false;
		Map map = (Map)target;
		for (PMapSpecNode node:nodes){
			if(map.containsKey(node.name) && !node.spec.validate(map.get(node.name), ctx, config))
				return false;
		}
		return true;
	}
}
