package org.dspace.services;

import static org.junit.Assert.*;
import org.dspace.services.api.configuration.ConfigurationService;
import org.dspace.services.api.configuration.reference.PropertyReference;
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
		assertTrue(config.getProperty(PropertyReference.key("unexisting.property"), Boolean.class, true));
		assertFalse(config.getProperty(PropertyReference.key("unexisting.property"), Boolean.class, false));
		assertNull(config.getProperty(PropertyReference.key("unexisting.property"), Boolean.class));
	}
	
	@Test
	public void testEnviromentVariables () {
		assertNotNull(config.getProperty(PropertyReference.key("user.home")));
	}
	
	@Test
	public void testAddProperty () {
		 assertTrue(config.addProperty(PropertyReference.key("test"), "test"));
		 assertFalse(config.addProperty(PropertyReference.key("test"), "joao"));
		 assertTrue(config.removeProperty(PropertyReference.key("test")));
	}

}
