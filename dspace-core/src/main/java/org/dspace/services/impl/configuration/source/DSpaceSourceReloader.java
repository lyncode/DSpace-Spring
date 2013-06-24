package org.dspace.services.impl.configuration.source;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.reloading.ReloadingStrategy;

public class DSpaceSourceReloader implements ReloadingStrategy {
	private Boolean changed;
	private String name;

	@Override
	public void setConfiguration(FileConfiguration configuration) {
		this.name = configuration.getFileName();
	}

	@Override
	public void init() {

	}

	@Override
	public boolean reloadingRequired() {
		boolean reloadRequired = false;
		synchronized (changed) {
			reloadRequired = changed;
		}
		return reloadRequired;
	}

	public void setChanged() {
		synchronized (changed) {
			changed = true;
		}
	}
	
	public String getFilename () {
		return this.name;
	}

	@Override
	public void reloadingPerformed() {
	}

}
