package org.dspace.configuration;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.reloading.ReloadingStrategy;

public class ReloadStrategy implements ReloadingStrategy {
	private WatchConfigurationFileChanges changes;

	public ReloadStrategy(WatchConfigurationFileChanges changes) {
		this.changes = changes;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void reloadingPerformed() {
		// Don't do anythinga
	}

	@Override
	public boolean reloadingRequired() {
		return this.changes.isModified();
	}

	@Override
	public void setConfiguration(FileConfiguration configuration) {
		// Do nothing
	}
}