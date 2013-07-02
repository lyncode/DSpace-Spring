package org.dspace.services.impl.application;

import java.lang.Thread.State;
import java.util.List;
import org.apache.log4j.Logger;
import org.dspace.install.InstallerThread;
import org.dspace.install.model.InstallObject;
import org.dspace.install.step.AbstractStep;
import org.dspace.install.step.InstallException;
import org.dspace.services.api.application.Service;
import org.dspace.services.api.application.ServiceException;
import org.dspace.services.api.configuration.ConfigurationService;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public class DSpaceInstallService implements Service {
	static Logger log = Logger.getLogger(DSpaceInstallService.class);
	
	@Autowired ConfigurationService config;
	@Autowired AutowireCapableBeanFactory factory;
	@Autowired WebApplicationService webappService;
	private InstallerThread installerThread;
	
	@Override
	public void refresh() throws ServiceException {
		this.stop();
		this.start();
	}

	@Override
	public void start() throws ServiceException {
		if (this.installerThread != null) {
			if (!this.installerThread.isAlive())
				this.installerThread.start();
		}
	}

	@Override
	public void stop() throws ServiceException {
		if (this.installerThread != null) {
			if (this.installerThread.isAlive()) {
				try {
					this.installerThread.interrupt();
					this.installerThread.join();
				} catch (InterruptedException e) {
					throw new ServiceException(e);
				}
			}
		}
	}

	@Override
	public void init() throws ServiceException {
		this.installerThread = new InstallerThread();
	}

	@Override
	public void destroy() throws ServiceException {
		if (this.installerThread != null) {
			if (this.installerThread.isAlive()) 
				this.installerThread.interrupt();
		}
	}

	@Override
	public boolean isRunning() {
		if (this.installerThread != null)
			return this.installerThread.isAlive();
		return false;
	}

	@Override
	public String getName() {
		return "Installer Service";
	}

	public void install(List<AbstractStep> steps, List<InstallObject> objects) throws InstallException {
		for (AbstractStep s : steps)
			factory.autowireBean(s);
		if (this.installerThread != null) {
			if (this.installerThread.isAlive()) {
				if (this.installerThread.getState() == State.BLOCKED || this.installerThread.getState() == State.WAITING) // Do not override already initialized installation processes
					this.installerThread.install(steps, objects);
			}
		}
	}

	public void doFinal() {
		this.config.setProperty(PropertyReference.INSTALLED, true);
	}
}
