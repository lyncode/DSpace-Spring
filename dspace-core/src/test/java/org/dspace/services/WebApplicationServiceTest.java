package org.dspace.services;

import static org.junit.Assert.*;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.dspace.services.api.application.ServiceException;
import org.dspace.services.impl.application.ConfigureLoggerService;
import org.dspace.services.impl.application.WebApplicationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/applicationContext.xml"})
public class WebApplicationServiceTest {

	@Autowired WebApplicationService webService;
	@Autowired ConfigureLoggerService logService;

	@Before
	public void setUp() throws Exception {
		logService.init();
		logService.start();
	}
	
	@Test
	public void testWebapplicationService() throws Exception {
		webService.init();
		webService.start();
		assertTrue(webService.isRunning());
		// Wait?
		while (webService.isStarting())
			Thread.sleep(1500);
		URLConnection conn;
		conn = new URL("http://localhost:"+WebApplicationService.DEFAULT_HTTP_PORT).openConnection();
		conn.connect();
		InputStream input = conn.getInputStream();
		assertNotNull(input);
		input.close();
		webService.destroy();
		while (webService.isStopping())
			Thread.sleep(1500);
		assertFalse(webService.isRunning());
	}
	
	@Test
	public void testWebapplicationServiceRefresh() throws Exception {
		webService.init();
		webService.start();
		assertTrue(webService.isRunning());
		// Wait?
		while (webService.isStarting())
			Thread.sleep(1500);
		URLConnection conn;
		conn = new URL("http://localhost:"+WebApplicationService.DEFAULT_HTTP_PORT).openConnection();
		conn.connect();
		InputStream input = conn.getInputStream();
		assertNotNull(input);
		input.close();
		webService.destroy();
		webService.init();
		webService.start();
		while (webService.isStopping() || webService.isStarting() || !webService.isRunning())
			Thread.sleep(1500);
		assertTrue(webService.isRunning());
		conn = new URL("http://localhost:"+WebApplicationService.DEFAULT_HTTP_PORT).openConnection();
		conn.connect();
		input = conn.getInputStream();
		assertNotNull(input);
		input.close();
		webService.destroy();
		while (webService.isStopping())
			Thread.sleep(1500);
		assertFalse(webService.isRunning());
	}

	@Test(expected=ServiceException.class)
	public void testUninitializedWebapplicationStart () throws ServiceException {
		assertFalse(webService.isRunning());
		webService.start();
	}

	@Test
	public void testUninitializedWebapplicationStop () throws ServiceException {
		assertFalse(webService.isRunning());
		webService.stop();
	}
}
