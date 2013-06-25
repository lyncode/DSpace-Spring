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
	
	@Autowired ConfigurationReloaderService reloader;
	@Autowired ConfigurableApplicationContext context;
	
	private DSpacePropertySource getSource (PropertyReference ref) {
		for (PropertySource<?> source : context.getEnvironment().getPropertySources()) {
			if (source instanceof DSpacePropertySource) {
				DSpacePropertySource src = (DSpacePropertySource) source;
				if (src.getName().equals(ref.getModule()))
					return src;
			}
		}
		DSpacePropertySource source;
		try {
			source = new DSpacePropertySource(ref.getModule(), reloader);
			if (ref.isDefault())
				context.getEnvironment().getPropertySources().addFirst(source);
			else
				context.getEnvironment().getPropertySources().addAfter(PropertyReference.DEFAULT_MODULE, source);
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
		if (reference.isDefault())
			return context.getEnvironment().getProperty(reference.getKey(), type, defaultValue);
		DSpacePropertySource source = this.getSource(reference);
		if (source == null) return defaultValue;
		else return type.cast(source.getProperty(reference.getKey()));
	}

	@Override
	public boolean registerHandler(ChangeHandler handler, PropertyReference... references) {
		List<PropertyReference> added = new ArrayList<PropertyReference>();
		for (PropertyReference ref : references) {
			if (this.registerHandler(handler, ref))
				added.add(ref);
			else {
				for (PropertyReference add : added)
					this.unregisterHandler(handler, add);
				return false;
			}
		}
		return true;
	}
	
	private boolean registerHandler(ChangeHandler handler, PropertyReference references) {
		DSpacePropertySource source = this.getSource(references);
		if (source == null) return false;
		else source.registerHandler(handler, references.getKey());
		return true;
	}

	@Override
	public boolean unregisterHandler (ChangeHandler handler) {
		for (PropertySource<?> source : context.getEnvironment().getPropertySources()) {
			if (source instanceof DSpacePropertySource) {
				DSpacePropertySource src = (DSpacePropertySource) source;
				src.unregisterHandler(handler);
			}
		}
		return true;
	}

	@Override
	public boolean unregisterHandler(ChangeHandler handler, PropertyReference... references) {
		for (PropertyReference ref : references) {
			DSpacePropertySource source = this.getSource(ref);
			if (source != null) source.unregisterHandler(handler, ref.getKey());
		}
		return true;
	}

	@Override
	public boolean addProperty(PropertyReference reference, Object value) {
		DSpacePropertySource source = this.getSource(reference);
		if (source == null) return false;
		else return source.addProperty(reference.getKey(), value);
	}

	@Override
	public boolean setProperty(PropertyReference reference, Object value) {
		DSpacePropertySource source = this.getSource(reference);
		if (source == null) return false;
		else return source.setProperty(reference.getKey(), value);
	}

	@Override
	public boolean isInstalled() {
		return this.getProperty(PropertyReference.INSTALLED, Boolean.class, false);
	}

	@Override
	public boolean removeProperty(PropertyReference key) {
		DSpacePropertySource source = this.getSource(key);
		if (source != null) return source.removeProperty(key.getKey());
		return false;
	}

}
