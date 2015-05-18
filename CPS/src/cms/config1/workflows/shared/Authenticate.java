package cms.config1.workflows.shared;

import java.util.Map;

import cms.dataClasses.AuthenSpec;
import cms.dataClasses.Errors;
import cms.dataClasses.InputMapSpec;
import cms.dataClasses.PMap;
import cms.dataClasses.PMap.PMapSpec;
import cms.dataClasses.PMap.PMapSpecNode;
import cms.serviceInterfaces.ErrorService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Spec;
import wFramework.base.work.Work;
import wFramework.base.work.CompletionStatus.CompletionFlag;
import cms.serviceInterfaces.*;
public class Authenticate extends Work {

	@Override
	public String getName() {
		return "authenticate";
	}
	
	@Override
	public Class[] getRequiredProducts(){
		return new Class[]{InputMapSpec.class};
	}

	private static PMapSpec inputOperatorMapSpec = new PMapSpec(new PMapSpecNode[]{
			new PMapSpecNode("username", PMap.stringSpec),
			new PMapSpecNode("password", PMap.stringSpec)
		});
	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		// grab input map
		Map<String, Object> map = (Map<String, Object>) ctx.getUnnamed(InputMapSpec.class);
		int x = 0;
		// this is weird... but we're mimicking the behaviour of goto statement
		// we use break statement to goto GOTO_HEAD
		while(x++==0){
			if(!map.containsKey("operator")) break;

			Map<String, Object> operatorMap = (Map<String, Object>) map.get("operator");
			if(!inputOperatorMapSpec.validate(operatorMap, ctx, config)) break;

			AuthenticationService as = (AuthenticationService)config.getService(AuthenticationService.class);
			String id = (String)operatorMap.get("username");
			String pwd= (String)operatorMap.get("password");
			try{
				if(!as.authenticate(id, pwd, ctx, config)) break;
			}catch(Exception e){break;}
			// nothing weird happens, report success
			ctx.putUnnamed(AuthenSpec.class, null);
			return null;
		}
		// GOTO_HEAD
		ErrorService es = (ErrorService)config.getService(ErrorService.class);
		es.reportError(Errors.newAuthenticationError(), ctx, config);
		return new CompletionStatus(CompletionFlag.Failure);
	}

	@Override
	public String getDefaultFailureMessage() {
		return "authentication failed.";
	}

}
