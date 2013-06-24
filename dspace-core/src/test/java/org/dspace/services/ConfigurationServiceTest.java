package org.dspace.services;

import static org.junit.Assert.*;
import org.dspace.services.api.configuration.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/applicationContext.xml"})
public class ConfigurationServiceTest {
	
	@Autowired ConfigurationService config;

	@Test
	public void testNullCases() {
		assertNotNull(config);
		assertTrue(config.getProperty("unexisting.property", Boolean.class, true));
		assertFalse(config.getProperty("unexisting.property", Boolean.class, false));
		assertNull(config.getProperty("unexisting.property", Boolean.class));
	}
	
	@Test
	public void testEnviromentVariables () {
		assertNotNull(config.getProperty("user.home"));
	}
	
	@Test
	public void testAddProperty () { // Without configuration file setup it must not be able to add the property
		 assertFalse(config.addProperty("test", "test"));
	}

}
