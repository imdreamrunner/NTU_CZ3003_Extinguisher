package cms.serviceInterfaces;

import wFramework.base.service.*;
import wFramework.base.*;
import wFramework.base.configuration.*;

public interface ErrorService extends Service{
	// problem is something that can be ignored. that is, a situation occurs, but Work goes on
	// Alert is something that requires human attention, say notifier cannot reach recipient
	// Error is something that causes a workflow to fail
	public void reportProblem(String s, Context ctx, Configuration config);
	public void reportAlert(String s, Context ctx, Configuration config);
	public void reportError(Error err, Context ctx, Configuration config);
	
	// return all errors that have been reported
	public Error[] getAllErrors(Context ctx, Configuration config);
	
	// reset system's errors. returns to original state
	public void clearErrors(Context ctx, Configuration config);
	
	// report an anonymous error without giving any detailed information
	public void setErrorWithoutDetails(Context ctx, Configuration config);
	
	// check if errors have been reported
	// anonymous errors show up here, but not in getAllErrors()
	public boolean hasErrors(Context ctx, Configuration config);
	
	/*
	 * our error service is still a bit screwed
	 * we don't have a way to systematically trace errors to its origin.
	 * We rely on the error origin itself to report the error
	 * 
	 * Standard error handlnig:
	 * 1. report error
	 * 2. return CompletionStatus with Failure flag
	 * 
	 * System error:
	 * 1. Exception is thrown
	 * 1.5 errHandler couldn't handle it
	 * 2. Supervisor wraps Exception in an Error object and report it
	 * 
	 * This means that supervisor should report Error when something wrong happens
	 */
	
	public static class Error {
		protected String mainType;
		protected String specType;
		protected String msg;
		public Error(String mainType, String specType, String msg){
			this.mainType = mainType;
			this.specType = specType;
			this.msg = msg;
		}
		public String getMainType() {
			return mainType;
		}
		public String getSpecType() {
			return specType;
		}
		public String getMsg() {
			return msg;
		}	
	}
}
