package cms.config1.services;

import java.util.ArrayList;

import misc.TmpOut;

import wFramework.base.*;
import wFramework.base.configuration.Configuration;
import wFramework.base.service.Service;
import wFramework.base.service.SystemService;
import cms.dataClasses.Errors;
import cms.serviceInterfaces.*;

public class DefaultErrorService implements ErrorService{

	private Object key = new Object();
	private static class ErrorResource{
		public ArrayList<Error> errors;
		public boolean hasError = false;
		public ErrorResource(){
			this.errors = new ArrayList<Error>();
		}
	}
	// we don't really care, not for now anyways
	public void reportProblem(String s, Context ctx, Configuration config) {
		TmpOut.pln(s);
	}

	
	public void reportAlert(String s, Context ctx, Configuration config) {
		TmpOut.pln("!!!!!!!!!!!!Alert Coming!!!!!!!!!!!!");
		TmpOut.pln(s);
		TmpOut.pln("============ Alert Ends ============");
	}

	private ErrorResource getER(Context ctx){
		ErrorResource er;
		if(!ctx.hasUnnamed(key)){
			er = new ErrorResource();
			ctx.putUnnamed(key, er);
		}else
			er = (ErrorResource) ctx.getUnnamed(key);
		return er;
	}
	public void reportError(Error err, Context ctx, Configuration config) {
		ErrorResource er = getER(ctx);
		er.errors.add(err);
		er.hasError = true;
	}

	public Error[] getAllErrors(Context ctx, Configuration config) {
		if(!ctx.hasUnnamed(key))
			return new Error[]{};
		return ((ErrorResource)ctx.getUnnamed(key)).errors.toArray(new Error[]{});
	}

	@Override
	public void clearErrors(Context ctx, Configuration config) {
		if(!ctx.hasUnnamed(key))
			return;
		((ErrorResource)ctx.getUnnamed(key)).errors.clear();
		getER(ctx).hasError = false;
	}

	@Override
	public void setErrorWithoutDetails(Context ctx, Configuration config) {
		getER(ctx).hasError = true;
	}
	@Override
	public boolean hasErrors(Context ctx, Configuration config){
		return getER(ctx).hasError;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canReplace(String version) {
		// TODO Auto-generated method stub
		return false;
	}

	// this service is stateless, so we don't really do much
	// if error report has to use fixed system resource (file), we might consider
	// giving it some state... but not now
	@Override
	public boolean startup() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean transitToThisStarts(Service oldService) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void transitToThisEnds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transitToAnotherEnds() {
		// TODO Auto-generated method stub
		
	}

	private SystemService systemService;
	@Override
	public void receiveSystemServiceHandler(SystemService service) {
		systemService = service;		
	}
}
