package org.dspace.services.api.configuration.event;

public interface ChangeHandler {
	void handle (ChangeEventInformation info);
}
