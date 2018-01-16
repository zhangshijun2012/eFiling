package com.sinosoft.efiling.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.sinosoft.util.CollectionHelper;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;

/**
 * 用于邮件发送
 * 
 * @author LuoGang
 * 
 */
public class MailHelper {
	/** 日志记录对象 */
	private static Logger logger = Logger.getLogger("mail");

	/**
	 * 是否为测试系统,如果是,则所有邮件发送给TO
	 * 
	 * @see #TO
	 */
	public static boolean DEBUG = StringHelper.parseBoolean(SystemHelper.getProperty("mail.debug"));
	/** 测试系统的收件地址 */
	public static String TO = StringHelper.trim(SystemHelper.getProperty("mail.debug.to"));

	/** 是否禁用邮件功能 */
	public static boolean disabled = StringHelper.parseBoolean(SystemHelper.getProperty("mail.disabled"));
	/** 使用的编码 */
	private static String encoding = SystemHelper.getProperty("mail.encoding", SystemHelper.ENCODING);

	private static String protocol;// = SystemHelper.getProperty("mail.transport.protocol", "smtp");
	private static String host;// = SystemHelper.getProperty("mail." + protocol + ".host");
	private static boolean auth;// = StringHelper.parseBoolean(SystemHelper.getProperty("mail." + protocol + ".auth",
								// "true"));

	/** 登录名 */
	private static String user;// = SystemHelper.getProperty("mail.user");
	/** 密码 */
	private static String password;// = SystemHelper.getProperty("mail.password");

	/** 默认的发件箱 */
	private static InternetAddress from;

	/** 邮件配置 */
	public static final Properties props = SystemHelper.props;

	/** 发邮件的Session */
	private static Session session;

	static {
		try {
			setProperties(props);
		} catch (Exception e) {
			// TODO
		}
	}

	/**
	 * 得到默认的session对象
	 * 
	 * @return
	 */
	public static Session getDefaultSession() {
		if (disabled) logger.info("mail.disabled=true,已禁用邮件功能!");

		if (disabled || session != null) return session;
		Properties props = MailHelper.props; // System.getProperties();
		// 防止未进行设置
		String key = "mail.transport.protocol";
		if (!props.containsKey(key)) props.put(key, protocol);
		key = "mail." + protocol + ".host";
		if (!props.containsKey(key)) props.put(key, host);
		key = "mail." + protocol + ".auth";
		if (!props.containsKey(key)) props.put(key, Boolean.toString(auth)); // Properties中的属性必须全是字符串

		if (auth) {
			// 有校验
			session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});
		} else {
			// 无校验
			session = Session.getInstance(props, null);
		}
		return session;
	}

	/**
	 * 配置属性
	 * 
	 * @return
	 */
	public static Properties getProps() {
		return props;
	}

	/**
	 * 更改配置,会使用propsx中的属性替换当前props中的属性，如果propsx没有指定，则依然使用当前props中的属性
	 * 
	 * @param propsx
	 */
	public static void setProperties(Properties propsx) {
		logger.debug("切换邮件配置:" + propsx);
		props.putAll(propsx);
		disabled = StringHelper.parseBoolean(props.getProperty("mail.disabled", Boolean.toString(disabled)));
		encoding = props.getProperty("mail.encoding", SystemHelper.ENCODING);
		DEBUG = StringHelper.parseBoolean(props.getProperty("mail.debug", Boolean.toString(DEBUG)));
		TO = StringHelper.trim(props.getProperty("mail.debug.to", TO));

		protocol = props.getProperty("mail.transport.protocol", "smtp"); // 默认为SMTP协议

		host = props.getProperty("mail." + protocol + ".host");
		user = props.getProperty("mail.user");
		password = props.getProperty("mail.password");

		if (!StringHelper.isEmpty(user) && !StringHelper.isEmpty(password)) {
			// 如果有用户名和密码默认为需要校验
			auth = true;
		} else {
			auth = false;
		}
		auth = StringHelper.parseBoolean(props.getProperty("mail." + protocol + ".auth", Boolean.toString(auth)));

		from = null;
		String _from = props.getProperty("mail.from");
		if (!StringHelper.isEmpty(_from)) {
			try {
				from = new InternetAddress(_from);
				logger.debug("from=" + from);
				from.setPersonal(from.getPersonal(), encoding);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.debug("from=" + from);

		session = getDefaultSession();
	}

	/**
	 * 发送邮件
	 * 
	 * @param recipients
	 * @param subject
	 * @param content
	 * @return
	 */
	public static boolean send(String[] recipients, String subject, String content) {
		return MailHelper.send(recipients, subject, content, null);
	}

	/**
	 * 发送邮件
	 * 
	 * @param recipients 收件人
	 * @param subject 主题
	 * @param content 正文内容,html文档
	 * @param from 发件人信息
	 * @return
	 */
	public static boolean send(String[] recipients, String subject, String content, String from) {
		if (disabled) return false;
		logger.info("发送邮件\r\n\t收件人:" + StringHelper.join(recipients) + ",\t发件人:" + from + "\r\n\t邮件标题:" + subject);
		MimeMessage message = new MimeMessage(session);
		try {
			InternetAddress _from = null;
			if (StringHelper.isEmpty(from)) {
				_from = MailHelper.from;
				logger.debug("发件人使用测试配置:" + _from);
			} else {
				_from = new InternetAddress(from);
				try {
					_from.setPersonal(_from.getPersonal(), encoding);
				} catch (UnsupportedEncodingException e) {
					// TODO
					logger.error("发件人名称编码转换失败" + _from.getPersonal(), e);
				}
			}
			if (_from != null) message.setFrom(_from); // 发送地址

			if (DEBUG && !StringHelper.isEmpty(TO)) {
				// 配置UAT测试时的邮件地址
				recipients = TO.split("\\s,\\s");
				logger.info("收件人使用测试配置:" + Arrays.toString(recipients));
			}
			List<InternetAddress> recipientsAddress = new ArrayList<InternetAddress>();
			for (String recipient : recipients) {
				CollectionHelper.add(recipientsAddress, InternetAddress.parse(recipient));
			}
			int count = 0; // 收件人人数
			for (InternetAddress addr : recipientsAddress) {
				count++;
				try {
					addr.setPersonal(addr.getPersonal(), encoding);
					logger.debug("收件人:" + addr);
				} catch (UnsupportedEncodingException e) {
					// TODO
					logger.error("收件人名称编码转换失败" + addr.getPersonal(), e);
				}
			}
			message.setRecipients(Message.RecipientType.TO, recipientsAddress.toArray(new InternetAddress[count])); // 接收地址
			message.setSentDate(new Date()); // 发送时间
			message.setSubject(subject, encoding); // 主题
			// message.setText(messageText, MESSAGE_ENCODING); // 内容
			message.setContent(StringHelper.trim(content), "text/html; charset=" + encoding);
			message.saveChanges();

			// 发送邮件
			Transport.send(message);

			logger.info("邮件发送成功");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("邮件发送失败", e);
			return false;
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @param recipients
	 * @param subject
	 * @param content
	 * @return
	 */
	public static boolean send(String recipients, String subject, String content) {
		return MailHelper.send(recipients, subject, content, null);
	}

	/**
	 * 发送邮件
	 * 
	 * @param recipients
	 *            收件人,可以为多个
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param from
	 *            发件人
	 * @see #send(String[], String, String)
	 * @see InternetAddress#parse(String)
	 * @return
	 */
	public static boolean send(String recipients, String subject, String content, String from) {
		return send(new String[] { recipients }, subject, content, from);
	}

	public static boolean isDisabled() {
		return disabled;
	}

	public static String getEncoding() {
		return encoding;
	}

	public static String getProtocol() {
		return protocol;
	}

	public static String getHost() {
		return host;
	}

	public static String getUser() {
		return user;
	}

	public static String getPassword() {
		return password;
	}

	public static InternetAddress getFrom() {
		return from;
	}

	public static String getTO() {
		return TO;
	}

	public static Session getSession() {
		return session;
	}

	public static void main(String[] args) {
		send("15922901803@163.com", "eFiling测试邮件" + DateHelper.now(), "<h3>eFiling测试邮件发送</h3><p>测试邮件发送功能</p>");
	}
}
