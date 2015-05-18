package cms.config1.workflows.otherWorkflows;

import misc.TmpOut;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.service.SystemService;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;
import cms.serviceInterfaces.NetworkService;

public class Test1 extends Work{

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		TmpOut.pln("test1 running");
		NetworkService ns = (NetworkService) config.getService(NetworkService.class);
		String reqBody = ns.getRequestBody(ctx);
		TmpOut.pln("test1 body: "+reqBody);

		ns.respond(ctx, "this is test work");
		return null;
	}

	@Override
	public String getName() {
		return "test1";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "";
	}
}
