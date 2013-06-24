package org.dspace.services.impl.security.authentication;

import java.util.Collection;

import org.dspace.services.api.security.authentication.AuthenticationResponse;
import org.springframework.security.core.GrantedAuthority;


public class UsernamePasswordAuthenticationResponse implements AuthenticationResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9106639649162327267L;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAuthenticated(boolean arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
