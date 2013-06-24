package org.dspace.services.impl.configuration.source;

import java.io.File;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Logger;
import org.dspace.services.api.configuration.ConfigurationServiceException;
import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.reference.Module;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.dspace.services.impl.application.ConfigurationReloaderService;
import org.dspace.services.impl.configuration.event.DSpaceConfigurationListener;
import org.springframework.core.env.PropertySource;

public class DSpacePropertySource extends PropertySource<Object> {
	private static Logger log = Logger.getLogger(DSpacePropertySource.class);
	public static final File CONFIG_LOCATION = new File("config");
	public static final String CONFIG_EXTENSION = ".properties";
	
	private PropertiesConfiguration config;

	public DSpacePropertySource(Module name, ConfigurationReloaderService reloader) throws ConfigurationServiceException {
		this(name.name().toLowerCase(), reloader);
	}

	public DSpacePropertySource(String module, ConfigurationReloaderService reloader) throws ConfigurationServiceException {
		super(module);
		File f = new File(CONFIG_LOCATION, module + CONFIG_EXTENSION);
		try {
			// Create dirs
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			// Create file
			if (!f.exists()) f.createNewFile();
			// Load properties
			config = new PropertiesConfiguration(f);
			config.setAutoSave(true);
			config.setLogger(new Log4JLogger(log));
			config.setReloadingStrategy(reloader.getReloader(f));
			
		} catch (IOException | ConfigurationException e) {
			throw new ConfigurationServiceException(e);
		}
	}

	@Override
	public Object getProperty(String name) {
		return this.config.getProperty(name);
	}
	
	public boolean setProperty (String name, Object value) {
		this.config.setProperty(name, value);
		return true;
	}
	
	public boolean addProperty (String name, Object value) {
		this.config.addProperty(name, value);
		return true;
	}
	
	public void registerWatcher (ChangeHandler handler, String name) {
		DSpaceConfigurationListener listener = new DSpaceConfigurationListener(handler, PropertyReference.key(this.getName(), name), this.config);
		this.config.addConfigurationListener(listener);
	}
	
	public void unregisterWatcher (ChangeHandler handler) {
		for (ConfigurationListener l : config.getConfigurationListeners()) {
			if (l instanceof DSpaceConfigurationListener) {
				DSpaceConfigurationListener ls = (DSpaceConfigurationListener) l;
				if (ls.getHandler() == handler) { // Same instance
					config.removeConfigurationListener(l);
				}
			}
		}
	}

	public void unregisterWatcher (ChangeHandler handler, String name) {
		for (ConfigurationListener l : config.getConfigurationListeners()) {
			if (l instanceof DSpaceConfigurationListener) {
				DSpaceConfigurationListener ls = (DSpaceConfigurationListener) l;
				if (ls.getHandler() == handler && ls.getReference().getKey().equals(name)) { // Same instance && same name
					config.removeConfigurationListener(l);
				}
			}
		}
	}
}
