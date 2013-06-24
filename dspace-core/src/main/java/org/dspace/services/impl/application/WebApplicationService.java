package org.dspace.services.impl.application;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dspace.services.api.application.Service;
import org.dspace.services.api.application.ServiceException;
import org.dspace.services.api.configuration.ConfigurationService;
import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.dspace.services.impl.configuration.handler.HaltServiceOnChangeHandler;
import org.dspace.services.impl.configuration.handler.RefreshServiceOnChangeHandler;
import org.dspace.services.impl.context.SharedApplicationContextServer;
import org.eclipse.jetty.ajp.Ajp13SocketConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class WebApplicationService implements Service {
	private static final String ROOT_HANDLER_NAME = "root";
	private static final String WAR_EXTENSION = ".war";
	public static final int DEFAULT_HTTP_PORT = 9999;
	public static final int DEFAULT_AJP_PORT = 8013;
	private static final String DEFAULT_WEBAPPS_DIR = "webapps";
	
	private static Logger log = Logger.getLogger(WebApplicationService.class);
	
	@Autowired ApplicationContext applicationContext;
	@Autowired ConfigurationService config;
	
	private Server server;
	private Map<String, Handler> handlers;
	private Map<String, ChangeHandler> watchHandlers;
	private Map<String, Connector> connectors;

	@Override
	public synchronized void refresh() throws ServiceException {
		((ConfigurableApplicationContext) applicationContext).refresh();
	}
	
	public synchronized void refresh (String handlerName) throws ServiceException {
		if (handlers == null) throw new ServiceException("Service not initialized correctly");
		Handler handler = handlers.get(handlerName);
		if (handler != null) {
			try {
				handler.stop();
				handler.start();
			} catch (Exception e) {
				throw new ServiceException("Unable to reload module '"+handlerName+"'", e);
			}
		}
	}
	
	public String getName () {
		return "Web Application";
	}

	@Override
	public synchronized void start() throws ServiceException {
		if (this.server == null)
			throw new ServiceException("Service not correctly initialized");

		try {
			if (server.isRunning())
				server.stop();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		this.setupAvailableWebapps();
		this.setupConnectors();
		
		HandlerList list = new HandlerList();
		for (String name : handlers.keySet())
			if (this.isWebappActive(name)) 
				list.addHandler(handlers.get(name));
		
		
		server.setHandler(list);
        
        try {
			server.start();
			log.info("Web server started");
		} catch (Exception e) {
			throw new ServiceException("Unable to start web server", e);
		}
	}

	@Override
	public synchronized void stop() throws ServiceException {
		try {
			if (this.server != null)
				this.server.stop();
		} catch (Exception e) {
			throw new ServiceException("Unable to stop web server", e);
		}
	}

	private boolean isWebappActive (String name) {
		if (name.equals(ROOT_HANDLER_NAME))
			return this.config.getProperty(PropertyReference.key(this.getWebappActiveProperty(name)), Boolean.class, true);
		else
			return this.config.getProperty(PropertyReference.key(this.getWebappActiveProperty(name)), Boolean.class, false);
	}
	
	public boolean isRunning () {
		if (this.server != null)
			return this.server.isRunning();
		return false;
	}
	
	private String getWebappActiveProperty (String name) {
		String propName = "webapp."+name.toLowerCase()+".active";
		if (name.equals(ROOT_HANDLER_NAME))
			propName = "webapp.main.active";
		
		return propName;
	}
	
	private String getWebappName (File war) {
		return war.getName().toLowerCase().replace(WAR_EXTENSION, "");
	}
	
	private void setupAvailableWebapps () {
		File webappDir = new File(config.getProperty(PropertyReference.key("server.webapps"), String.class, DEFAULT_WEBAPPS_DIR));
		
		File[] files = webappDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File dir) {
				return dir.isFile() && dir.getName().toLowerCase().endsWith(WAR_EXTENSION);
			}
		});
		
		if (files == null) files = new File[0];
		
		
		for (File war : files) {
			String name = this.getWebappName(war);
			if (!handlers.containsKey(name)) {
				SharedApplicationContextServer webapp = new SharedApplicationContextServer();
				if (name.equals(ROOT_HANDLER_NAME))
					webapp.setContextPath("/");
				else
					webapp.setContextPath(name);
				
				webapp.setWar(war.getPath());
				webapp.setParentLoaderPriority(true);
				webapp.setApplicationContext(applicationContext);
				
				handlers.put(name, webapp);
				
				HaltServiceOnChangeHandler watcher = new HaltServiceOnChangeHandler(this, this.isWebappActive(name));
				config.addWatchHandler(watcher, PropertyReference.key(this.getWebappActiveProperty(name)));
				watchHandlers.put(name, watcher);
			}
		}
	}
	
	private void setupConnectors () {
		int httpPort = config.getProperty(PropertyReference.key("server.http.port"), Integer.class, DEFAULT_HTTP_PORT);
		if (config.getProperty(PropertyReference.key("server.http"), Boolean.class, true)) {
			SocketConnector connectorHTTP = new SocketConnector();
			connectorHTTP.setPort(httpPort);
			connectors.put("HTTP", connectorHTTP);
		} else connectors.remove("HTTP");
		
		if (!watchHandlers.containsKey("server.http"))
			watchHandlers.put("server.http", new HaltServiceOnChangeHandler(this, connectors.containsKey("HTTP")));
		if (!watchHandlers.containsKey("server.http.port"))
			watchHandlers.put("server.http.port", new HaltServiceOnChangeHandler(this, httpPort));
		
		
		int ajpPort = config.getProperty(PropertyReference.key("server.ajp.port"), Integer.class, DEFAULT_AJP_PORT);
		if (config.getProperty(PropertyReference.key("server.ajp"), Boolean.class, false)) {
			Ajp13SocketConnector ajp = new Ajp13SocketConnector();
			ajp.setPort(ajpPort);
			connectors.put("AJP", ajp);
		} else connectors.remove("AJP");
		
		if (!watchHandlers.containsKey("server.ajp"))
			watchHandlers.put("server.ajp", new HaltServiceOnChangeHandler(this, connectors.containsKey("AJP")));
		if (!watchHandlers.containsKey("server.ajp.port"))
			watchHandlers.put("server.ajp.port", new HaltServiceOnChangeHandler(this, ajpPort));
 
		// register the connector
		server.setConnectors(connectors.values().toArray(new Connector[0]));
	}
	
	public void init () {
		this.server = new Server();
		this.server.setGracefulShutdown(1000);
	    this.server.setStopAtShutdown(true);
		this.handlers = new HashMap<String, Handler>();
		this.watchHandlers = new HashMap<String, ChangeHandler>();
		this.watchHandlers.put("dspace.installed", new RefreshServiceOnChangeHandler(this, config.isInstalled()));
		config.addWatchHandler(this.watchHandlers.get("dspace.installed"), PropertyReference.INSTALLED);
		this.connectors = new HashMap<String, Connector>();
	}
	
	public boolean isStarting () {
		if (this.server == null) return false;
		else {
			return this.server.isStarting();
		}
	}
	
	public boolean isStopping () {
		if (this.server == null) return false;
		else {
			return this.server.isStopping();
		}
	}
 

	@Override
	public void destroy() throws ServiceException {
		this.stop();
		this.server.destroy();
		for (String key : this.watchHandlers.keySet()) 
			this.config.removeWatchHandler(this.watchHandlers.get(key), PropertyReference.key(key));
		this.server = null;
		this.handlers = null;
		this.watchHandlers = null;
		this.connectors = null;
	}

}
