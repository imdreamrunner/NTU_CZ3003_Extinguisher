package cms.dataClasses;

import cms.serviceInterfaces.ErrorService.Error;

public class Errors {
	public static final String success = "success";
	public static final String internalError="internalErr";
	public static final String externalError="externalErr";
	public static Error newParseError(){
		return new Error(externalError, "parse", "Input to system cannot be correctly parsed.");
	}
	public static Error newSystemError(){
		return new Error(internalError, "unknown", "CPS system error occured. Request cannot be completed.");
	}
	public static Error newSystemError(String msg){
		return new Error(internalError, "unknown", msg);
	}
	public static Error newAuthenticationError(){
		return newAuthenticationError("Invalid operator account.");
	}
	public static Error newAuthenticationError(String msg){
		return new Error(externalError, "authentication", msg);
	}
	public static Error newQueryError(){
		return new Error(externalError,"query","Query cannot be performed.");
	}
	public static Error newValidationError(){
		return new Error(externalError, "validaion", "Validation of incident failed.");
	}
	public static Error newValidationError(String msg){
		return new Error(externalError, "validaion", msg);
	}
	public static Error newDBSaveError(){
		return newDBSaveError("Failed to save incident into database.");
	}
	public static Error newDBSaveError(String msg){
		return new Error(internalError, "DBSave", msg);
	}
	public static Error newObsRegError(String msg){
		return new Error(externalError, "observer-registration", msg);
	}
	public static Error newObsRegError(){
		return newObsRegError("registration of observer failed.");
	}
}
