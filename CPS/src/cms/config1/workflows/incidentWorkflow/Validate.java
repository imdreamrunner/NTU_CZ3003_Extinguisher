package cms.config1.workflows.incidentWorkflow;

import java.util.Map;

import misc.TmpOut;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;
import wFramework.base.work.CompletionStatus.CompletionFlag;
import cms.dataClasses.AuthenSpec;
import cms.dataClasses.Errors;
import cms.dataClasses.InputMapSpec;
import cms.dataClasses.ValidatedIncidentSpec;
import cms.serviceInterfaces.AuthenticationService;
import cms.serviceInterfaces.ErrorService;
import cms.serviceInterfaces.NetworkService;

public class Validate extends Work {

	@Override
	public String getName() {
		return "validate";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "validation failed.";
	}
	
	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{InputMapSpec.class};
	}
	
	@Override
	public Class[] getGuaranteedProducts(){
		return new Class[]{ValidatedIncidentSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		ErrorService es = (ErrorService)config.getService(ErrorService.class);
		/*
		 * below is some old stuff for testing communication.
		NetworkService ns =(NetworkService)config.getService(NetworkService.class);
		TmpOut.pln("trying to send request from within Validate");
		try{
			ns.sendRequest("http://localhost:8080/test1", "sending stuffs from within Validate work");
		}catch(Exception e){
			e.printStackTrace();
		}
		TmpOut.pln("request sent, from within Validate");
		*/
		/*
		 * 1. grab input map
		 * 2. check "incident" exists in input map
		 * 3. remove some keys in incident
		 * 4. validate incident
		 */
		// grab input map
		Map<String, Object> map = (Map<String, Object>) ctx.getUnnamed(InputMapSpec.class);
		if(!map.containsKey("incident")){
			es.reportError(Errors.newValidationError("incident information not found in this request"),
					ctx, config);
			return new CompletionStatus(CompletionFlag.Failure);
		}
		// here's the map we want to validate
		Map<String, Object>inciMap = (Map<String, Object>)map.get("incident");
		// we drop some keys first before we run to validation
		if(inciMap.containsKey("_id"))
			inciMap.remove("_id");
		if(inciMap.containsKey("timeStamp"))
			inciMap.remove("timeStamp");
		if(inciMap.containsKey("operator"))
			inciMap.remove("operator");
		if(inciMap.containsKey("isLatest"))
			inciMap.remove("isLatest");
		// now we run the validation
		ValidatedIncidentSpec validatedIncidentSpec = new ValidatedIncidentSpec();
		try{
			if(!validatedIncidentSpec.validate(inciMap, ctx, config)){ 
				es.reportError(Errors.newValidationError(), ctx, config);
				return new CompletionStatus(CompletionFlag.Failure);
				}
			}catch(Exception e){
				es.reportError(Errors.newValidationError(), ctx, config);
				return new CompletionStatus(CompletionFlag.Failure);
				}
		// nothing weird happens, report success
		ctx.putUnnamed(ValidatedIncidentSpec.class, inciMap);
	
		return null;
	}

}
