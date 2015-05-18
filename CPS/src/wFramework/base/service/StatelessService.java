package wFramework.base.service;

/*
 * Stateless services are simpler stuffs that follow a fixed interface, do a set of specified things,
 * but do not own states of their own. So they are much easier to replace. These things are more like
 * libraries that things in the work flow can directly call
 */
public interface StatelessService {
	public String getVersion();
}
