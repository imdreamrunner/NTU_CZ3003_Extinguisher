package cms.config1.workflows.otherWorkflows;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import cms.serviceInterfaces.NetworkService;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;

public class TestHostWork extends Work{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultFailureMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		try{
		  byte[] encoded = Files.readAllBytes(Paths.get("sandbox/test1.html"));
		  String ss = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
		  NetworkService ns = (NetworkService)config.getService(NetworkService.class);
		  ns.respond(ctx, ss);
		}catch(Exception e){e.printStackTrace();}
		  
		return null;
	}

}
