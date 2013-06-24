package org.dspace.services.api.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class CannotLoginException extends AuthenticationException {
	private static final long serialVersionUID = -6696102807679620077L;

	public CannotLoginException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public CannotLoginException(String msg, Throwable t) {
		super(msg, t);
		// TODO Auto-generated constructor stub
	}

}
