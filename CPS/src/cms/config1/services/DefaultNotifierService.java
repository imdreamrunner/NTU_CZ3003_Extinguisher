package cms.config1.services;

import java.util.*;
import java.util.Map.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import misc.TmpOut;

import cms.serviceInterfaces.*;
import cms.dataClasses.IncidentInDBSpec;
import cms.dataClasses.ValidatedIncidentSpec;
import cms.dataClasses.PMap;
import wFramework.base.*;
import wFramework.base.configuration.Configuration;
import wFramework.base.service.Service;
import wFramework.base.service.SystemService;

import com.mongodb.*;

public class DefaultNotifierService implements cms.serviceInterfaces.NotifierService{

	// private key to find stuffs
	private Object key = new Object();
	private Queue<IncidentTagsPair> queue;
	private Timer periodicTimer = new Timer(); 
	// in this function, we periodically check stuffs and do what we need
	private TimerTask periodicTask = new TimerTask(){
		public void run(){
			runNotification();
		}
	};
	
	private void runNotification(){
		/*
		 * these steps:
		 * 1. check if queue is empty. if yes return
		 * 2. dequeue ALL OF THEM
		 * 3. For each incident to be reported
		 * 		a. find all interested observers
		 * 		b. put {incident, tags} in observers' mail
		 * 4. Send mails to relevant observers
		 */
		//TmpOut.pln("notification check");
		if(queue.isEmpty()){
			//TmpOut.pln("no notification to send");
			return;
		}
		TmpOut.pln("have stuffs to send");
		// grab DB handle first
		DatabaseService dbs = (DatabaseService)systemService.
				getCurrentConfig().getService(DatabaseService.class);
		DBCollection obsCol = dbs.getDBCollection("observer");
		IncidentTagsPair itp = queue.poll();
		HashMap<String, ArrayList<IncidentTagsPair>> mails = new HashMap<String, 
				ArrayList<IncidentTagsPair>>();
		// dequeue one by one until empty, for each dequeued incident:
		while(itp != null){
			// find all interested observers
			TmpOut.pln("Incident with these tags: "+itp.tags.toString());
			BasicDBList list = new BasicDBList();
			list.addAll(new ArrayList(itp.tags));
			DBObject inClause = new BasicDBObject("$in", list);
			DBObject query = new BasicDBObject("tags", inClause);
			DBCursor obsIt = obsCol.find(query);
			
			// push to observer's mail
			while(obsIt.hasNext()){
				DBObject observer = obsIt.next();
				try {
					TmpOut.pln("To push to: "+observer.get("url"));
					// check if observer can see all information
					boolean showReporter = (boolean)observer.get("showAllInfo");
					String url = (String)observer.get("url");
					ArrayList<IncidentTagsPair> reports;
					if(!mails.containsKey(url)){
						reports = new ArrayList<IncidentTagsPair>();
						mails.put(url, reports);
					}else
						reports = mails.get(url);
					if(!showReporter)
						itp.incident.remove("reporter");
					reports.add(itp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// move on to next incident in queue
			itp = queue.poll();
		}
		// notify
		NetworkService ns = (NetworkService)systemService.getCurrentConfig().getService(NetworkService.class);
		Iterator<Entry<String, ArrayList<IncidentTagsPair>>> obIt = mails.entrySet().iterator();
		// notification done using 5 concurrent threads
		// for each relevant observer
		while(obIt.hasNext()){
			Entry<String, ArrayList<IncidentTagsPair>> observer = obIt.next();
			try{
				TmpOut.pln("Notifying: "+observer.getKey());
				String content = PMap.toStrig(observer.getValue());
				TmpOut.pln("Notifying content:"+content);
				ns.sendRequest(observer.getKey(), content);
			}catch(Exception e){
				TmpOut.pln("Notification to "+ observer.getKey() +"failed.");
				e.printStackTrace();
			}
		}
		TmpOut.pln("Notifying done");
	}
	
	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public boolean canReplace(String version) {
		return false;
	}

	@Override
	public boolean startup() {
		TmpOut.pln("notifier service starting...");
		queue = new ConcurrentLinkedQueue<IncidentTagsPair>();
		// delay 10 seconds, run once every 10 seconds
		periodicTimer.schedule(periodicTask, 30000, 30000);
		return true;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		runNotification();
		periodicTimer.cancel();
		queue.clear();
	}

	@Override
	public boolean transitToThisStarts(Service oldService) {
		// TODO Auto-generated method stub
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

	private SystemService systemService;
	@Override
	public void receiveSystemServiceHandler(SystemService service) {
		systemService = service;
	}


	// by right we should allow multiple incidents to be processed in the same request
	// that's why we use <Incident, Tags> pairs
	/*
	private class NotifierResource{
		public HashMap<Map<String, Object>, TreeSet<String>> pairs;
		public NotifierResource(){this.pairs = new HashMap<Map<String, Object>, TreeSet<String>>();}
		public void addTag(Map<String, Object> inc, String tagName){
			TreeSet<String> tags;
			if(pairs.containsKey(inc))
				tags = pairs.get(inc);
			else{
				tags = new TreeSet<String>();
				pairs.put(inc, tags);
			}
			tags.add(tagName);
		}
	}
	*/
	public static class Tags {
		public TreeSet<String> tags;
		public Tags(){
			this.tags = new TreeSet<String>();
		}
		public void addTag(String s){
			tags.add(s);
		}
	}
	public static class IncidentTagsPair extends HashMap<String, Object>{
		public Map<String, Object> incident;
		public TreeSet<String> tags;
		public IncidentTagsPair(Map<String, Object> inc, TreeSet<String> tags){
			this.incident = inc; this.tags = tags;
		}
		public void readyToMap(){
			this.put("incident", incident);
			this.put("tags", tags);
		}
	}
	@Override
	public void addTag(String tagName, Context ctx, Configuration config) {
		Tags ns;
		if(!ctx.hasUnnamed(key)){
			ns = new Tags();
			ns.addTag(tagName);
			ctx.putUnnamed(key, ns);
		}else{
			ns = (Tags) ctx.getUnnamed(key);
			ns.addTag(tagName);
		}
	}

	@Override
	public void enque(Context ctx, Configuration config) {
		Tags ns = (Tags)ctx.getUnnamed(key);
		// FIXME we need "_id" in the incident we send out, so we cannot use "ValidatedIncidentSpec"
		// another new spec, "IncidentInDBSpec", should be used instead
		Map<String, Object> inc = (Map)ctx.getUnnamed(IncidentInDBSpec.class);
		IncidentTagsPair itp = new IncidentTagsPair(inc, ns.tags);
		itp.readyToMap();
		// TODO check if pairs doesn't have tags for the specified incident
		queue.add(itp);
	}

	@Override
	public boolean registerObserver(Map<String, Object> obs,
			String[] tags, Context ctx, Configuration config) {
		// TODO Auto-generated method stub
		return false;
	}

}
