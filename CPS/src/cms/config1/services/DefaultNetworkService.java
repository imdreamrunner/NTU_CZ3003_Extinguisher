package cms.config1.services;

import java.io.IOException;

import misc.TmpOut;


import wFramework.base.Context;
import wFramework.base.service.Service;
import wFramework.base.service.SystemService;

import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import javaxt.http.servlet.ServletException;

import cms.serviceInterfaces.NetworkService;
import javaxt.http.*;
import javaxt.http.servlet.*;
import com.joejernst.http.*;

public class DefaultNetworkService implements  NetworkService{

	@Override
	public String getVersion() {
		return "0";
	}

	@Override
	public boolean canReplace(String version) {
		// can replace nothing
		return false;
	}


	private javaxt.http.Server server;
	private class NetworkContext{
		public HttpServletRequest request;
		public HttpServletResponse response;
		public boolean alreadyResponded = false;
		public NetworkContext(HttpServletRequest request, HttpServletResponse response){
			this.request = request;
			this.response = response;
		}
	}
	private class RequestHandler extends HttpServlet {
        public void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException {
        	
        	/*
        	String result = "Hello, the time is now " + new java.util.Date();
        	result += "<br />";
        	result += request.getURL().getPath();
            response.write(result);
             *
             * steps:
             * 1. build initial Context
             * 2. grab URL path as workName
             * 3. invoke system service to launch top-level work
             * 
             */
        	System.out.println("request received: "+request.getPath());
        	NetworkContext nc = new NetworkContext(request, response);
        	Context ctx = new Context();
        	ctx.putUnnamed(ctxKey, nc);
        	
        	String name = request.getPath();
        	
        	systemService.launchWorkflow(name, ctx);
        }
    }
	private Object ctxKey;
	@Override
	public boolean startup() {
		TmpOut.pln("network service starting...");
		server = new javaxt.http.Server(8080, 2, new RequestHandler());
		server.start();
		ctxKey = new Object();
		return true;
	}

	@Override
	public void shutdown() {
		// guess what? javaxt.http.Server has no way of stopping!
	}

	@Override
	public boolean transitToThisStarts(Service oldService) {
		// TODO what the hell does this mean?
		return true;
	}

	@Override
	public void transitToThisEnds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transitToAnotherEnds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listenAt(int port, Object handler, int numOfThreads) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopListen(int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRequestURL(Context ctx) {
		NetworkContext nc = (NetworkContext) ctx.getUnnamed(ctxKey);
		return nc.request.getURL().toString();
	}

	@Override
	public String getRequestBody(Context ctx) {
		NetworkContext nc = (NetworkContext) ctx.getUnnamed(ctxKey);
		try{
			return new String(nc.request.getBody(), "UTF-8");
		}catch(Exception e){
			return "";
		}
	}

	@Override
	public void respond(Context ctx, String response) {
		TmpOut.pln("network trying to respond...");
		NetworkContext nc = (NetworkContext) ctx.getUnnamed(ctxKey);
		if(nc.alreadyResponded) return;
		synchronized(nc){
			nc.alreadyResponded = true;
			try{
				nc.response.write(response);
			}catch(Exception e){}
		}
	}

	private SystemService systemService;
	@Override
	public void receiveSystemServiceHandler(SystemService service) {
		systemService = service;		
	}

	@Override
	public String sendRequest(String url, String message) throws IOException {
		return (new Request(url).setBody(message).postResource()).getBody();		
	}

}
