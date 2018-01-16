package com.sinosoft.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/**
 * 邮件服务
 * 
 * @author LuoGang
 *
 */
public class MailService {
	/** INBOX收件箱 */
	public static final String INBOX = "INBOX";

	public MailService() {
		super();
	}

	/**
	 * 使用设置初始
	 * 
	 * @param settings
	 */
	public MailService(MailSettings settings) {
		super();
		this.settings = settings;
	}

	/** 环境变量 */
	private MailSettings settings;

	/** 邮件连接对象 */
	private Session session;

	/**
	 * 使用当前配置连接服务器,得到javax.mail.Session对象，
	 * 注意，如果此方法调用过后再对settings对象的host等属性的修改将不会生效
	 * 
	 * @return
	 */
	public Session connect() {
		if (session == null) {
			session = Session.getInstance(settings.props(), settings.getAuthenticator());
		}
		return session;
	}

	/**
	 * 使用新的配置连接服务器
	 * 
	 * @param settings
	 * @return
	 */
	public Session connect(MailSettings settings) {
		if (settings == null) {
			throw new RuntimeException("settings must not be null");
		}
		this.settings = settings;
		this.session = null;
		return this.connect();
	}

	/**
	 * 得到Transport连接对象
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public Transport getTransport() throws MessagingException {
		connect();
		Transport transport = session.getTransport(settings.getTransportProtocol());
		if (settings.isAuth()) {
			transport.connect(settings.getTransportHost(), settings.getTransportPort(), settings.getUser(),
					settings.getPassword());
		} else {
			transport.connect(settings.getTransportHost(), settings.getTransportPort(), null, null);
		}
		return transport;
	}

	/**
	 * 发送邮件.此方法会调用msg.saveChanges()
	 * 
	 * @param msg 邮件Message对象，包括收件人等信息
	 */
	public void send(Message msg) {
		Transport transport = null;
		try {
			transport = getTransport();
			msg.saveChanges();
			transport.sendMessage(msg, msg.getAllRecipients());
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				transport.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送文本信息
	 * 
	 * @param subject 邮件标题
	 * @param content 邮件正文
	 * @param recipients 收件人
	 */
	public void send(String subject, String content, String recipients) {
		MimeMessage msg = new MimeMessage(session);
		try {
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
			msg.setSentDate(new Date()); // 发送时间
			msg.setSubject(subject); // 主题
			msg.setContent(content == null ? "" : content, "text/html;charset=" + settings.getEncoding());
			// msg.setText(content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.send(msg);
	}

	/**
	 * 得到Store连接对象,主要可用于收取邮件
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public Store getStore() throws MessagingException {
		connect();
		Store store = session.getStore(settings.getStoreProtocol());
		if (settings.isAuth()) {
			store.connect(settings.getStoreHost(), settings.getStorePort(), settings.getUser(), settings.getPassword());
		} else {
			store.connect(settings.getStoreHost(), settings.getStorePort(), null, null);
		}
		return store;
	}

	/**
	 * 接收邮件
	 * 
	 * @param list
	 * @param folder
	 * @param write 是否以读写模式打开邮件
	 * @throws MessagingException
	 */
	protected void receive(List<Message> list, Folder folder, boolean write) throws MessagingException {
		folder.open(write ? Folder.READ_WRITE : Folder.READ_ONLY);
		try {
			Message[] msgs = folder.getMessages();
			if (msgs != null) {
				for (Message msg : msgs) {
					list.add(msg);
				}
			}
			// if (delete) folder.expunge();
			// Folder[] children = null;
			// try {
			// children = folder.list();
			// } catch (MessagingException e) {
			// // TODO POP3Folder不能包含子目录
			// }
			// if (children == null || children.length == 0) return;
			// for (Folder child : children) {
			// if (child.equals(folder)) continue;
			// this.receive(list, child, delete);
			// }
		} finally {
			// folder.close(false);
		}
	}

	/**
	 * 收取所有邮件,不删除服务器上的邮件.POP3只能读取INBOX目录的邮件
	 * 
	 * @return
	 */
	public List<Message> receive() {
		return this.receive(false);
	}

	/**
	 * 收取所有邮件
	 * 
	 * @param write 是否以读写模式打开邮件
	 * @return 所有收取到的邮件
	 */
	public List<Message> receive(boolean write) {
		Store store = null;
		List<Message> list = new ArrayList<Message>();
		try {
			store = this.getStore();
			Folder[] folders = store.getDefaultFolder().list();
			for (Folder folder : folders) {
				try {
					this.receive(list, folder, write);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (MessagingException e) {
			list = null; // 需要抛出异常
			throw new RuntimeException(e);
		} finally {
			if (store != null && (list == null || list.isEmpty())) {
				// 没有返回数据则关闭store.否则应用在得到返回数据后应该关闭store
				try {
					store.close();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * 读取names文件夹下的邮件
	 * 
	 * @param names
	 * @param write 是否以读写模式打开邮件
	 * @return 所有收取到的邮件
	 */
	public List<Message> receive(String[] names, boolean write) {
		Store store = null;
		List<Message> list = new ArrayList<Message>();
		try {
			store = this.getStore();
			for (String name : names) {
				this.receive(list, store.getFolder(name), write);
			}
		} catch (MessagingException e) {
			list = null; // 需要抛出异常
			throw new RuntimeException(e);
		} finally {
			if (store != null && (list == null || list.isEmpty())) {
				// 没有返回数据则关闭store.否则应用在得到返回数据后应该关闭store
				try {
					store.close();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * 读取name文件夹下的邮件,不删除服务器上的邮件
	 * 
	 * @param name
	 * @return 所有收取到的邮件
	 */
	public List<Message> receive(String[] names) {
		return this.receive(names, false);
	}

	/**
	 * 防止中文乱码，对text进行解码
	 * 
	 * @param text
	 * @return
	 * @see MailSettings#getEncoding()
	 */
	public String decodeText(String text) {
		// if (!text.startsWith("=?")) {
		// try {
		// return new String(text.getBytes("iso8859-1"), settings.getEncoding());
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// // e.printStackTrace();
		// }
		// }
		try {
			return MimeUtility.decodeText(text);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return text;
	}

	/**
	 * 解码地址
	 * 
	 * @param addrs
	 * @return
	 */
	public String decodeText(Address... addrs) {
		StringBuilder text = new StringBuilder();
		for (Address addr : addrs) {
			if (addr instanceof InternetAddress) {
				InternetAddress addr2 = (InternetAddress) addr;
				text.append(decodeText(addr2.getPersonal()));
				text.append("(").append(decodeText(addr2.getAddress())).append(")");
			} else {
				text.append(decodeText(addr.toString()));
			}
		}
		return text.toString();
	}

	/**
	 * 解析邮件正文
	 * 
	 * @param msg
	 * @return
	 */
	public String decodeContent(Message msg) {
		Object content;
		try {
			content = msg.getContent();
		} catch (Exception e) {
			return null;
		}
		if (content instanceof Multipart) return this.decodeContent((Multipart) content);
		if (content instanceof Part) return this.decodeContent((Part) content);
		try {
			return MimeUtility.decodeText(String.valueOf(content));
		} catch (UnsupportedEncodingException e) {
			return String.valueOf(content);
		}
	}

	/**
	 * 解析邮件内容
	 * 
	 * @param part
	 * @return
	 */
	public String decodeContent(Multipart mp) {
		try {
			int count = mp.getCount();
			StringBuilder content = new StringBuilder();
			for (int i = 0; i < count; i++) {
				Part part = mp.getBodyPart(0);
				String result = null;
				if (part.getContent() instanceof Multipart) {
					// 递归迭代
					result = decodeContent(mp);
				} else {
					result = decodeContent(part);
				}
				if (result != null) content.append(result);
				content.append("\r\n");

			}
			return content.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析邮件
	 * 
	 * @param part
	 * @return
	 */
	public String decodeContent(Part part) {
		String content = null;
		try {
			if (part.getDisposition() != null) {
				// 附件
				return null;
			}
			content = String.valueOf(part.getContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (content != null) {
			try {
				return MimeUtility.decodeText(content);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return content;
	}
}
