package cms.serviceInterfaces;
import java.io.IOException;

import wFramework.base.Context;
import wFramework.base.service.Service;

public interface NetworkService extends Service{
	
	public interface RequestHandler{
		
	}
	// bind servlet to port, specify number of threads supported
	// used by SystemService only
	public void listenAt(int port, Object handler, int numOfThreads);
	
	// unbind servlet at port
	// used by SystemService only
	public void stopListen(int port);
	
	public String getRequestURL(Context ctx);
	public String getRequestBody(Context ctx);
	public void respond(Context ctx, String response);
	public String sendRequest(String url, String message) throws IOException;
}
