package org.dspace.install.step;

import java.util.Map;
import org.dspace.install.model.EmailServerInformation;
import org.dspace.install.model.EmailServerInformation.ConnectionType;
import org.dspace.services.api.configuration.ConfigurationService;
import org.dspace.services.api.configuration.reference.Module;
import org.springframework.beans.factory.annotation.Autowired;

import static org.dspace.services.api.configuration.reference.PropertyReference.*;

public class EmailServerStep extends AbstractStep {
	@Autowired ConfigurationService config;

	@Override
	public void prepare(Map<String, Object> model) {
	}

	@Override
	public EmailServerInformation validate(Map<String, Object> request) throws InstallException {
		String host = (String) request.get("host");
		String testEmail = (String) request.get("test");
		String connection = (String) request.get("connection");
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		String portI = (String) request.get("port");
		String ignoreButton = (String) request.get("ignore");
		
		
		if (ignoreButton == null) {
			int port = 0;
			try {
				port = Integer.parseInt(portI);
			} catch (Exception e) {
				throw new InstallException("Malforme port number", e);
			}
			EmailServerInformation email = new EmailServerInformation(host, port, username, password);
			email.setConnection(ConnectionType.valueOf(connection.toUpperCase()));
			
			try {
				email.test(testEmail);
				return email;
			} catch (Exception e) {
				throw new InstallException("Unable to send test message", e);
			}
		}
		
		return null;
	}

	@Override
	public void install(Object values) throws InstallException {
		EmailServerInformation info = (EmailServerInformation) values;

		config.addProperty(key(Module.EMAIL, "server.host"), info.getHost());
		config.addProperty(key(Module.EMAIL, "server.port"), info.getPort());
		config.addProperty(key(Module.EMAIL, "server.type"), info.getConnection().name());
		if (info.hasAuthentication()) {
			config.addProperty(key(Module.EMAIL, "server.auth.user"), info.getUsername());
			config.addProperty(key(Module.EMAIL, "server.auth.pass"), info.getPassword());
		}
	}

}
