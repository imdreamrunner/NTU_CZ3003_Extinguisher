package cms.config1.workflows.incidentWorkflow;

import java.util.Map;

import cms.dataClasses.ValidatedIncidentSpec;
import cms.serviceInterfaces.NotifierService;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;

public class Tag extends Work {

	@Override
	public String getName() {
		return "incident tagging";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "incident tagging failed";
	}
	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{ValidatedIncidentSpec.class};
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		/*
		 * currently our tags are rather simple
		 * 1. type name is a tag name
		 * 2. Leveli, where i is the integer level value, is a tag
		 * 3. "any": is a tag applied to all incidents 
		 */
		Map<String, Object> validInc = (Map)ctx.getUnnamed(ValidatedIncidentSpec.class);
		NotifierService ns = (NotifierService)config.getService(NotifierService.class);
		String tag1 = (String)validInc.get("type");
		String tag2 = "level"+(Integer)validInc.get("level");
		ns.addTag(tag1, ctx, config);
		ns.addTag(tag2, ctx, config);
		ns.addTag("any", ctx, config);
		/*
		Map<String, Object> outMap = (Map<String, Object>) ctx.getUnnamed(cms.dataClasses.OutputMapSpec.class);
		outMap.put("debuginfo", "tagger is invoked!");
		*/
		return null;
	}

}
