package cms.config2;

import wFramework.base.work.*;
import wFramework.base.configuration.ConfigBuilder;
import wFramework.base.configuration.PartialConfig;
import wFramework.base.service.SystemService;
import wFramework.serviceImpl1.DefaultSystemService;
import cms.config1.services.*;
import cms.config1.workflows.*;
import cms.config1.workflows.incidentWorkflow.*;
import cms.config1.workflows.otherWorkflows.ActiveRequest;
import cms.config1.workflows.otherWorkflows.KillerWork;
import cms.config1.workflows.otherWorkflows.ObsRegWork;
import cms.config1.workflows.otherWorkflows.ReconfigWork;
import cms.config1.workflows.otherWorkflows.Test1;
import cms.config1.workflows.otherWorkflows.TestHostWork;
import cms.config1.workflows.shared.Authenticate;
import cms.config1.workflows.shared.Parse;
import cms.config1.workflows.shared.Respond;
import cms.serviceInterfaces.*;

public class NewConfigBuilder implements ConfigBuilder {

	private Work getIncidentWorkflow(){
		/*
		 * incident workflow: parse -> { authenticate -> [process] -> [finalize]} -> respond
		 * process: validate -> tag
		 * finalize: DBWB -> notify 
		 */
		Work process = new NormalWorkGroupTemplate("incident-middle-process",
				new Work[]{new Validate(), new Tag(), new ImmediateTagger()},
				"failed to process the incident request.");
		Work finalize= new NormalWorkGroupTemplate("incident-middle-finalize",
				new Work[]{new DBWriteBack(), new Notify()},
				"failed to finalize request.");
		Work middle = new NormalWorkGroupTemplate("incident-middle",
				new Work[]{new Authenticate(), process, finalize},
				"request processing failed.");
		Work incident = new CPSTopWorkGroupTemplate("incident-workflow", 
				new Parse(),
				middle,
				new Respond(),
				"failed to process incident.");
		return incident;
	}
	private Work getActiveRequestWorkflow(){
		Work middle = new NormalWorkGroupTemplate("request-middle",
				new Work[]{new Authenticate(), new ActiveRequest()},
				"query processing failed");
		Work activeRequest = new CPSTopWorkGroupTemplate("incident-workflow", 
				new Parse(),
				middle,
				new Respond(),
				"failed to process incident.");
		return activeRequest;
	}
	private Work getReconfigWorkflow(){
		return new CPSTopWorkGroupTemplate("reconfiguration-workflow",
				new Parse(),
				new ReconfigWork(),
				new Respond(),
				"Reconfiguration failed.");
	}
	private Work getObserverRegistrationWorkflow(){
		Work middle = new NormalWorkGroupTemplate("observer-registration-middle",
				new Work[]{new Authenticate(), new ObsRegWork()},
				"query processing failed");
		Work obsReg = new CPSTopWorkGroupTemplate("observer-registration-workflow", 
				new Parse(),
				middle,
				new Respond(),
				"failed to process incident.");
		return obsReg;
	}
	@Override
	public void build(PartialConfig pconfig, SystemService cm) {
		// 1. register services first
		pconfig.registerService(SystemService.class);
		pconfig.registerService(NetworkService.class);
		pconfig.registerService(DatabaseService.class);
		pconfig.registerService(NotifierService.class);
		pconfig.registerService(ErrorService.class);
		pconfig.registerService(AuthenticationService.class);
		// 2. register service providers
		pconfig.registerServiceProvider(SystemService.class, 	DefaultSystemService.class);
		pconfig.registerServiceProvider(NetworkService.class, 	DefaultNetworkService.class);
		pconfig.registerServiceProvider(DatabaseService.class, 	DefaultDatabaseService.class);
		pconfig.registerServiceProvider(NotifierService.class, 	NewNotifierService.class);
		pconfig.registerServiceProvider(ErrorService.class, 	DefaultErrorService.class);
		pconfig.registerServiceProvider(AuthenticationService.class, DefaultAuthenticationService.class);
		

		// hack: register workflows directly
		pconfig.registerWorkflow("/incident", getIncidentWorkflow());
		pconfig.registerWorkflow("/request", getActiveRequestWorkflow());
		pconfig.registerWorkflow("/reconfig", getReconfigWorkflow());
		pconfig.registerWorkflow("/register", getObserverRegistrationWorkflow());
		
		// test workflows:
		pconfig.registerWorkflow("/test1", new Test1());
		pconfig.registerWorkflow("/test2", new TestWork2());
		pconfig.registerWorkflow("/testhost", new TestHostWork());
		pconfig.registerWorkflow("/kill", new KillerWork());

		// we're hacking here and aren't running a textual configuration,
		// so we combine step 3 and 4 :D
		// 3. register Work
		// 4. register WorkGroup
	}

}
