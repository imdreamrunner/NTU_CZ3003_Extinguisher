package cms.config1.workflows.incidentWorkflow;

import java.util.Map;

import cms.dataClasses.IncidentInDBSpec;
import cms.dataClasses.ValidatedIncidentSpec;
import cms.serviceInterfaces.NotifierService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;

public class Notify extends Work{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultFailureMessage() {
		return "notification failed";
	}
	
	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{ValidatedIncidentSpec.class, IncidentInDBSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		NotifierService ns = (NotifierService)config.getService(NotifierService.class);
		//Map<String, Object> inc = (Map) ctx.getUnnamed(ValidatedIncidentSpec.class);
		// enqueue has an implicit dependency on IncidentInDBSpec... this is bad.. but, nevermind
		ns.enque(ctx, config);
		return null;
	}

}
