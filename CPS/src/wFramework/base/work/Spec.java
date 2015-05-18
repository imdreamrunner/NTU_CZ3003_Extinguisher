package wFramework.base.work;

import wFramework.base.*;
import wFramework.base.configuration.Configuration;

// WorkProduct specification
public interface Spec {
	public boolean validate(Object target, Context ctx, Configuration config);
}
