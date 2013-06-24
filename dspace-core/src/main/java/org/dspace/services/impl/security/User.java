package org.dspace.services.impl.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dspace.orm.dao.content.PredefinedGroup;
import org.dspace.orm.entity.Eperson;
import org.dspace.services.impl.security.Role.StaticRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


public class User implements org.dspace.services.api.security.User, Serializable {
	private static final long serialVersionUID = 4466828947980026611L;
	private Eperson eperson;
	
	public static User getCurrentUser () {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) 
				return (User) auth;
			else
				return new User();
		} catch (Exception e) {
			return new User();
		}
	}
	
	public User () {
		// guest
	}
	
	public User (Eperson eperson) {
		this.eperson = eperson;
	}

	@Override
	public String getName() {
		if (this.isAuthenticated()) return this.eperson.getName();
		else return "Guest";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<Role> roles = new ArrayList<Role>();
		if (this.isAuthenticated()) {
			if (this.eperson.memberOf(PredefinedGroup.ADMIN))
				roles.add(new Role(StaticRole.ADMIN));
			else
				roles.add(new Role(StaticRole.MEMBER));
		}
		return roles;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		return (this.eperson != null);
	}

	@Override
	public void setAuthenticated(boolean arg0) throws IllegalArgumentException {
		// Nothing
	}

	@Override
	public Eperson getEperson() {
		return this.eperson;
	}
}
