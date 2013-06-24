package org.dspace.install.step;

import java.util.Map;
import org.dspace.install.model.EmailServerInformation;
import org.dspace.install.model.EmailServerInformation.ConnectionType;

public class EmailServerStep extends AbstractStep {

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
		// TODO Auto-generated method stub
		
	}

}