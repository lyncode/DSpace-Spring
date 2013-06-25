package org.dspace.install.step;

import java.io.IOException;
import java.util.Map;
import org.dspace.install.model.DatabaseInformation;
import org.dspace.orm.DSpaceDataSourceBuilder;
import org.dspace.orm.DSpaceSessionFactoryBuilder;
import org.dspace.services.api.configuration.ConfigurationService;
import org.dspace.services.api.configuration.reference.Module;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.dspace.services.api.configuration.reference.PropertyReference.*;

public class DatabaseStep extends AbstractStep {
	@Autowired DSpaceDataSourceBuilder datasourceBuilder;
	@Autowired DSpaceSessionFactoryBuilder sessionBuilder;
	@Autowired ConfigurationService config;

	@Override
	public void prepare(Map<String, Object> model) {
		
	}

	@Override
	public DatabaseInformation validate(Map<String, Object> request) throws InstallException {
		String portI = (String) request.get("port");
		String host = (String) request.get("host");
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		String schema = (String) request.get("schema");
		String dialect = (String) request.get("dialect");
		int port = 0;
		try {
			port = Integer.parseInt(portI);
		} catch (Exception e) {
			throw new InstallException("Malformed port number", e);
		}
		try {
			Class<?> dialectClass = Class.forName(dialect);
			Class<?> driverClass = org.postgresql.Driver.class;
			if (this.isOracle(dialect)) 
				driverClass = Class.forName("oracle.jdbc.OracleDriver");
			DatabaseInformation connection = new DatabaseInformation(host, username, password, schema, port, driverClass, dialectClass);
			connection.test();
			return connection;
		} catch (Exception e) {
			throw new InstallException("Unable to connect to database", e);
		}
	}
	
	private boolean isOracle (String dialect) {
		if (dialect.equals(org.hibernate.dialect.Oracle8iDialect.class.getName()))
			return true;
		if (dialect.equals(org.hibernate.dialect.Oracle9iDialect.class.getName()))
			return true;
		if (dialect.equals(org.hibernate.dialect.Oracle10gDialect.class.getName()))
			return true;
		return false;
	}

	@Override
	public void install(Object values) throws InstallException {
		DatabaseInformation information = (DatabaseInformation) values;
		SessionFactory session;
		try {
			// create database
			session = sessionBuilder.createInstall(information, datasourceBuilder.createInstall(information));
			session.openSession();
			session.close();
			
			// store configuration
			config.addProperty(key(Module.DATABASE, "url"), information.getURL());
			config.addProperty(key(Module.DATABASE, "username"), information.getUser());
			config.addProperty(key(Module.DATABASE, "password"), information.getPass());
			config.addProperty(key(Module.DATABASE, "driver"), information.getDriverClass().getName());
			config.addProperty(key(Module.DATABASE, "dialect"), information.getDialectClass().getName());
			
			
		} catch (IOException e) {
			throw new InstallException(e);
		}
	}
	
}
