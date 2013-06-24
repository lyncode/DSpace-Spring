/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.orm;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;


import org.dspace.install.model.DatabaseInformation;
import org.dspace.orm.entity.IDSpaceObject;
import org.dspace.services.api.configuration.ConfigurationService;
import org.dspace.services.api.configuration.reference.Module;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 * This session factory builder uses the DSpace configuration.
 * 
 * @author João Melo <jmelo@lyncode.com>
 */
public class DSpaceSessionFactoryBuilder  {
	@Autowired ConfigurationService config;
	private DataSource datasource;
	
	private static LocalSessionFactoryBean local;
	private static SessionFactory sessionFac = null;
	
	public DataSource getDataSource() {
		return datasource;
	}

	public void setDataSource(DataSource datasource) {
		this.datasource = datasource;
	}
	


	public SessionFactory createInstall (DatabaseInformation info, DataSource datasource) throws IOException {
		LocalSessionFactoryBean local = new LocalSessionFactoryBean();
		local.setDataSource(datasource);
		local.setPackagesToScan(IDSpaceObject.class.getPackage().getName());
		Properties prop = new Properties();
		prop.put("hibernate.dialect", info.getDialectClass().getName());
		prop.put("hibernate.connection.autocommit", false);
		prop.put("hibernate.current_session_context_class", "thread");
		prop.put("hibernate.show_sql", true);
		prop.put("hibernate.hbm2ddl.auto", "create");
		
		local.setHibernateProperties(prop);
		local.afterPropertiesSet();
		SessionFactory sessionFac = local.getObject();
		return sessionFac;
	}


	public SessionFactory create () throws IOException {
		if (!config.isInstalled()) return null;
		if (sessionFac == null) {
			local = new LocalSessionFactoryBean();
			local.setDataSource(datasource);
			local.setPackagesToScan(IDSpaceObject.class.getPackage().getName());
			Properties prop = new Properties();
			
			String dbdialect = config.getProperty(PropertyReference.key(Module.DATABASE, "dialect"));
			prop.put("hibernate.dialect", dbdialect);
			prop.put("hibernate.connection.autocommit", false);
			prop.put("hibernate.current_session_context_class", "thread");
			prop.put("hibernate.show_sql", true);
			
			local.setHibernateProperties(prop);
			local.afterPropertiesSet();
			sessionFac = local.getObject();
		}
		return sessionFac;
	}
}
