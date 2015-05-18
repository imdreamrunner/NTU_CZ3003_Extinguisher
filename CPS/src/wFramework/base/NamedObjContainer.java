package wFramework.base;
import java.util.HashMap;

// simple wrapper of HashMap<String, Object>
// a simple dictionary that stores a set of (key, value) pairs
// key must be String, value is Object

public class NamedObjContainer extends HashMap<String, Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8540007004584421907L;
	/*
	private HashMap<String, Object> namedObjects;
	public NamedObjContainer(){
		namedObjects = new HashMap<String, Object>();
	}
	public Object getNamedObject(String name){
		return namedObjects.get(name);
	}
	// we need a way to implement privileged modification
	public void setNamedObject(String name, Object obj){
		namedObjects.put(name, obj);
	}
	public boolean hasName(String name){
		return namedObjects.containsKey(name);
	}
	public void removeNamedObject(String name){
		namedObjects.remove(name);
	}
	public int size(){
		return namedObjects.size();
	}
	*/
}
