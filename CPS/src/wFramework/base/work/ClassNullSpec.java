package wFramework.base.work;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;

public class ClassNullSpec implements Spec {
		private final Class type;
		public ClassNullSpec(Class type){
			this.type = type;
		}
		public boolean validate(Object o, Context ctx, Configuration config){
			return (o==null)||type.isInstance(o);
		}
}
