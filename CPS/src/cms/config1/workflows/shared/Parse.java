package cms.config1.workflows.shared;

import java.util.HashMap;
import java.util.Map;
import cms.dataClasses.PMap;
import cms.dataClasses.InputMapSpec;
import cms.serviceInterfaces.*;
import wFramework.base.*;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.*;

public class Parse extends Work{

	@Override
	public Class[] getGuaranteedProducts(){
		return new Class[]{InputMapSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		NetworkService ns = (NetworkService) config.getService(NetworkService.class);
		String input = ns.getRequestBody(ctx);
		Map<String, Object> inputMap;
		try{
			inputMap = PMap.fromString(input);
		}catch(Exception e){
			// report error and break
			ErrorService es = (ErrorService)config.getService(ErrorService.class);
			es.reportError(cms.dataClasses.Errors.newParseError(), ctx, config);
			return new CompletionStatus(e);
		}
		// we have guaranteed to put an InputMap into context, now is time to do it
		ctx.putUnnamed(InputMapSpec.class, inputMap);
		//Map<String, Object> outputMap = new HashMap<String, Object>();
		//ctx.putUnnamed(OutputMapSpec.class, outputMap);
		
		// null is success
		return null;
	}

	@Override
	public String getName() {
		return "parse";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "input to CPS server cannot be parsed.";
	}

}
