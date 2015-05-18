package cms.dataClasses;

import java.util.ArrayList;
import java.util.Map;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.Spec;

public class OutputDataSpec implements Spec {

	@Override
	public boolean validate(Object target, Context ctx, Configuration config) {
		return (target instanceof Map) || (target instanceof ArrayList);
	}
}
