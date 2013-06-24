/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.orm;

import org.dspace.install.model.DatabaseInformation;
import org.dspace.services.api.configuration.ConfigurationService;
import org.dspace.services.api.configuration.reference.PropertyReference;
import org.dspace.services.api.configuration.reference.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


/**
 * This data source builder uses the current DSpace configuration file.
 * 
 * @author João Melo <jmelo@lyncode.com>
 */
public class DSpaceDataSourceBuilder {
	@Autowired ConfigurationService config;
	private static DriverManagerDataSource driver;
	
	public DriverManagerDataSource create () {
		if (!config.isInstalled()) return null;
		if (driver == null) {
			driver = new DriverManagerDataSource();
			driver.setDriverClassName(config.getProperty(PropertyReference.key(Module.DATABASE, "driver")));
			
			driver.setUrl(config.getProperty(PropertyReference.key(Module.DATABASE, "url")));
			String user = config.getProperty(PropertyReference.key(Module.DATABASE, "username"));
			if (user != null) {
				driver.setUsername(user);
				String pass = config.getProperty(PropertyReference.key(Module.DATABASE, "password"));
				if (pass != null) driver.setPassword(pass);
			}
		}
		return driver;
	}

	public DriverManagerDataSource createInstall (DatabaseInformation information) {
		DriverManagerDataSource driver = new DriverManagerDataSource();
		driver = new DriverManagerDataSource();
		driver.setDriverClassName(information.getDriverClass().getName());
		driver.setUrl(information.getURL());
		driver.setUsername(information.getUser());
		driver.setPassword(information.getPass());
		return driver;
	}
	
}
