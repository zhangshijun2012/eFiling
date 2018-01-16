package com.sinosoft.mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * 邮件设置
 * 
 * @author LuoGang
 *
 */
public class MailSettings implements Cloneable {
	/** mail.user */
	public static final String KEY_USER = "mail.user";
	/** mail.password */
	public static final String KEY_PASSWORD = "mail.password";
	/** mail.from */
	public static final String KEY_FROM = "mail.from";
	/** mail.host */
	public static final String KEY_HOST = "mail.host";
	/** mail.port */
	public static final String KEY_PORT = "mail.port";

	/** mail.transport.protocol */
	public static final String KEY_TRANSPORT_PROTOCOL = "mail.transport.protocol";
	/** mail.store.protocol */
	public static final String KEY_STORE_PROTOCOL = "mail.store.protocol";

	/** mail.encoding */
	public static final String KEY_ENCODING = "mail.encoding";

	/** smtp */
	public static final String SMTP = "smtp";
	/** POP3 */
	public static final String POP3 = "pop3";
	/** imap */
	public static final String IMAP = "imap";

	public MailSettings() {
		super();
	}

	public MailSettings(Properties props) {
		super();
		this.setProps(props);
	}

	/** 注入的变量 */
	private Properties props;

	/**
	 * 返回Properties对象，此对象可用于初始化javax.mail.Session
	 * 
	 * @return
	 */
	public Properties props() {
		if (props == null) props = System.getProperties(); // 默认使用当前系统变量
		props.setProperty(KEY_TRANSPORT_PROTOCOL, transportProtocol); // 使用的协议
		props.setProperty(KEY_STORE_PROTOCOL, storeProtocol); // 使用的协议

		props.setProperty(KEY_HOST, transportHost);
		props.setProperty("mail." + transportProtocol + ".host", transportHost);
		if (transportPort > -1) {
			props.setProperty(KEY_PORT, Integer.toString(transportPort));
			props.setProperty("mail." + transportProtocol + ".port", Integer.toString(transportPort));
		}
		props.setProperty("mail." + transportProtocol + ".auth", Boolean.toString(this.isAuth()));

		if (user != null) {
			props.setProperty(KEY_USER, user);
			props.setProperty("mail." + transportProtocol + ".user", user);
		}
		String from = this.from;
		if (from == null) from = user;
		if (from != null) {
			props.setProperty(KEY_FROM, from);
			props.setProperty("mail." + transportProtocol + ".from", from);
		}

		props.setProperty(KEY_ENCODING, this.encoding);

		_authenticator = null;
		if (this.isAuth() && this.authenticator == null) {
			_authenticator = new MailAuthenticator(this.user, this.password);
		}

		return props;
	}

	/** 邮件认证对象 */
	private Authenticator authenticator;
	/** 如果authenticator为null,次字段为自动生成的认证对象 */
	private Authenticator _authenticator;
	private String user;
	private String from;
	private String password;

	/** 发送邮件协议 */
	private String transportHost;
	private int transportPort = -1;
	private String transportProtocol = SMTP;

	/** 读取文件的协议 */
	private String storeProtocol = POP3;
	private String storeHost;
	private int storePort = -1;

	/** 是否需要校验用户名和密码 */
	private Boolean auth;

	/** 发送普通文本是邮件正文的编码，默认为GBK */
	private String encoding = "GBK";

	/**
	 * 邮件用户
	 * 
	 * @return
	 */
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * 登陆密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 发件人
	 * 
	 * @return
	 */
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * 得到发件人地址
	 * 
	 * @return
	 */
	public Address getFromAddres() {
		String addr = from == null ? user : from;
		if (addr == null) return null;
		InternetAddress[] a = null;
		try {
			a = InternetAddress.parse(addr);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a != null && a.length > 0 ? a[0] : null;
	}

	/**
	 * 使用的协议,默认为SMTP
	 * 
	 * @return
	 */
	public String getTransportProtocol() {
		return transportProtocol;
	}

	public void setTransportProtocol(String protocol) {
		this.transportProtocol = protocol;
	}

	/**
	 * 邮件服务器地址
	 * 
	 * @return
	 */
	public String getTransportHost() {
		return transportHost;
	}

	public void setTransportHost(String host) {
		this.transportHost = host;
	}

	/**
	 * 服务器端口
	 * 
	 * @return
	 */
	public int getTransportPort() {
		return transportPort;
	}

	public void setTransportPort(int port) {
		this.transportPort = port;
	}

	/**
	 * 读取邮件的协议，默认为POP3
	 * 
	 * @return
	 */
	public String getStoreProtocol() {
		return storeProtocol;
	}

	public void setStoreProtocol(String storeProtocol) {
		this.storeProtocol = storeProtocol;
	}

	public String getStoreHost() {
		return storeHost;
	}

	public void setStoreHost(String storeHost) {
		this.storeHost = storeHost;
	}

	public int getStorePort() {
		return storePort;
	}

	public void setStorePort(int storePort) {
		this.storePort = storePort;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	/**
	 * 是否需要进行密码校验
	 * 
	 * @return
	 */
	public boolean isAuth() {
		if (auth == null) return this.user != null || this.password != null;
		return auth.booleanValue();
	}

	/**
	 * 邮件认证对象
	 * 
	 * @return
	 * @see javax.mail.Session#getInstance(Properties, Authenticator)
	 */
	public Authenticator getAuthenticator() {
		return authenticator == null ? _authenticator : authenticator;
	}

	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	/**
	 * 邮件文本编码
	 * 
	 * @return
	 */
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 得到注入的props对象，应当在props方法后调用
	 * 
	 * @return
	 */
	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
		if (props == null) return;

		if (props.containsKey(KEY_USER)) this.user = props.getProperty(KEY_USER);
		if (props.containsKey(KEY_PASSWORD)) this.password = props.getProperty(KEY_PASSWORD);
		if (props.containsKey(KEY_TRANSPORT_PROTOCOL)) {
			this.transportProtocol = props.getProperty(KEY_TRANSPORT_PROTOCOL);
		}
		if (props.containsKey(KEY_STORE_PROTOCOL)) {
			this.storeProtocol = props.getProperty(KEY_STORE_PROTOCOL);
		}

		if (props.containsKey(KEY_HOST)) {
			this.transportHost = props.getProperty(KEY_HOST);
			this.storeHost = props.getProperty(KEY_HOST);
		}
		if (props.containsKey(KEY_PORT)) {
			try {
				int port = Integer.parseInt(props.getProperty(KEY_PORT));
				this.transportPort = port;
				this.storePort = port;
			} catch (NumberFormatException e) {
				// TODO
			}
		}

		String key = "mail." + this.transportProtocol + ".host";
		if (props.containsKey(key)) {
			this.transportHost = props.getProperty(key);
		}
		key = "mail." + this.transportProtocol + ".port";
		if (props.containsKey(key)) {
			try {
				this.transportPort = Integer.parseInt(props.getProperty(key));
			} catch (NumberFormatException e) {
				// TODO
			}
		}

		key = "mail." + this.storeProtocol + ".host";
		if (props.containsKey(key)) {
			this.storeHost = props.getProperty(key);
		}
		key = "mail." + this.storeProtocol + ".port";
		if (props.containsKey(key)) {
			try {
				this.storePort = Integer.parseInt(props.getProperty(key));
			} catch (NumberFormatException e) {
				// TODO
			}
		}

		if (props.containsKey(KEY_FROM)) this.from = props.getProperty(KEY_FROM);
		if (props.containsKey(KEY_ENCODING)) this.encoding = props.getProperty(KEY_ENCODING);
	}

	@Override
	public MailSettings clone() {
		MailSettings settings;
		try {
			settings = (MailSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			settings = new MailSettings();
			settings.transportProtocol = this.transportProtocol;
			settings.storeProtocol = this.storeProtocol;
			settings.transportHost = this.transportHost;
			settings.transportPort = this.transportPort;
			settings.user = this.user;
			settings.password = this.password;
			settings.authenticator = this.authenticator;
			settings.auth = this.auth;
			settings.from = this.from;
			settings.encoding = this.encoding;
		}
		settings.props = (Properties) props.clone();

		return settings;
	}

}
