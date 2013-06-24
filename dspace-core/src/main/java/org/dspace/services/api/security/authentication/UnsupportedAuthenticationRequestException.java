package org.dspace.services.api.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedAuthenticationRequestException extends AuthenticationException {
	private static final long serialVersionUID = -6696102807679620077L;

	public UnsupportedAuthenticationRequestException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public UnsupportedAuthenticationRequestException(String msg, Throwable t) {
		super(msg, t);
		// TODO Auto-generated constructor stub
	}

}
