package org.dspace.services.impl.configuration.handler;

import org.apache.log4j.Logger;
import org.dspace.services.api.application.Service;
import org.dspace.services.api.application.ServiceException;
import org.dspace.services.api.configuration.event.ChangeEventInformation;
import org.dspace.services.api.configuration.event.ChangeHandler;

public class RefreshServiceOnChangeHandler implements ChangeHandler {
	private static Logger log = Logger.getLogger(RefreshServiceOnChangeHandler.class);
	private Service service;
	private Object oldValue;

	public RefreshServiceOnChangeHandler (Service service, Object oldValue) {
		this.service = service;
		this.oldValue = oldValue;
	}
	
	private void myHandle(ChangeEventInformation info) throws ServiceException {
		if (info.getNewValue() == null && this.oldValue != null)
			service.refresh();
		else {
			if (!info.getNewValue().equals(this.oldValue)) {
				service.refresh();
			}
		}
	}

	@Override
	public void handle(ChangeEventInformation info) {
		try {
			this.myHandle(info);
		} catch (ServiceException e) {
			log.error("Unable to refresh service", e);
		}
	}

}
