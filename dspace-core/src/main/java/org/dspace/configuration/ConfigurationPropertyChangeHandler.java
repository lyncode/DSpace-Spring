package org.dspace.configuration;

public abstract class  ConfigurationPropertyChangeHandler {
	public abstract void  handleModification (Object object);
	public abstract void  handleCreation (Object object);
	public abstract void  handleDelete ();
}
