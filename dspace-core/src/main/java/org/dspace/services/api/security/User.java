package org.dspace.services.api.security;

import org.dspace.orm.entity.Eperson;
import org.springframework.security.core.Authentication;


public interface User extends Authentication {
	Eperson getEperson ();
}
