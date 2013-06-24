package org.dspace.services.impl.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dspace.configuration.ConfigurationPropertyChangeHandler;
import org.dspace.services.api.configuration.ConfigurationServiceException;
import org.dspace.util.Pair;
import org.springframework.core.env.PropertySource;


public class DSpacePropertySource extends PropertySource<Object> {
	public static final String DSPACE_SOURCE = "dspaceSource";
	private static Logger log = LogManager.getLogger(DSpacePropertySource.class);
	private PropertiesConfiguration properties;
	private Map<Pair<String, ConfigurationPropertyChangeHandler>, DSpaceConfigurationListener> changeListeners = null;
	
	public DSpacePropertySource(File f, ReloadingStrategy reloader) throws ConfigurationServiceException {
		super(f.getName());
		changeListeners = new HashMap<Pair<String, ConfigurationPropertyChangeHandler>, DSpaceConfigurationListener>();
		try {
			properties = new PropertiesConfiguration(f);
			properties.setAutoSave(true);
			properties.setReloadingStrategy(reloader);
			properties.setLogger(new Log4JLogger(log));
		} catch (ConfigurationException e) {
			throw new ConfigurationServiceException(e);
		}
	}

	@Override
	public Object getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	public Object get(String key) {
		return this.properties.getProperty(key);
	}

	public boolean has(String name) {
		return this.properties.containsKey(name);
	}

	public void set(String name, Object value) {
		this.properties.setProperty(name, value);
	}

	public void addHandler(String name, ConfigurationPropertyChangeHandler handler) {
		DSpaceConfigurationListener listener = new DSpaceConfigurationListener(name, handler);
		Pair<String, ConfigurationPropertyChangeHandler> pair = new Pair<String, ConfigurationPropertyChangeHandler>(name, handler);
		this.changeListeners.put(pair, listener);
		this.properties.addConfigurationListener(listener);
	}


	public void removeHandler(ConfigurationPropertyChangeHandler handler) {
		for (Pair<String, ConfigurationPropertyChangeHandler> pair : this.changeListeners.keySet()) {
			if (pair.getSecond() == handler) {
				DSpaceConfigurationListener listener = this.changeListeners.get(pair);
				this.properties.removeConfigurationListener(listener);
			}
		}
	}

	public void add(String key, Object value) {
		this.properties.addProperty(key, value);
	}

	public class DSpaceConfigurationListener implements ConfigurationListener {
		private String key;
		private ConfigurationPropertyChangeHandler handler;
		
		
		public DSpaceConfigurationListener(String key, ConfigurationPropertyChangeHandler handler) {
			this.key = key;
			this.handler = handler;
		}

		@Override
		public void configurationChanged(ConfigurationEvent event) {
			if (event.getType() == AbstractConfiguration.EVENT_ADD_PROPERTY) {
				if (event.getPropertyName().equals(this.key)) {
					this.handler.handleCreation(event.getPropertyValue());
				}
			} else if (event.getType() == AbstractConfiguration.EVENT_SET_PROPERTY) {
				if (event.getPropertyName().equals(this.key)) {
					this.handler.handleModification(event.getPropertyValue());
				}
			} else if (event.getType() == AbstractConfiguration.EVENT_CLEAR_PROPERTY) {
				if (event.getPropertyName().equals(this.key)) {
					this.handler.handleDelete();
				}
			} else if (event.getType() == AbstractConfiguration.EVENT_CLEAR) {
				this.handler.handleDelete();
			}
		}

	}
}
