package cms.serviceInterfaces;

import cms.dataClasses.*;
import wFramework.base.*;
import wFramework.base.configuration.Configuration;
import wFramework.base.service.Service;

public interface AuthenticationService extends Service{
	public boolean authenticate(String id, String pwd, Context ctx, Configuration config) throws Exception;
	public boolean hasPriviledge(String s, Context ctx, Configuration config);
	public String getOperatorId(Context ctx, Configuration config);
}
