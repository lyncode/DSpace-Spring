package org.dspace.services.impl.context;

import org.dspace.services.api.context.Context;
import org.dspace.services.api.context.ContextService;
import org.dspace.services.impl.security.User;

public class DSpaceContextService implements ContextService {
	private ThreadLocal<Context> context;
	
	public DSpaceContextService () {
		context = new ThreadLocal<Context>();
		context.set(null);
	}
	
	@Override
	public Context getContext() {
		Context ctx = this.context.get();
		if (ctx == null) {
			return this.newContext();
		} else return ctx;
	}

	@Override
	public Context newContext() {
		this.context.set(new DSpaceContext(User.getCurrentUser()));
		return this.context.get();
	}

}
