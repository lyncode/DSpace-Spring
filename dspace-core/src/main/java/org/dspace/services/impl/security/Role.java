package org.dspace.services.impl.security;

import org.springframework.security.core.GrantedAuthority;

public class Role implements GrantedAuthority {
	private static final long serialVersionUID = -8483700184617009271L;
	private StaticRole role;
	
	public Role () {
		this.role = StaticRole.MEMBER;
	}
	
	public Role (StaticRole role) {
		this.role = role;
	}
	
	public String getAuthority() {
		return "ROLE_"+role.name();
	}

	public enum StaticRole {
		ADMIN,
		MEMBER
	}
	
	public boolean equals (Object obj) {
		if (obj instanceof Role) {
			return ((Role) obj).getAuthority().equals(this.getAuthority());
		} else if (obj instanceof StaticRole) {
			return new Role(((StaticRole) obj)).equals(this);
		} else return super.equals(obj);
	}
}
