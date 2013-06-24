package org.dspace.services.api.application;

public interface Service {
	void refresh () throws ServiceException;
	void start () throws ServiceException;
	void stop () throws ServiceException;
	void init () throws ServiceException;
	void destroy () throws ServiceException;
	boolean isRunning ();
	String getName();
}
