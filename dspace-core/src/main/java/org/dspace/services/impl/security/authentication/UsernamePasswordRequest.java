package org.dspace.services.impl.security.authentication;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class UsernamePasswordRequest implements Authentication {
	private static final long serialVersionUID = -2354679951654649567L;
	private String username;
	private String password;
	
	public UsernamePasswordRequest (String email, String password) {
		this.username = email;
		this.password = password;
	}
	
	public String getEmail () {
		return this.username;
	}
	
	public String getPassword () {
		return this.password;
	}
	
	public String getName() {
		return this.getEmail();
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	public Object getCredentials() {
		return this.getPassword();
	}

	public Object getDetails() {
		return null;
	}

	public Object getPrincipal() {
		return null;
	}

	public boolean isAuthenticated() {
		return false;
	}

	public void setAuthenticated(boolean isAuthenticated)
			throws IllegalArgumentException {
		
	}
	
}
