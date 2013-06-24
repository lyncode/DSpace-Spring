package org.dspace.install.model;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailServerInformation implements InstallObject {
	
	public enum ConnectionType {
		NONE,
		SSL,
		TLS
	}
	
	private String host;
	private int port;
	private boolean hasAuthentication;
	private ConnectionType connection;
	
	private String username;
	private String password;
	

	public EmailServerInformation(String host, int port) {
		this.host = host;
		this.port = port;
		this.hasAuthentication = false;
		this.connection = ConnectionType.NONE;
	}
	
	public EmailServerInformation (String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;
		
		this.hasAuthentication = (user != null);
		this.connection = ConnectionType.NONE;
		
		this.username = user;
		this.password = pass;
	}
	
	public ConnectionType getConnection () {
		return this.connection;
	}

	/**
	 * @param useSSL the useSSL to set
	 */
	public void setConnection(ConnectionType type) {
		this.connection = type;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the hasAuthentication
	 */
	public boolean hasAuthentication() {
		return hasAuthentication;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	
	public void test (String testEmail) throws Exception {
		final String username = this.getUsername();
		final String password = this.getPassword();
		Properties props = new Properties();
		

		props.put("mail.smtp.host", this.getHost());
		props.put("mail.smtp.port", this.getPort()+"");
		if (this.connection == ConnectionType.TLS) {
			props.put("mail.smtp.starttls.enable", "true");
		} else if (this.connection == ConnectionType.SSL) {
			props.put("mail.smtp.socketFactory.port", this.getPort()+"");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		
		Authenticator auth = null;
		if (this.hasAuthentication()) {
	        props.put("mail.smtp.auth", "true");
	        auth = new Authenticator() {
	        	protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			};
		}
		
		
        // or use getDefaultInstance instance if desired...
        Session session = Session.getInstance(props, auth);

		Message message = new MimeMessage(session);
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(testEmail));
		message.setSubject("DSpace : Testing SMTP Configuration");
		message.setText("Success!");
		Transport.send(message);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object deepClone() {
		EmailServerInformation instance = new EmailServerInformation(this.getHost(), this.getPort(), this.getUsername(), this.getPassword());
		instance.setConnection(this.getConnection());
		return instance;
	}

	
}
