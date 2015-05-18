package cms.config1.workflows.otherWorkflows;

import java.util.ArrayList;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import cms.dataClasses.AuthenSpec;
import cms.dataClasses.Errors;
import cms.dataClasses.InputMapSpec;
import cms.dataClasses.ObserverSpec;
import cms.serviceInterfaces.AuthenticationService;
import cms.serviceInterfaces.DatabaseService;
import cms.serviceInterfaces.ErrorService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;

public class ObsRegWork extends Work {

	@Override
	public String getName() {
		return "observer registration";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "observer registration failed";
	}
	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{AuthenSpec.class, InputMapSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		/*
		 * 1. check operator has priviledge to create observers
		 * 2. validate observer
		 * 3. insert observer
		 */
		// 1.
		AuthenticationService as = (AuthenticationService)config.getService(AuthenticationService.class);
		ErrorService es = (ErrorService)config.getService(ErrorService.class);
		if(!as.hasPriviledge("observer.create", ctx, config)){
			es.reportError(Errors.newAuthenticationError("This operator cannot create observers."), ctx, config);
			return new CompletionStatus(CompletionStatus.CompletionFlag.Failure);
		}
		// 2.
		Map<String, Object> inputMap = (Map)ctx.getUnnamed(InputMapSpec.class);
		while(true)
		{
			if(!inputMap.containsKey("observer"))break;
			Map<String, Object> observer = (Map)inputMap.get("observer");
			ObserverSpec obSpec = new ObserverSpec();
			if(!obSpec.validate(observer, ctx, config))break;
			
			// 3.
			DatabaseService ds = (DatabaseService)config.getService(DatabaseService.class);
			DBCollection obsCol = ds.getDBCollection("observer");
			String url = (String)observer.get("url");
			BasicDBList tags = new BasicDBList();
			tags.addAll((ArrayList)observer.get("tags"));
			boolean showAllInfo = (boolean)observer.get("showAllInfo");
			obsCol.insert(new BasicDBObject
					("url", url)
				.append("tags", tags)
				.append("showAllInfo", showAllInfo));
			return null;
		}
		es.reportError(Errors.newObsRegError("observer information missing or incorrect."), ctx, config);
		return new CompletionStatus(CompletionStatus.CompletionFlag.Failure);
	}

}
