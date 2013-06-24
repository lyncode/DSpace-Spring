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
import org.apache.log4j.Logger;

public class ConfigurationChangeLookup extends Thread {
	private static Logger log = Logger.getLogger(ConfigurationChangeLookup.class);
	private File directory;
	private List<DSpaceSourceReloader> lookups;
	
	public ConfigurationChangeLookup (File dir) {
		directory = dir;
		lookups = new ArrayList<DSpaceSourceReloader>();
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
					DSpaceSourceReloader reloader = this.getReloader(file.getName());
					if (reloader != null) reloader.setChanged();
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private DSpaceSourceReloader getReloader(String name) {
		DSpaceSourceReloader rel = null;
		synchronized (lookups) {
			for (DSpaceSourceReloader r : this.lookups) {
				if (r.getFilename().equals(name)) {
					rel = r;
					break;
				}
			}
		}
		return rel;
	}
	
	public void addReloader (DSpaceSourceReloader r) {
		synchronized (lookups) {
			lookups.add(r);
		}
	}
}
