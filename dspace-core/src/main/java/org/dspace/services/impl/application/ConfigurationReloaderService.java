package org.dspace.services.impl.application;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.dspace.services.api.application.Service;
import org.dspace.services.api.application.ServiceException;
import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.dspace.services.impl.configuration.source.ConfigurationChangeLookup;
import org.dspace.services.impl.configuration.source.DSpacePropertySource;

public class ConfigurationReloaderService implements Service {
	private ConfigurationChangeLookup lookup;
	
	public ConfigurationReloaderService () {
		this.lookup = new ConfigurationChangeLookup(DSpacePropertySource.CONFIG_LOCATION);
	}
	
	@Override
	public void refresh() throws ServiceException {
		this.stop();
		this.start();
	}

	@Override
	public void start() throws ServiceException {
		if (!this.lookup.isAlive()) {
			this.lookup.start();
		}
	}

	@Override
	public void stop() throws ServiceException {
		this.lookup.interrupt();
	}

	@Override
	public void init() throws ServiceException {
		//
	}

	@Override
	public void destroy() throws ServiceException {
		this.stop();
	}

	@Override
	public boolean isRunning() {
		return this.lookup.isAlive();
	}

	@Override
	public String getName() {
		return "Configuration reloader service";
	}

	public void registerSource (String module, PropertiesConfiguration config) {
		lookup.addReloader(module, config);
	}

	public void registerHandler(ChangeHandler handler, PropertyReference key) {
		lookup.registerHandler(handler, key);
	}

	public void unregisterHandler(ChangeHandler handler) {
		lookup.unregisterHandler(handler);
	}

	public void unregisterHandler(ChangeHandler handler, PropertyReference key) {
		lookup.unregisterHandler(handler, key);
	}
}
