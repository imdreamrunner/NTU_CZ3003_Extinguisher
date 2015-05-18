package cms.dataClasses.oldStuff;



public class ErrorReport{
	/*
	 * FIXME We aren't using ErrorReport to do anythign yet
	 * By right we'd like to use ErrorReport to form a chain of error report, from the lowest-level
	 * Work all the way up to the top-level Work. We give each Work the opportunity to decide what 
	 * error exactly to report.
	 * 
	 * But right now we aren't dealing with this yet. We are just using a simpler mechanism, 
	 * CompletionStatus to represent whether a work completed successfully.
	 * 
	 * If we want to report some problem to the requester, we need to make a direct call to the
	 * error service directly within the Work we do
	 */
	
	/*
	public enum Status {Success, Failure, Custom}

	// completion flag: success, failure, of custom
	private Status status;
	// caught by supervisor when Work isn't handling its own problem
	private final Exception e;
	// used to report a state that is neither success nor failure
	private final Object customObj;
	// used to report failure. represents the root ErrorReport that triggers this chain
	private final ErrorReport rootCause;
	// the Work that failed
	private final Work problemWork;
	// Error report at a loewr hierarchy
	private final ErrorReport cause;
	// some more detailed error information
	// we aren't using one class per error type, so that's why we use strings
	private final String errorType;
	private final String errorMsg;
	
	// success constructor
	public ErrorReport(){
		this.e = null;
		this.customObj = null;
		this.rootCause = null;
		this.problemWork = null;
		this.cause = null;
		this.errorType = null;
		this.errorMsg = null;
		this.status = Status.Success;
	}
	
	public ErrorReport (Exception e){

		this.e = e;
		this.customObj = null;
		this.rootCause = null;
		this.problemWork = null;
		this.cause = null;
		this.errorType = null;
		this.errorMsg = null;
		this.status = Status.Failure;
	}
	
	public static ErrorReport newChainedReport(Status status, Work problemWork, 
			ErrorReport cause, String type, String msg){
		return new ErrorReport(status, null, null, cause.getRootCause(), 
				problemWork, cause, type, msg);
	}
	
	// complete constructor
	public ErrorReport(Status status, Exception e, Object customObj,
			ErrorReport rootCause, Work problemWork, ErrorReport cause,
			String errorType, String errorMsg) {
		this.status = status;
		this.e = e;
		this.customObj = customObj;
		this.rootCause = rootCause;
		this.problemWork = problemWork;
		this.cause = cause;
		this.errorType = errorType;
		this.errorMsg = errorMsg;
	}

	// constructor for custom status
	public ErrorReport(Status status, Object customObj) {
		this.status = status;
		this.e = null;
		this.customObj = customObj;
		this.rootCause = null;
		this.problemWork = null;
		this.cause = null;
		this.errorType = null;
		this.errorMsg = null;
	}

	public Status getStatus() {return status;}
	public Exception getException() {return e;}
	public Object getCustomObj(){return this.customObj;}
	public ErrorReport getRootCause() {	return rootCause;}
	public Work getProblemWork() {	return problemWork;}
	public ErrorReport getCause() {	return cause;}
	public String getErrorType() {	return errorType;}
	public String getErrorMsg() {	return errorMsg;}
	*/
}

/*
*/