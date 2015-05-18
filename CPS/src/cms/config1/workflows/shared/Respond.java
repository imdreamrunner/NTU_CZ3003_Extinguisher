package cms.config1.workflows.shared;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import cms.dataClasses.*;
import cms.serviceInterfaces.*;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.*;

public class Respond extends Work{

	@Override
	public String getName() {
		return "response";
	}
	
	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{OutputDataSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		try{
		/*
		 * 1. check error. if error exists, respond with error message
		 * 2. otherwise, respond using OutputMap
		 */
		NetworkService ns = (NetworkService)config.getService(NetworkService.class);
		ErrorService es = (ErrorService)config.getService(ErrorService.class);
		ErrorService.Error[] errs = es.getAllErrors(ctx, config);
		
		/*
		 * responseMap contains 2 key-value pairs
		 * error: {err object}
		 * data: outputData
		 */
		Map<String, Object> responseMap;
		Map<String, Object> errorMap=null;
		Object data=null;

		responseMap = new HashMap<String, Object>();
		
		if(es.hasErrors(ctx, config)){
			ErrorService.Error err;
			// no error is present, yet system has error
			// means we have an anonymous error
			if(errs.length==0)
				err = cms.dataClasses.Errors.newSystemError();
			else
			// we only report the last error
				err = errs[errs.length-1];
			/*
			 * {
			 * 		mainType: string
			 * 		specType: string
			 * 		msg:	  string 
			 * }
			 */
			errorMap = new HashMap<String, Object>();
			errorMap.put("mainType", err.getMainType());
			errorMap.put("specType", err.getSpecType());
			errorMap.put("msg", 	 err.getMsg());
		}
		if(ctx.hasUnnamed(OutputDataSpec.class))
			data = ctx.getUnnamed(OutputDataSpec.class);
		
		responseMap.put("error", errorMap);
		responseMap.put("data", data);
		
		String response = PMap.toString(responseMap);
		ns.respond(ctx, response);
		}catch(Exception e){
			// TODO proper error handling 
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getDefaultFailureMessage() {
		return "response could not be made.";
	}

}
