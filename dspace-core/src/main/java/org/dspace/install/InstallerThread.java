package org.dspace.install;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.dspace.install.model.InstallObject;
import org.dspace.install.step.AbstractStep;
import org.dspace.install.step.InstallException;

public class InstallerThread extends Thread {
	private static Logger log = Logger.getLogger(InstallerThread.class);
	private Object locker;
	private Object installLocker;
	private List<AbstractStep> steps;
	private List<Object> args;
	private InstallException exception;
	
	public InstallerThread () {
		locker = new Object();
		installLocker = new Object();
	}
	
	public synchronized void install (List<AbstractStep> steps, List<InstallObject> objects) {
		this.steps = new ArrayList<AbstractStep>(steps);
		this.args = new ArrayList<Object>();
		for (InstallObject obj : objects) {
			if (obj != null)
				this.args.add(obj.deepClone());
			else
				this.args.add(null);
		}
		
		this.notifyNext();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		this.waitNext();
		this.resetError();
		
		for (int i = 0; i < this.getListSize() && !this.hasError(); i++) {
			AbstractStep step = null;
			Object input = null;
			synchronized (this) {
				step = this.steps.get(i);
				input = this.args.get(i);
			}
			try {
				step.install(input);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				this.setError(new InstallException(e));
			}
		}
		
		this.notifyInstallFinish();
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {
		this.waitInstallFinish();
		super.interrupt();
	}
	
	private void waitInstallFinish () {
		try {
			synchronized (this.installLocker) {
				this.installLocker.wait();
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void notifyInstallFinish () {
		synchronized (installLocker) {
			this.installLocker.notifyAll();
		}
	}

	private synchronized void setError (InstallException e) {
		this.exception = e;
	}
	
	private synchronized void resetError () {
		this.exception = null;
	}
	
	private synchronized boolean hasError() {
		return exception != null;
	}
	
	public synchronized InstallException getError () {
		return exception;
	}

	private synchronized int getListSize () {
		return this.steps.size();
	}
	
	private void notifyNext () {
		synchronized (locker) {
			locker.notify();
		}
	}
	
	private void waitNext () {
		try {
			synchronized (locker) {
				locker.wait();
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}
}
