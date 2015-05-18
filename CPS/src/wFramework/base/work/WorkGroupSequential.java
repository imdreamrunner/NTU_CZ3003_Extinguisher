package wFramework.base.work;

import wFramework.base.*;
import wFramework.base.configuration.Configuration;


public class WorkGroupSequential extends WorkGroup
{

	/*
	 * this is for text-based configuration.
	 * we don't need it yet
	public WorkGroupConstructor SequentialCtor = new WorkGroupConstructor()
	{
		public WorkGroup construct(NamedObjContainer params){
			Work[] sequence = (Work[])params.get("sequence");
			WorkGroupErrHandler errHandler = 
				(WorkGroupErrHandler )params.get("err-handler");
			return new WorkGroupSequential(sequence, errHandler);
		}
	};
	*/
	
	private final String name;
	private final String defaultFailureMsg;
	private final Work[] sequence;
	private final WorkGroupErrHandler errHandler;
	/*
	 * private Work predicate;
	 * private Work[] sequence
	 */
	public WorkGroupSequential(String name, Work[] sequence, 
			WorkGroupErrHandler errHandler, String defaultFailureMsg){
		this.name  = name;
		this.sequence = sequence;
		this.errHandler = errHandler;
		this.defaultFailureMsg = defaultFailureMsg;
	}
	
	// FIXME should return ErrorReport instead of CompletionStatus
	private CompletionStatus handleError(CompletionStatus report, Work problemWork,
			Context ctx, Configuration config){
		if(errHandler == null)
			return report;
		return errHandler.handleErr(report, problemWork, ctx, config);
	}
	
	
	@Override
	public CompletionStatus runInContext(Context ctx, Configuration config) {
		for(int i = 0; i<sequence.length; i++)
		{
			/*
			 * Our error management strategy:
			 * 1. if work doesn't return anything or returns success, just continue
			 * 2. if errHandler isn't available, abort and report to supervisor
			 * 3. if errHandler is available, try and use it to handle
			 * 		if errHandler reports failure, abort and report to supervisor
			 * 		else continue and pretend nothing happened
			 */
			try
			{
				CompletionStatus s = sequence[i].runInContext(ctx, config);
				// we assume empty state as success
				if(s==null) continue;
				if(s.getFlag()!=CompletionStatus.CompletionFlag.Success)
				{
					// duplicate: ErrHandling_142372
					CompletionStatus handledResult = handleError(s, sequence[i], ctx, config);
					if(handledResult==null) continue;
					if(handledResult.getFlag() != CompletionStatus.CompletionFlag.Success)
						return handledResult;
						/*
						return ErrorReport.newChainedReport(Status.Failure, 
								sequence[i], handledResult, "", "");
								*/
				}
			}
			catch(Exception e){
				// duplicate: ErrHandling_142372
				CompletionStatus s = new CompletionStatus(e);
				CompletionStatus handledResult = handleError(s, sequence[i], ctx, config);
				if(handledResult==null) continue;
				if(handledResult.getFlag() != CompletionStatus.CompletionFlag.Success)
					return handledResult;
			}
		}
		return new CompletionStatus(CompletionStatus.CompletionFlag.Success);
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getDefaultFailureMessage(){
		return defaultFailureMsg;
	}
}