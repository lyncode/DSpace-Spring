package org.dspace.services.api.configuration.event;

import org.dspace.services.api.configuration.reference.PropertyReference;

public class ChangeEventInformation {
	private Object newValue;
	private PropertyReference reference;
	private ChangeType changetype;

	
	public ChangeEventInformation(Object newValue, PropertyReference reference, ChangeType changetype) {
		super();
		this.newValue = newValue;
		this.reference = reference;
		this.changetype = changetype;
	}
	
	/**
	 * @return the newValue
	 */
	public Object getNewValue() {
		return newValue;
	}
	/**
	 * @return the reference
	 */
	public PropertyReference getReference() {
		return reference;
	}
	/**
	 * @return the changetype
	 */
	public ChangeType getChangetype() {
		return changetype;
	}
}
