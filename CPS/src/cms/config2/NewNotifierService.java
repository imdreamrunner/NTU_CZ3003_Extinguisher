package cms.config2;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import misc.TmpOut;

import cms.serviceInterfaces.*;
import cms.config1.services.DefaultNotifierService;
import cms.config1.services.DefaultNotifierService.IncidentTagsPair;
import cms.config1.services.DefaultNotifierService.Tags;
import cms.dataClasses.IncidentInDBSpec;
import cms.dataClasses.ValidatedIncidentSpec;
import cms.dataClasses.PMap;
import wFramework.base.*;
import wFramework.base.configuration.Configuration;
import wFramework.base.service.Service;
import wFramework.base.service.SystemService;

import com.mongodb.*;

public class NewNotifierService implements cms.serviceInterfaces.NotifierService{

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
		// extract all entries from current notification queue, put into working queue,
		// and send to notifWithQueue
		Queue<IncidentTagsPair> workingQ = new ConcurrentLinkedQueue<IncidentTagsPair>();
		IncidentTagsPair itp = queue.poll();
		while(itp!=null){
			workingQ.add(itp);
			itp = queue.poll();
		}
		notifWithQueue(workingQ);
	}
	
	// notifWithQueue may be called under 2 circumstances:
	// 1. periodic notification
	// 2. immediate notification for incidents with specific tags
	private void notifWithQueue(Queue<IncidentTagsPair> workingQ){
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
		if(workingQ.isEmpty()){
			//TmpOut.pln("no notification to send");
			return;
		}
		TmpOut.pln("New notifier service has stuffs to send");
		// grab DB handle first
		DatabaseService dbs = (DatabaseService)systemService.
				getCurrentConfig().getService(DatabaseService.class);
		DBCollection obsCol = dbs.getDBCollection("observer");
		IncidentTagsPair itp = workingQ.poll();
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
			itp = workingQ.poll();
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
		return "1.1.0";
	}

	@Override
	public boolean canReplace(String version) {
		// we can only replace 1.0.0
		return version.equals("1.0.0");
	}

	@Override
	public boolean startup() {
		TmpOut.pln("new notifier service starting...");
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
		/*
		 * When transition begins, we take the queue away from the old service
		 * that is, we add all entries from the old queue into our own queue, then we
		 * clear the old queue
		 */
		startup();
		DefaultNotifierService oldInstance = (DefaultNotifierService)oldService;
		try{
			Field queueField = oldInstance.getClass().getDeclaredField("queue");
			Field timerField = oldInstance.getClass().getDeclaredField("periodicTimer");
			queueField.setAccessible(true);
			timerField.setAccessible(true);
			Queue<IncidentTagsPair> oldQueue = (Queue<IncidentTagsPair>) queueField.get(oldInstance);
			Timer oldTimer = (Timer)timerField.get(oldInstance);
			oldTimer.cancel();
			queueField.set(oldInstance, queue);
			// add all elements of old queue to new queue
			IncidentTagsPair itp = oldQueue.poll();
			while(itp!=null){
				queue.add(itp);
				itp = oldQueue.poll();
			}
			return true;
		}catch(Exception e){
			TmpOut.pln("New notifier failed to grab the queue from the old notifier.");
			e.printStackTrace();
			return false;
		}
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

	private class ImmediateNotifier implements Runnable{
		private IncidentTagsPair itp;
		public ImmediateNotifier (IncidentTagsPair itp){
			this.itp = itp;
		}
		public void run() {
			Queue<IncidentTagsPair> workingQ = new ConcurrentLinkedQueue<IncidentTagsPair>();
			workingQ.add(itp);
			notifWithQueue(workingQ);
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
		/*
		 * two ways: if it contains a tag "immediateNotif", we send out immediate notification
		 * otherwise just add it to queue
		 */
		if(ns.tags.contains("immediateNotif")){
			TmpOut.pln("taking the immediate path");
			Thread t = new Thread(new ImmediateNotifier(itp));
			t.start();
		}
		else
			queue.add(itp);
	}

	@Override
	public boolean registerObserver(Map<String, Object> obs,
			String[] tags, Context ctx, Configuration config) {
		// TODO Auto-generated method stub
		return false;
	}

}
