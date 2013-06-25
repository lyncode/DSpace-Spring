package org.dspace.services.impl.context;

import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SuppressWarnings("unchecked")
public class SharedApplicationContextServer extends WebAppContext implements ApplicationContextAware {
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.getServletContext().setAttribute(ContextApplicationLoader.PARENT_APPLICATION_CONTEXT, applicationContext);
	}
	
	
}
