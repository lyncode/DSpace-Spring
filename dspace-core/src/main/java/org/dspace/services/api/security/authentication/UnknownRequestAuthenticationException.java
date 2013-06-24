package org.dspace.services.api.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class UnknownRequestAuthenticationException extends
		AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2153360947377107501L;

	public UnknownRequestAuthenticationException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public UnknownRequestAuthenticationException(String msg, Throwable t) {
		super(msg, t);
		// TODO Auto-generated constructor stub
	}

}
