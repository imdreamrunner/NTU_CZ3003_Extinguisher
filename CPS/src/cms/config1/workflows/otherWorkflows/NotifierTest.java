package cms.config1.workflows.otherWorkflows;

import cms.serviceInterfaces.NotifierService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;

public class NotifierTest extends Work {

	@Override
	public String getName() {
		return "notifier test";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "notifier test failed";
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		NotifierService nots = (NotifierService)config.getService(NotifierService.class);
		nots.addTag("testtag", ctx, config);
		return null;
	}

}
