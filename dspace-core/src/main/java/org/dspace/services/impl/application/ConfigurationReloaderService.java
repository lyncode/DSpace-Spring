package org.dspace.services.impl.application;

import java.io.File;
import java.util.Map;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.dspace.services.api.application.Service;
import org.dspace.services.api.application.ServiceException;
import org.dspace.services.impl.configuration.source.ConfigurationChangeLookup;
import org.dspace.services.impl.configuration.source.DSpacePropertySource;
import org.dspace.services.impl.configuration.source.DSpaceSourceReloader;

public class ConfigurationReloaderService implements Service {
	private Map<String, DSpaceSourceReloader> reloaders;
	private ConfigurationChangeLookup lookup;
	
	@Override
	public void refresh() throws ServiceException {
		this.stop();
		this.start();
	}

	@Override
	public void start() throws ServiceException {
		if (this.lookup != null)
			if (!this.lookup.isAlive())
				this.lookup.start();
	}

	@Override
	public void stop() throws ServiceException {
		if (this.lookup != null)
			this.lookup.interrupt();
	}

	@Override
	public void init() throws ServiceException {
		if (this.lookup == null)
			this.lookup = new ConfigurationChangeLookup(DSpacePropertySource.CONFIG_LOCATION);
	}

	@Override
	public void destroy() throws ServiceException {
		if (this.lookup != null) {
			this.stop();
			this.lookup = null;
		}
	}

	@Override
	public boolean isRunning() {
		if (this.lookup == null) return false;
		else return this.lookup.isAlive();
	}

	@Override
	public String getName() {
		return "Configuration reloader service";
	}

	public ReloadingStrategy getReloader (File f) {
		if (!this.reloaders.containsKey(f.getName()))
			this.reloaders.put(f.getName(), new DSpaceSourceReloader());
		return this.reloaders.get(f.getName());
	}
}
