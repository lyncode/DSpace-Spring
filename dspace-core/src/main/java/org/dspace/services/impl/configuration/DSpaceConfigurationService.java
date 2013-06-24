package org.dspace.services.impl.configuration;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.dspace.services.api.configuration.ConfigurationService;
import org.dspace.services.api.configuration.ConfigurationServiceException;
import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.dspace.services.impl.application.ConfigurationReloaderService;
import org.dspace.services.impl.configuration.source.DSpacePropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;

public class DSpaceConfigurationService implements ConfigurationService {
	private static Logger log = Logger.getLogger(DSpaceConfigurationService.class);
	private static final String defaultModule = "dspace";
	@Autowired ConfigurationReloaderService reloader;
	@Autowired ConfigurableApplicationContext context;
	
	private DSpacePropertySource getSource (PropertyReference ref) {
		String module = (ref.getModule() == null) ? defaultModule : ref.getModule();
		for (PropertySource<?> source : context.getEnvironment().getPropertySources()) {
			if (source instanceof DSpacePropertySource) {
				DSpacePropertySource src = (DSpacePropertySource) source;
				if (src.getName().equals(module))
					return src;
			}
		}
		DSpacePropertySource source;
		try {
			source = new DSpacePropertySource(module, reloader);
			if (ref.getModule() == null)
				context.getEnvironment().getPropertySources().addFirst(source);
			else
				context.getEnvironment().getPropertySources().addAfter(defaultModule, source);
			return source;
		} catch (ConfigurationServiceException e) {
			log.error("Cannot create configuration source", e);
			
			return null;
		}
	}

	@Override
	public String getProperty(PropertyReference reference) {
		return this.getProperty(reference, String.class, null);
	}

	@Override
	public <T> T getProperty(PropertyReference reference, Class<T> type) {
		return this.getProperty(reference, type, null);
	}

	@Override
	public <T> T getProperty(PropertyReference reference, Class<T> type, T defaultValue) {
		if (reference.getModule() == null)
			return context.getEnvironment().getProperty(reference.getKey(), type, defaultValue);
		DSpacePropertySource source = this.getSource(reference);
		if (source == null) return defaultValue;
		else return type.cast(source.getProperty(reference.getKey()));
	}

	@Override
	public boolean addWatchHandler(ChangeHandler handler, PropertyReference... references) {
		List<PropertyReference> added = new ArrayList<PropertyReference>();
		for (PropertyReference ref : references) {
			if (this.addWatchHandler(handler, ref))
				added.add(ref);
			else {
				for (PropertyReference add : added)
					this.removeWatchHandler(handler, add);
				return false;
			}
		}
		return true;
	}
	
	private boolean addWatchHandler(ChangeHandler handler, PropertyReference references) {
		DSpacePropertySource source = this.getSource(references);
		if (source == null) return false;
		else source.registerWatcher(handler, references.getKey());
		return true;
	}

	@Override
	public boolean removeWatchHandler(ChangeHandler handler) {
		for (PropertySource<?> source : context.getEnvironment().getPropertySources()) {
			if (source instanceof DSpacePropertySource) {
				DSpacePropertySource src = (DSpacePropertySource) source;
				src.unregisterWatcher(handler);
			}
		}
		return true;
	}

	@Override
	public boolean removeWatchHandler(ChangeHandler handler, PropertyReference... references) {
		for (PropertyReference ref : references) {
			DSpacePropertySource source = this.getSource(ref);
			if (source != null) source.unregisterWatcher(handler, ref.getKey());
		}
		return true;
	}

	@Override
	public boolean addProperty(PropertyReference reference, Object value) {
		DSpacePropertySource source = this.getSource(reference);
		if (source == null) return false;
		else source.addProperty(reference.getKey(), value);
		return true;
	}

	@Override
	public boolean setProperty(PropertyReference reference, Object value) {
		DSpacePropertySource source = this.getSource(reference);
		if (source == null) return false;
		else source.setProperty(reference.getKey(), value);
		return true;
	}

	@Override
	public boolean isInstalled() {
		return this.getProperty(PropertyReference.INSTALLED, Boolean.class, false);
	}

}
