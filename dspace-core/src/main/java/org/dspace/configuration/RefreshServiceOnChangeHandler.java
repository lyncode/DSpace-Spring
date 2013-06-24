package org.dspace.configuration;

import org.apache.log4j.Logger;
import org.dspace.services.api.application.Service;
import org.dspace.services.api.application.ServiceException;

public class RefreshServiceOnChangeHandler extends ConfigurationPropertyChangeHandler {
	private static Logger log = Logger.getLogger(RefreshServiceOnChangeHandler.class);
	private Object property;
	private Service service;

	public RefreshServiceOnChangeHandler(Service service, Object initialValue) {
		super();
		this.property = initialValue;
		this.service = service;
	}
	public RefreshServiceOnChangeHandler(Service service) {
		super();
		this.property = null;
		this.service = service;
	}

	@Override
	public void handleModification(Object object) {
		if (object != this.property) {
			if (object != null && !object.equals(this.property)) {
				try {
					this.service.refresh();
				} catch (ServiceException e) {
					log.debug(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void handleCreation(Object object) {
		if (object != this.property) {
			if (object != null && !object.equals(this.property)) {
				try {
					this.service.refresh();
				} catch (ServiceException e) {
					log.debug(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void handleDelete() {
		if (this.property != null) {
			this.property = null;
			try {
				this.service.refresh();
			} catch (ServiceException e) {
				log.debug(e.getMessage(), e);
			}
		}
	}

}
