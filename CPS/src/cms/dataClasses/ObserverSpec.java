package cms.dataClasses;

import java.util.ArrayList;

import cms.dataClasses.PMap.PMapSpec;
import cms.dataClasses.PMap.PMapSpecNode;
import wFramework.base.Context;
import wFramework.base.configuration.Configuration;
import wFramework.base.work.ClassSpec;
import wFramework.base.work.Spec;

public class ObserverSpec implements Spec {
	private static PMapSpec validator = new PMap.PMapSpec(
			new PMapSpecNode[]{	
			// Check for null situation
			new PMap.PMapSpecNode("url", 			new ClassSpec(String.class)),
			new PMap.PMapSpecNode("tags", 			new ClassSpec(ArrayList.class)), 
			new PMap.PMapSpecNode("showAllInfo", 	new ClassSpec(Boolean.class))
			});
	
	
	@Override
	public boolean validate(Object target, Context ctx, Configuration config) {
		return validator.validate(target, ctx, config);
	}

}
