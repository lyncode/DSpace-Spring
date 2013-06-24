package org.dspace.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dspace.services.impl.configuration.DSpaceConfigurationLocator;
import org.springframework.context.ConfigurableApplicationContext;


public class WatchConfigurationFileChanges implements Runnable {
	private Logger log = LogManager.getLogger(WatchConfigurationFileChanges.class); 
	private boolean changed;
	private ConfigurableApplicationContext context;
	
	public WatchConfigurationFileChanges (ConfigurableApplicationContext context2) {
		this.context = context2;
	}
	
	public synchronized boolean isModifiedAndReset () {
		if (this.changed) {
			this.changed = false;
			return this.changed;
		}
		return false;
	}
	public synchronized boolean isModified () {
		return this.changed;
	}

	public synchronized void setModified () {
		this.changed = true;
	}

	@Override
	public void run() {
		while (true) {
			Path path = Paths.get(DSpaceConfigurationLocator.getConfigurationDirectory(this.context).toURI());
			try {
				WatchService watcher = path.getFileSystem().newWatchService();
				path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY);

				WatchKey watckKey = watcher.take();

				List<WatchEvent<?>> events = watckKey.pollEvents();
				for (WatchEvent<?> event : events) {
					Path file = (Path) event.context();
					if (file != null && 
							file.getFileName() != null && 
							file.getFileName().equals(DSpaceConfigurationLocator.getConfigurationFilename()))
						this.setModified();
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}