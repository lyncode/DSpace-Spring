package org.dspace.services.impl.configuration.event;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.dspace.services.api.configuration.event.ChangeEventInformation;
import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.event.ChangeType;
import org.dspace.services.api.configuration.reference.PropertyReference;

public class DSpaceConfigurationListener implements ConfigurationListener {
	private ChangeHandler handler;
	private PropertiesConfiguration config;
	private PropertyReference ref;
	
	public DSpaceConfigurationListener (ChangeHandler handler, PropertyReference ref, PropertiesConfiguration config) {
		this.handler = handler;
		this.ref = ref;
		this.config = config;
	}
	
	@Override
	public void configurationChanged(ConfigurationEvent event) {
		if (event.isBeforeUpdate()) return; // Ignores events before
		ChangeType type = ChangeType.MODIFIED;
		switch (event.getType()) {
			case PropertiesConfiguration.EVENT_ADD_PROPERTY:
				type = ChangeType.ADDED;
				break;
			case PropertiesConfiguration.EVENT_SET_PROPERTY:
				type = ChangeType.MODIFIED;
				break;
			case PropertiesConfiguration.EVENT_CLEAR:
				type = ChangeType.REMOVED;
				break;
			case PropertiesConfiguration.EVENT_CLEAR_PROPERTY:
				type = ChangeType.REMOVED;
				break;
			case PropertiesConfiguration.EVENT_RELOAD:
				type = ChangeType.MODIFIED;
				break;
			default:
				return;
		}
		
		ChangeEventInformation eventInfo;
		if (event.getPropertyValue() != null)
			eventInfo = new ChangeEventInformation(event.getPropertyValue(), ref, type);
		else
			eventInfo = new ChangeEventInformation(config.getProperty(ref.getKey()), ref, type);
		handler.handle(eventInfo);
	}

	
	public ChangeHandler getHandler () {
		return this.handler;
	}

	public PropertyReference getReference() {
		return this.ref;
	}
}
