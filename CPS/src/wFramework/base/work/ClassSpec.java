package wFramework.base.work;

import wFramework.base.Context;
import wFramework.base.configuration.Configuration;

// a convenience spec
// requres instance to be of a specific type
public class ClassSpec implements Spec{
	private final Class type;
	public ClassSpec(Class type){
		this.type = type;
	}
	public boolean validate(Object o, Context ctx, Configuration config){
		return type.isInstance(o);
	}
}