package wFramework.base.work;
import wFramework.base.*;
import wFramework.base.configuration.*;
// A WorkProduct conforming to a specification
public class WorkProduct<T extends Spec> {
	private Object product;
	private T spec;
	private boolean validated = false;
	public WorkProduct(Object product, T spec){
		this.product = product;
		this.spec = spec;
	}
	public Object getProduct(){return product;}
	public boolean isValidated(){return validated;}
	public boolean validate(Context ctx, Configuration config){
		validated = spec.validate(product, ctx, config);
		return validated;
	}
}
