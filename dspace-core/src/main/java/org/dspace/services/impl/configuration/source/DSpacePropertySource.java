package org.dspace.services.impl.configuration.source;

import java.io.File;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Logger;
import org.dspace.services.api.configuration.ConfigurationServiceException;
import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.reference.Module;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.dspace.services.impl.application.ConfigurationReloaderService;
import org.springframework.core.env.PropertySource;

public class DSpacePropertySource extends PropertySource<Object> {
	private static Logger log = Logger.getLogger(DSpacePropertySource.class);
	public static final File CONFIG_LOCATION = new File("config");
	public static final String CONFIG_EXTENSION = ".properties";
	
	private PropertiesConfiguration config;
	private ConfigurationReloaderService reload;

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
			
			this.reload = reloader;
			this.reload.registerSource(module, config);
		} catch (Exception e) {
			throw new ConfigurationServiceException(e);
		}
	}

	public File getFile () {
		return this.config.getFile();
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
		if (this.config.containsKey(name))
			return false;
		this.config.addProperty(name, value);
		return true;
	}
	
	public void registerHandler (ChangeHandler handler, String name) {
		this.reload.registerHandler(handler, PropertyReference.key(this.getName(), name));
	}
	
	public void unregisterHandler (ChangeHandler handler) {
		this.reload.unregisterHandler(handler);
	}

	public void unregisterHandler (ChangeHandler handler, String name) {
		this.reload.unregisterHandler(handler, PropertyReference.key(this.getName(), name));
	}

	public boolean removeProperty(String key) {
		if (this.config.containsKey(key)) {
			this.config.clearProperty(key);
			return true;
		}
		return false;
	}
}
