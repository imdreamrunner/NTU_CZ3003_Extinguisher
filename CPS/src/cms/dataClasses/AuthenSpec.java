package cms.dataClasses;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.Spec;

public class AuthenSpec implements Spec {

	// we don't do any validation for this
	// authentication is actually managed by AuthenticationService
	// we use this product to signal that indeed authentication has passed
	// but if we want detailed information regarding the authentication, we'd have to ask
	// the authentication service
	@Override
	public boolean validate(Object target, Context ctx, Configuration config) {
		return true;
	}

}
