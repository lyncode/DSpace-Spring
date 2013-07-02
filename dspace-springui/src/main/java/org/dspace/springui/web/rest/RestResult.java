package org.dspace.springui.web.rest;

import java.io.Serializable;

public class RestResult<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 3762604003204049233L;
	private T element;
	private boolean error;
	private String errorMessage;
	
	public RestResult (T element) {
		this.element = element;
		this.error = false;
		this.errorMessage = null;
	}
	
	public RestResult (String errorMessage) {
		this.error = true;
		this.errorMessage = errorMessage;
		this.element = null;
	}

	/**
	 * @return the element
	 */
	public Serializable getElement() {
		return element;
	}

	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
