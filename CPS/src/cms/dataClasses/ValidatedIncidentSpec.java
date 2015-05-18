package cms.dataClasses;
import java.util.ArrayList;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.ClassNullSpec;
import wFramework.base.work.ClassSpec;
import wFramework.base.work.Spec;
import cms.dataClasses.PMap.PMapSpecNode;
import cms.dataClasses.PMap.PMapSpec;
import cms.serviceInterfaces.DatabaseService;

public class ValidatedIncidentSpec implements Spec {
	
	private static Spec stringSpec = new ClassSpec(String.class);
	private static Spec stringNullSpec = new ClassNullSpec(String.class);
	private static Spec dateSpec = new ClassNullSpec(Long.class);
	private static Spec mapSpec = new ClassSpec(Map.class);
	private static Spec alSpec  = new ClassSpec(ArrayList.class);
	private static PMapSpec validator = new PMap.PMapSpec(
			new PMapSpecNode[]{	
			// Check for null situation
			new PMap.PMapSpecNode("initialId", 	stringNullSpec),
			new PMap.PMapSpecNode("parent", 	stringNullSpec), 
			new PMap.PMapSpecNode("type", 		stringSpec),
			new PMap.PMapSpecNode("location", 	alSpec),
			new PMap.PMapSpecNode("level", 		new ClassSpec(Integer.class)),
			// Add DateSpec to check date
			new PMap.PMapSpecNode("startTime", 	dateSpec),
			new PMap.PMapSpecNode("completeTime", 	dateSpec),
			new PMap.PMapSpecNode("isValid", 	new ClassSpec(Boolean.class)),
			new PMap.PMapSpecNode("reporter",   mapSpec),
			new PMap.PMapSpecNode("remark", 	stringSpec),
			new PMap.PMapSpecNode("isLatest"),
			new PMap.PMapSpecNode("_id"),	// these nodes check for the absence of the named node
			new PMap.PMapSpecNode("operator"),
			new PMap.PMapSpecNode("timeStamp"),
		}
	);
	
	@Override
	public boolean validate(Object target, Context ctx, Configuration config) {
		if(!(target instanceof Map))
			return false;
		Map map = (Map)target;
		// NOTE the problem with dropping keys in a validator is that...
		// validator is supposed to "validate", not to "check and improve"
		// the map.remove() part has been moved to Validate work
		
		if(!validator.validate(map, ctx, config))
			return false;
		
		
		// check at initialId, extra checks needed if it is not null
		// initialId should be non-null only if we're updating an incident
		String initialId = (String)map.get("initialId"); 
		if(initialId != null){
			DBCollection incCol = ((DatabaseService)config.getService(DatabaseService.class))
				.getDBCollection("incident");
			DBObject initInc = incCol.findOne(new BasicDBObject("_id", new ObjectId(initialId)));
			// we need to make sure inittialId's incident
			// 1. exists, 
			// 2. is indeed initial
			if(initInc==null) // 1
				return false; 
			if(!initInc.get("initialId").toString().equals(initialId)) // 2
				return false;
		}
		return true;
	}
}