package com.sinosoft.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 密码验证对象
 * 
 * @author LuoGang
 *
 */
public class MailAuthenticator extends Authenticator {

	public MailAuthenticator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MailAuthenticator(String user, String password) {
		super();
		this.user = user;
		this.password = password;
	}

	/** 用户名 */
	private String user;
	/** 密码 */
	private String password;

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		// TODO Auto-generated method stub
		return new PasswordAuthentication(user, password);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
