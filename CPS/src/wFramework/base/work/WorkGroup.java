package wFramework.base.work;

import wFramework.base.Context;
import wFramework.base.NamedObjContainer;
import wFramework.base.configuration.Configuration;


public abstract class WorkGroup extends Work{
	/*
	 * each type of WorkGroup requires a WorkGroupConstructor to accept a list of Works
	 * and return a WorkGroup object of specified type
     *
	 * When we register a WorkGroup type, we need "WorkGroupTypeName" and "WorkGroupConstructor"
	 * so that later each registered WorkGroup would have a WorkGroupType, and can be constructed
	 * simply using the corresponding constructor
	 * 
	 * Each Wor
	 */
	
	//FIXME error handler should actually return ErrorReport instead of CompletionStatus
	public interface WorkGroupErrHandler{
		public CompletionStatus handleErr(CompletionStatus report, Work problemWork, 
				Context ctx, Configuration config);
	}
	
	public interface WorkGroupConstructor{
		public WorkGroup construct(NamedObjContainer params);
	}
	
}