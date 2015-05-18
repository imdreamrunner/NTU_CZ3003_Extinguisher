package cms.config1.workflows.incidentWorkflow;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import cms.dataClasses.Errors;
import cms.dataClasses.IncidentInDBSpec;
import cms.dataClasses.OutputDataSpec;
import cms.dataClasses.PMap;
import cms.dataClasses.ValidatedIncidentSpec;
import cms.serviceInterfaces.AuthenticationService;
import cms.serviceInterfaces.DatabaseService;
import cms.serviceInterfaces.ErrorService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;

public class DBWriteBack extends Work{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultFailureMessage() {
		// TODO Auto-generated method stub
		return null;
	} 

	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{ValidatedIncidentSpec.class};
	}
	
	@Override
	public Class[] getGuaranteedProducts(){
		return new Class[]{OutputDataSpec.class, IncidentInDBSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {

		DatabaseService ds = (DatabaseService)config.getService(DatabaseService.class);
		DBCollection incCol = ds.getDBCollection("incident");
		ErrorService es = (ErrorService) config.getService(ErrorService.class);
		
		/*
		 * 0. clone ValidatedIncident
		 * 1. put timeStamp=Date() into clone
		 * 2. put Operator into clone
		 * 2.1. put isLatest=true into clone
		 * 2.2. set isLatest=false for previous item 
		 * 3. insert map into db.incident.insert(cloned map)
		 * 4. get the saved document from db
		 * 5. patch initialId = _id.$oid if initialId==null
		 * BasicDBObject doc = new BasicDBObject( "name", "Matt" );
		 * collection.insert( doc );
		 * ObjectId id = (ObjectId)doc.get( "_id" );
		 */
		Map<String, Object> valiInc = (Map)ctx.getUnnamed(ValidatedIncidentSpec.class);
		// 0. clone
		Map<String, Object> inciInDB = PMap.shallowCopy(valiInc);
		// 1 and 2
		String opId;
		AuthenticationService as = (AuthenticationService)config.getService(AuthenticationService.class);
		opId = as.getOperatorId(ctx, config);
		Date now = new Date();
		inciInDB.put("operator", opId);
		inciInDB.put("timeStamp", now);
		// 2.1
		inciInDB.put("isLatest", true);
		// 2.2
		String initialId = (String)inciInDB.get("initialId");
		if(initialId!=null){
			/*
			 * when initialId is not null, means a previous version has isLatest=true
			 * we need to set that to false
			 * we first look for an existing incident with "initialId" : initialId and isLatest:true
			 * if it is not found, it means that the the current inciInDB is the first update ot the
			 * initial incident. We then look for an incident with "_id": initialId
			 * 
			 * if even this cannot be found, we raise an error
			 * if it is found, we just set its isLatest = false
			 */
			// query for initialId = initialId and isLatest = true
			DBObject query = new BasicDBObject().append("initialId", initialId).append("isLatest", true);
			DBObject previous = incCol.findOne(query);
			/*
			if(previous == null){
				query = new BasicDBObject().append("_id", new ObjectId(initialId));
				DBObject initial = incCol.findOne(query);
				previous = initial;
			}
			*/
			if(previous==null){
				es.reportError(Errors.newDBSaveError("A previous version of the incident with the specified initialId cannot be found."), ctx, config);
				return new CompletionStatus(CompletionStatus.CompletionFlag.Failure);
			}
			previous.put("isLatest", false);
			incCol.save(previous);
		}else{
			/*
			 * When initialId is null, we need to get its _id, and put it as its own initialId
			 * this is done from within step 3 by looking at initialId==null
			 */
		}
		// 3,4
		DBObject saveObj;
		try{
			saveObj =PMap.toDBObject(inciInDB); 
			incCol.insert(saveObj);
			// 5
			if(initialId == null){
				String newId = saveObj.get("_id").toString();
				saveObj.put("initialId", newId);
				incCol.save(saveObj);
			}
			inciInDB = PMap.toMap(saveObj);

			PMap.trimIncident(inciInDB, true);
		}catch(Exception e){
			es.reportError(Errors.newDBSaveError(), ctx, config);
			return new CompletionStatus(CompletionStatus.CompletionFlag.Failure);
		}
		// now we have a full IncidentInDB object, we put it in output map, and IncidentInDB
		ctx.putUnnamed(IncidentInDBSpec.class, inciInDB);
		// FIXME OutputMap format needs fixing
		ctx.putUnnamed(OutputDataSpec.class, inciInDB);
		return null;
	}

}
