package wFramework.base.work;


/*
 * State report:
 * A Work needs to be able to report to its supervisor whether or not itself is correctly
 * completed. If it encounters error half-way, it can raise an exception, or, it can simply return
 * an object representing its state of completion.
 */
public class CompletionStatus {

	public enum CompletionFlag {Success, Failure, Custom}
	private final CompletionFlag flag;
	private final Exception e;
	private final Object customFlag;
	
	// success constructor
	public CompletionStatus(){
		this.flag = CompletionFlag.Success;
		this.e = null;
		this.customFlag = null;
	}
	// exception constructor
	public CompletionStatus(Exception e){
		this.flag = CompletionFlag.Failure;
		this.e = e;
		this.customFlag = null;
	}
	// simple failure constructor
	public CompletionStatus(CompletionFlag flag){
		// we can't have custom flag without custom object, can we??
		assert (flag == CompletionFlag.Failure) || (flag == CompletionFlag.Success); 
		this.flag = flag;
		this.e = null;
		this.customFlag = null;
	}
	// general constructor
	public CompletionStatus(CompletionFlag flag, Exception e, Object cust){
		this.flag = flag;
		this.e = e;
		this.customFlag = cust;
	}
	public CompletionFlag getFlag() {return flag;}
	public Exception getException() {return e;}
	public Object getCustomFlag() {	return customFlag;}
}
