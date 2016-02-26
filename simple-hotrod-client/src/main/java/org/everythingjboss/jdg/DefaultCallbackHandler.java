package org.everythingjboss.jdg;

import javax.security.auth.callback.*;
import javax.security.sasl.RealmCallback;

import java.io.IOException;

public class DefaultCallbackHandler implements CallbackHandler {

	final private String login;
	final private char[] password;
	final private String realm;

	public DefaultCallbackHandler(String login, char[] password, String realm) {
		this.login = login;
		this.password = password;
		this.realm = realm;
	}

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				System.out.println("NameCallback Called");
				((NameCallback) callback).setName(login);
			} else if (callback instanceof PasswordCallback) {
				System.out.println("PasswordCallback Called");
				((PasswordCallback) callback).setPassword(password);
			} else if (callback instanceof RealmCallback) {
				System.out.println("RealmCallback Called");
				((RealmCallback) callback).setText(realm);
			} else {
				throw new UnsupportedCallbackException(callback);
			}
		}
	}

}
