package com.moonic.mail;

import javax.mail.*;

public class MailAuthenticator extends Authenticator {
	String username = null;
	String password = null;

	public MailAuthenticator() {
	}
	
	public MailAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
}