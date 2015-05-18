package cms.dataClasses;

import java.util.Map;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.Spec;

public class IncidentInDBSpec implements Spec {

	@Override
	public boolean validate(Object target, Context ctx, Configuration config) {
		// TODO of course we could do a more thorough validation
		// but that is not of high priority, anyway
		if(!(target instanceof Map))
			return false;
		if(!((Map)target).containsKey("_id"))
			return false;
		return true;
	}

}
