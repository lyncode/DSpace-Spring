package org.dspace.services.impl.context;

import org.dspace.orm.entity.Eperson;
import org.dspace.services.api.context.Context;
import org.dspace.services.api.security.User;
import org.dspace.services.impl.security.Role.StaticRole;

public class DSpaceContext implements Context {
	private boolean ignoreAuthorization;
	private User user;
	
	public DSpaceContext (User usr) {
		this.user = usr;
	}
	
	@Override
	public Eperson getCurrentEperson() {
		return this.user.getEperson();
	}

	@Override
	public boolean ignoreAuthorization() {
		return this.ignoreAuthorization;
	}

	@Override
	public boolean isAdmin() {
		return this.user.getAuthorities().contains(StaticRole.ADMIN);
	}
	
}
