package org.dspace.services.impl.configuration.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.dspace.services.api.configuration.event.ChangeEventInformation;
import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.event.ChangeType;
import org.dspace.services.api.configuration.reference.PropertyReference;

public class ConfigurationReloader {
	private static Logger log = Logger.getLogger(ConfigurationReloader.class);
	private String module;
	private PropertiesConfiguration config;
	private Map<String, List<ChangeHandler>> handlers;
	private Map<String, Object> oldValues;
	
	public ConfigurationReloader (String module, PropertiesConfiguration config) {
		this.config = config;
		this.module = module;
		this.handlers = new HashMap<String, List<ChangeHandler>>();
		this.oldValues = new HashMap<String, Object>();
	}
	
	public boolean addHandler (String name, ChangeHandler handler) {
		if (!this.handlers.containsKey(name))
			this.handlers.put(name, new ArrayList<ChangeHandler>());
		
		
		// Store handler
		this.handlers.get(name).add(handler);
		
		// Store old value
		this.oldValues.put(name, config.getProperty(name));
		
		return true;
	}
	
	public void check () {
		try {
			config.refresh();
		} catch (ConfigurationException e) {
			log.error(e.getMessage(), e);
		}
		for (String key : this.handlers.keySet()) {
			Object newValue = this.config.getProperty(key);
			Object oldValue = this.oldValues.get(key);
			if (sameType(oldValue, newValue) && distinct(oldValue, newValue))
				this.fireHandlers(key);
			this.oldValues.put(key, newValue);
		}
	}
	
	private void fireHandlers(String key) {
		Object oldValue = this.oldValues.get(key);
		Object newValue = config.getProperty(key);
		ChangeType type = ChangeType.REMOVED;
		if (oldValue == null && newValue != null) 
			type = ChangeType.ADDED;
		else if (oldValue != null && newValue != null)
			type = ChangeType.MODIFIED;
		ChangeEventInformation info = new ChangeEventInformation(newValue, PropertyReference.key(module, key), type);
		for (ChangeHandler handler : this.handlers.get(key))
			handler.handle(info);
	}

	private static boolean distinct(Object oldValue, Object newValue) {
		if (oldValue != null && newValue != null)
			return !oldValue.equals(newValue);
		else if (oldValue == null && newValue == null)
			return false;
		else
			return true;
	}

	private static boolean sameType(Object oldValue, Object newValue) {
		if (oldValue != null && newValue != null) 
			return oldValue.getClass().getName().equals(newValue.getClass().getName());
		else	
			return true;
	}

	public boolean equals (Object obj) {
		if (obj instanceof ConfigurationReloader) {
			return ((ConfigurationReloader) obj).module.equals(this.module);
		} else if (obj instanceof String) {
			return ((String) obj).equals(this.config.getFileName());
		}
		return super.equals(obj);
	}

	public boolean removeHandler(ChangeHandler handler) {
		for (String key : this.handlers.keySet()) {
			List<Integer> toRemove = new ArrayList<Integer>();
			for (int i = 0;i<this.handlers.get(key).size();i++)
				if (this.handlers.get(key).get(i) == handler) 
					toRemove.add(i);
			
			for (Integer i : toRemove)
				this.handlers.get(key).remove(i);
		}
		
		return true;
	}

	public boolean removeHandler(String key, ChangeHandler handler) {
		List<Integer> toRemove = new ArrayList<Integer>();
		for (int i = 0;i<this.handlers.get(key).size();i++)
			if (this.handlers.get(key).get(i) == handler) 
				toRemove.add(i);
		
		for (Integer i : toRemove)
			this.handlers.get(key).remove(i);
		
		return true;
	}

	public String getModule() {
		return this.module;
	}
}
