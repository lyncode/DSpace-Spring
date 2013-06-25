package org.dspace.services.impl.configuration.source;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.dspace.services.impl.configuration.events.ConfigurationReloader;

public class ConfigurationChangeLookup extends Thread {
	private static Logger log = Logger.getLogger(ConfigurationChangeLookup.class);
	private File directory;
	private List<ConfigurationReloader> lookups;
	
	public ConfigurationChangeLookup (File dir) {
		directory = dir;
		lookups = new ArrayList<ConfigurationReloader>();
	}
	
	public void run () {
		while (true) {
			Path path = Paths.get(directory.toURI());
			try {
				WatchService watcher = path.getFileSystem().newWatchService();
				path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY);

				WatchKey watckKey = watcher.take();

				List<WatchEvent<?>> events = watckKey.pollEvents();
				for (WatchEvent<?> event : events) {
					File file = ((Path) event.context()).toFile();
					this.checkChanges(file.getName());
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	private int getIndex (String name) {
		synchronized (lookups) {
			int index = 0;
			for (ConfigurationReloader r : lookups) {
				if (r.equals(name))
					return index;
				
				index++;
			}
		}
		return -1;
	}

	private int getIndexModule(String module) {
		synchronized (lookups) {
			int index = 0;
			for (ConfigurationReloader r : lookups) {
				if (r.getModule().equals(module))
					return index;
				
				index++;
			}
		}
		return -1;
	}
	
	private void checkChanges(String name) {
		int index = this.getIndex(name);
		synchronized (lookups) {
			if (index >= 0) {
				lookups.get(index).check();
			}
		}
	}

	public boolean addReloader (String module, PropertiesConfiguration r) {
		int index = this.getIndex(r.getFileName());
		synchronized (lookups) {
			if (index == -1)
				lookups.add(new ConfigurationReloader(module, r));
		}
		return true;
	}

	public boolean registerHandler(ChangeHandler handler, PropertyReference key) {
		int index = this.getIndexModule(key.getModule());
		synchronized (lookups) {
			if (index >= 0) 
				return lookups.get(index).addHandler(key.getKey(),	handler);
		}
		return false;
	}

	public boolean unregisterHandler(ChangeHandler handler) {
		synchronized (lookups) {
			for (ConfigurationReloader r : lookups)
				if (!r.removeHandler(handler))
					return false;
		}
		return true;
	}

	public boolean unregisterHandler(ChangeHandler handler, PropertyReference key) {
		int index = this.getIndexModule(key.getModule());
		synchronized (lookups) {
			if (index >= 0) 
				return lookups.get(index).removeHandler(key.getKey(),	handler);
		}
		return false;
	}
}
