package cms.config1.workflows.otherWorkflows;

import wFramework.base.Context;

import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Spec;
import wFramework.base.work.Work;
import cms.serviceInterfaces.*;

public class KillerWork extends Work {

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		NetworkService ns = (NetworkService)config.getService(NetworkService.class);
		ns.respond(ctx, "System being killed...");
		System.exit(0);
		return null;
	}

	@Override
	public String getName() {
		return "killer";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "failed to kill system";
	}
}
