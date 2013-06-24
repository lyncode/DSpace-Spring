package org.dspace.services.api.context;

import org.dspace.orm.entity.Eperson;

public interface Context {
	Eperson getCurrentEperson();
	boolean ignoreAuthorization();
	boolean isAdmin();
}
