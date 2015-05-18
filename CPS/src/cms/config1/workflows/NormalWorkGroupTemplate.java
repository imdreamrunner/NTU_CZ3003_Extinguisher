package cms.config1.workflows;

import cms.dataClasses.Errors;
import cms.serviceInterfaces.ErrorService;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.CompletionStatus;
import wFramework.base.work.Work;
import wFramework.base.work.WorkGroup;
import wFramework.base.work.CompletionStatus.CompletionFlag;

public class NormalWorkGroupTemplate extends WorkGroup{

	private String name, defaultErrMsg;
	private Work[] sequence;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	public NormalWorkGroupTemplate(String name, Work[] sequence, String defaultFailureMsg){
		this.name  = name;
		this.sequence = sequence;
		this.defaultErrMsg = defaultFailureMsg;
	}

	@Override
	public String getDefaultFailureMessage() {
		return defaultErrMsg;
	}

	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		ErrorService es = (ErrorService)config.getService(ErrorService.class);
		for(int i = 0; i<sequence.length; i++)
		{
			/*
			 * 1. child no error
			 * 2. child got error
			 * 	a. exception: report error as system error, use Work's default failure message
			 * 	b. failure flag
			 * 		i. no existing error: report user error with no specType, only default message
			 * 		ii. errors already exist: no need to report error
			 */
			try
			{
				CompletionStatus s = sequence[i].runInContext(ctx, config);
				// we assume empty state as success
				if(s==null || s.getFlag() == CompletionStatus.CompletionFlag.Success)
					continue;
				if(!es.hasErrors(ctx, config)){
					es.reportError(Errors.newSystemError(sequence[i].getDefaultFailureMessage()), ctx, config);
				}
				return new CompletionStatus(CompletionFlag.Failure);
			}
			catch(Exception e){
				e.printStackTrace();
				if(!es.hasErrors(ctx, config)){
					es.reportError(Errors.newSystemError(sequence[i].getDefaultFailureMessage()), ctx, config);
				}
				return new CompletionStatus(CompletionFlag.Failure);
			}
		}
		return new CompletionStatus(CompletionFlag.Success);
	}

}
