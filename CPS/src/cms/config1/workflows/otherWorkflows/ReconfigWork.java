package cms.config1.workflows.otherWorkflows;

import java.util.Map;

import cms.dataClasses.Errors;
import cms.dataClasses.InputMapSpec;
import cms.serviceInterfaces.*;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.service.SystemService;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;

public class ReconfigWork extends Work{

	@Override
	public String getName() {
		return "reconfiguration";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "reconfiguration failed, check system log for more details.";
	}
	
	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{InputMapSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		ErrorService es = (ErrorService)config.getService(ErrorService.class);
		Map<String, Object> inputMap = (Map)ctx.getUnnamed(InputMapSpec.class);
		while(true){
			if(!inputMap.containsKey("command"))break;
			SystemService ss = (SystemService)config.getService(SystemService.class);
			// TODO validate command is string
			String command = (String)inputMap.get("command");
			boolean result = ss.loadNewConfig(command);
			if(!result) break;
			return null;
		}
		es.reportError(Errors.newSystemError("Reconfiguration failed. Please check system log for details"),
				ctx, config);
		return new CompletionStatus(CompletionStatus.CompletionFlag.Failure);
	}

}
