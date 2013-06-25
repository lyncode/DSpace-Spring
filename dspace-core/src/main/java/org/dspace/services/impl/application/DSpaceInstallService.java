package org.dspace.services.impl.application;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.dspace.install.model.InstallObject;
import org.dspace.install.step.AbstractStep;
import org.dspace.install.step.InstallException;
import org.dspace.services.api.application.Service;
import org.dspace.services.api.application.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public class DSpaceInstallService implements Service {
	static Logger log = Logger.getLogger(DSpaceInstallService.class);
	
	@Autowired AutowireCapableBeanFactory factory;
	@Autowired WebApplicationService webappService;
	private Installer installerThread;
	
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
					this.installerThread.gracefullStop();
					this.installerThread.join();
				} catch (InterruptedException e) {
					throw new ServiceException(e);
				}
			}
		}
	}

	@Override
	public void init() throws ServiceException {
		this.installerThread = new Installer(webappService);
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
		if (this.installerThread != null) {
			if (this.installerThread.isAlive()) {
				if (this.installerThread.getState() == State.BLOCKED) // Do not override already initialized installation processes
					this.installerThread.configure(steps, objects, factory);
			}
		}
	}
	
	public class Installer extends Thread {
		private Object locker;
		private List<AbstractStep> steps;
		private List<Object> inputs;
		private InstallException exception;
		private WebApplicationService service;
		
		public Installer (WebApplicationService service) {
			locker = new Object();
			this.service = service;
		}

		public void configure (List<AbstractStep> steps, List<InstallObject> objects, AutowireCapableBeanFactory factory) {
			synchronized (this.steps) {
				this.steps = new ArrayList<AbstractStep>();
				this.steps.addAll(steps);
				for (AbstractStep s : this.steps)
					factory.autowireBean(s);
				
				this.inputs = new ArrayList<Object>();
				
				for (InstallObject obj : objects) {
					this.inputs.add(obj.deepClone());
				}
			}
			
			this.locker.notifyAll();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#start()
		 */
		@Override
		public synchronized void start() {
			try {
				synchronized (locker) {
					locker.wait();
				}
				super.start();
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			synchronized (exception) {
				exception = null;
			}
			for (int i = 0; i < this.getListSize() && !this.hasError(); i++) {
				AbstractStep step = null;
				Object input = null;
				synchronized (this.steps) {
					step = this.steps.get(i);
					input = this.inputs.get(i);
				}
				try {
					step.install(input);
				} catch (InstallException e) {
					synchronized (this.exception) {
						exception = e;
					}
				}
			}
			
			if (!this.hasError()) {
				try {
					this.service.refresh();
				} catch (ServiceException e) {
					synchronized (exception) {
						exception = new InstallException(e);
					}
				}
			}
		}
		
		public boolean hasError() {
			boolean error = false;
			synchronized (exception) {
				error = exception != null;
			}
			return error;
		}
		
		public InstallException getError () {
			InstallException error = null;
			synchronized (exception) {
				error = exception;
			}
			return error;
		}

		private int getListSize () {
			int size = 0;
			synchronized (this.steps) {
				size = this.steps.size();
			}
			return size;
		}
		
		public void gracefullStop () {
			synchronized (steps) {
				steps.clear();
			}
			this.locker.notifyAll();
		}
	}
	
}
