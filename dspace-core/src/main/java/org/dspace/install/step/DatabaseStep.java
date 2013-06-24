package org.dspace.install.step;

import java.util.Map;
import org.dspace.install.model.DatabaseInformation;

public class DatabaseStep extends AbstractStep {

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
		int port = 0;
		try {
			port = Integer.parseInt(portI);
		} catch (Exception e) {
			throw new InstallException("Malformed port number", e);
		}
		DatabaseInformation connection = new DatabaseInformation(host, username, password, schema, port);
		try {
			connection.test();
		} catch (Exception e) {
			throw new InstallException("Unable to connect to database", e);
		}
		return connection;
	}

	@Override
	public void install(Object values) throws InstallException {
		// TODO Auto-generated method stub
		
	}
	
}
