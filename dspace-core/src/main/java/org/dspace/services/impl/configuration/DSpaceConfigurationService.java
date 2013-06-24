package org.dspace.services.impl.configuration;

import java.util.Iterator;

import org.dspace.configuration.ConfigurationPropertyChangeHandler;
import org.dspace.services.api.configuration.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;


public class DSpaceConfigurationService implements ConfigurationService {
	@Autowired ApplicationContext context;
	
	@Override
	public <T> T getProperty(String name, Class<T> type) {
		return context.getEnvironment().getProperty(name, type);
	}

	@Override
	public String getProperty(String name) {
		return context.getEnvironment().getProperty(name);
	}

	@Override
	public <T> T getProperty(String name, Class<T> type, T defaultValue) {
		return context.getEnvironment().getProperty(name, type, defaultValue);
	}
	
	@Override
	public boolean setProperty(String key, Object value) {
		DSpacePropertySource source = this.getSource();
		if (source != null) {
			source.set(key, value);
			return true;
		} else {
			return false;
		}
	}
	
	private DSpacePropertySource getSource () {
		ConfigurableApplicationContext cfg = (ConfigurableApplicationContext) context;
		Iterator<PropertySource<?>> srcs = cfg.getEnvironment().getPropertySources().iterator();
		while (srcs.hasNext()) {
			PropertySource<?> src = srcs.next();
			if (src instanceof DSpacePropertySource) {
				return ((DSpacePropertySource) src);
			}
		}
		return null;
	}

	@Override
	public boolean addWatchHandler(ConfigurationPropertyChangeHandler handler,
			String... key) {
		DSpacePropertySource source = this.getSource();
		if (source != null) {
			for (String k : key)
				source.addHandler(k, handler);
			return true;
		}
		return false;
	}

	@Override
	public boolean addProperty(String key, Object value) {
		DSpacePropertySource source = this.getSource();
		if (source != null) {
			source.add(key, value);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeWatchHandler(ConfigurationPropertyChangeHandler handler) {
		DSpacePropertySource source = this.getSource();
		if (source != null) {
			source.removeHandler(handler);
			return true;
		}
		return false;
	}
}
