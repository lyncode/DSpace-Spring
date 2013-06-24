package org.dspace.install.model;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseInformation implements InstallObject {
	private String host;
	private String user;
	private String pass;
	private String schema;
	private int port;
	
	public DatabaseInformation(String host, String user, String pass,
			String schema, int port) {
		super();
		this.host = host;
		this.user = user;
		this.pass = pass;
		this.schema = schema;
		this.port = port;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	public void test () throws Exception {
		Class.forName(org.postgresql.Driver.class.getName());
		Connection connection = null;
		connection = DriverManager.getConnection("jdbc:postgresql://"+host+":"+port+"/"+schema, user, pass);
		connection.close();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object deepClone() {
		return new DatabaseInformation(this.getHost(), this.getUser(), this.getPass(), this.getSchema(), this.getPort());
	}
}
