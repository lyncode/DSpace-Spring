package org.dspace.services.api.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public abstract class GenericAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication request)
			throws AuthenticationException {
		if (!(request instanceof AuthenticationRequest))
			throw new UnknownRequestAuthenticationException("Unknown authentication request");
		else
			return auth((AuthenticationRequest) request);
				
	}
	
	public abstract AuthenticationResponse auth (AuthenticationRequest request);

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean supports(Class<?> arg0) {
		if (arg0.isAssignableFrom(AuthenticationRequest.class))
			return this.supportsRequest((Class<? super AuthenticationRequest>) arg0);
		else
			return false;
	}

	public abstract boolean supportsRequest(Class<? super AuthenticationRequest> request);
	
}
