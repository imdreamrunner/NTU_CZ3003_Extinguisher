package wFramework.base.work;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;


// Each WorkElement would extend the Work class
// Each WorkGroup is a collection of Work objects
/*
 * Each Work is a piece of executable code with precondition, postcondition
 * Each WorkGroup, on the other hand, is a mere collection of Work or WorkGroup objects
 * WorkGroup needs to be run by a WorkSupervisor
 * WorkGroup, when created, is associated with a WorkSupervisor
 * The WorkSupervisor supervising this WorkGroup asks the specified WorkSupervisor to run this WorkGroup
 */

public abstract class Work {
	public abstract String getName();
	public abstract String getDefaultFailureMessage();
	
	// this Work will assume the existence of a set of WorkProduct in context.
	// If there is no guarantee that the specified workproducts will be available by the time
	// this work runs, this work should not run at all
	public Class[] getRequiredProducts(){
		return new Class[]{};
	}
	
	// WorkProduct that this piece of Work is guaranteed to produce, so that Works that follow
	// can use
	public Class[] getGuaranteedProducts(){
		return new Class[]{};
	}
	
	public abstract CompletionStatus runInContext(Context ctx, Configuration config); 
}

