package org.dspace.services.impl.application;

import java.io.File;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.dspace.configuration.ReloadStrategy;
import org.dspace.configuration.WatchConfigurationFileChanges;
import org.dspace.services.api.application.Service;
import org.dspace.services.api.application.ServiceException;
import org.dspace.services.api.configuration.ConfigurationServiceException;
import org.dspace.services.impl.configuration.DSpaceConfigurationLocator;
import org.dspace.services.impl.configuration.DSpacePropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

public class ConfigurationWatcherService implements Service {
	@Autowired ConfigurableApplicationContext applicationContext;
	private Thread fileWatcher;
	
	@Override
	public synchronized void refresh() throws ServiceException {
		this.stop();
		this.start();
	}

	@Override
	public synchronized void start() throws ServiceException {
		if (fileWatcher == null) throw new ServiceException("Service not correctly initialized");
		fileWatcher.start();
	}

	@Override
	public synchronized void stop() throws ServiceException {
		if (fileWatcher == null) throw new ServiceException("Service not correctly initialized");
		fileWatcher.interrupt();
	}

	@Override
	public void init() throws ServiceException {
		try {
			WatchConfigurationFileChanges watcher = new WatchConfigurationFileChanges(applicationContext);
			ReloadingStrategy reloader = new ReloadStrategy(watcher);
			fileWatcher = new Thread(watcher);
			File configFile = new File(DSpaceConfigurationLocator.getConfigurationDirectory(applicationContext), DSpaceConfigurationLocator.getConfigurationFilename());
			DSpacePropertySource source = new DSpacePropertySource(configFile, reloader);
			applicationContext.getEnvironment().getPropertySources().addFirst(source);
		} catch (ConfigurationServiceException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void destroy() throws ServiceException {
		this.stop();
	}

	@Override
	public boolean isRunning() {
		if (this.fileWatcher != null)
			return this.fileWatcher.isAlive();
		return false;
	}

	@Override
	public String getName() {
		return "Configuration Change Watcher";
	}
	

}
