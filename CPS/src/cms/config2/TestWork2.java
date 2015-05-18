package cms.config2;

import misc.TmpOut;
import wFramework.base.*;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;
import cms.serviceInterfaces.NetworkService;

public class TestWork2 extends Work{

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		TmpOut.pln("TestWork2 running: we're in config2");
		NetworkService ns = (NetworkService) config.getService(NetworkService.class);
		ns.respond(ctx, "this is test work 2: we're in config2");
		return null;
	}

	@Override
	public String getName() {
		return "test2";
	}

	@Override
	public String getDefaultFailureMessage() {
		return "";
	}
}
