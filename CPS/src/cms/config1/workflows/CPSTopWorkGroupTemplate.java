package cms.config1.workflows;

import cms.dataClasses.Errors;
import cms.serviceInterfaces.ErrorService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.CompletionStatus.CompletionFlag;
import wFramework.base.work.Work;
import wFramework.base.work.WorkGroup;

public class CPSTopWorkGroupTemplate extends WorkGroup{

	private String name, failMsg;
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getDefaultFailureMessage(){
		return failMsg;
	}
	
	private Work parse, process, respond;
	
	public CPSTopWorkGroupTemplate(String name, Work parse, Work process, Work respond,
			String failMsg){
		this.name = name;
		this.parse = parse;
		this.process = process;
		this.respond = respond;
		this.failMsg = failMsg;
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		/*
		 * 1. child no error
		 * 2. child got error
		 * 	a. exception: report error as system error, use Work's default failure message
		 * 	b. failure flag
		 * 		i. no existing error: report user error with no specType, only default message
		 * 		ii. errors already exist: no need to report error
		 */
		// run parse and process first
		
		try{
			CompletionStatus result;
			ErrorService es = (ErrorService)config.getService(ErrorService.class);
			Work sequence[] = new Work[]{parse, process};
			int i = 0;
			try{
				for(i = 0; i<sequence.length; i++){
					result = sequence[i].runInContext(ctx, config);
					if(result!=null && result.getFlag()!=CompletionFlag.Success){
						if(!es.hasErrors(ctx, config))
							es.reportError(Errors.newSystemError(sequence[i].getDefaultFailureMessage()),ctx, config);
						// fall back to RESPONSE POINT directly
						break;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				if(!es.hasErrors(ctx, config))
					es.reportError(Errors.newSystemError(sequence[i].getDefaultFailureMessage()), ctx, config);
				// fall back to RESPONSE POINT directly
			}
			respond.runInContext(ctx, config);
			return null;
		}catch(Exception e){
			// irrecoverable error occured
			// TODO we can write to a pre-defined stream instead of stdout
			e.printStackTrace();
			return null;
		}
	}

}
