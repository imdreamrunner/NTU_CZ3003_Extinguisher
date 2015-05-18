package wFramework.base;

import java.util.HashMap;

public class Context{
	// HashMap<String, Object> = namedObjs
	private NamedObjContainer namedObjs;
	private HashMap<Object, Object> unnamedObjs;
	public Context(){
		namedObjs = new NamedObjContainer();
		unnamedObjs=new HashMap<Object, Object>();
	}
	public Object getUnnamed(Object key){
		return unnamedObjs.get(key);
	}
	public Object getNamed(String name){
		return namedObjs.get(name);
	}
	public void putUnnamed(Object key, Object value){
		unnamedObjs.put(key, value);
	}
	public void putNamed(String name, Object value){
		namedObjs.put(name, value);
	}
	public void removeUnnamed(Object key){
		unnamedObjs.remove(key);
	}
	public void removeNamed(String name){
		namedObjs.remove(name);
	}
	public boolean hasUnnamed(Object key){
		return unnamedObjs.containsKey(key);
	}
	public boolean hasNamed(String name){
		return namedObjs.containsKey(name);
	}
}
