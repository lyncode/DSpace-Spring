package org.dspace.services.impl.configuration;

import java.io.File;

import org.springframework.context.ConfigurableApplicationContext;


public class DSpaceConfigurationLocator {
	private static final String DEFAULT_CONFIG_FILE = "dspace.properties";
	private static final String DIRECTORY = "config";

	public static File getConfigurationDirectory(ConfigurableApplicationContext ctx) {
		return new File(DIRECTORY);
	}

	public static String getConfigurationFilename() {
		return DEFAULT_CONFIG_FILE;
	}
}
