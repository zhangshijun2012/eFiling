package com.sinosoft.mail;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

/**
 * 邮件任务
 * 
 * @author LuoGang
 * 
 */
public class MailTask implements Runnable {
	/** 日期时间格式 */
	static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式 */
	static final String DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * 格式化date为pattern格式的字符串
	 * 
	 * @param date Date 日期
	 * @return 转换后的日期时间字符串
	 * @see SimpleDateFormat#SimpleDateFormat(String)
	 * @see SimpleDateFormat#format(Date)
	 */
	public static String format(Date date) {
		return new SimpleDateFormat(DATETIME_PATTERN).format(date);
	}

	/**
	 * 日期格式化
	 * 
	 * @param date
	 * @param patter
	 * @return
	 */
	public static String format(Date date, String patter) {
		return new SimpleDateFormat(patter).format(date);
	}

	/** 配置属性 */
	private Properties props;
	/** 接收后是否删除邮件 */
	private boolean delete = true;
	/** 读取邮件的文件夹 */
	private String[] folders = { "INBOX" };

	public void run() {
		synchronized (this) {
			if (props == null || props.isEmpty()) {
				InputStream in = null;
				in = MailService.class.getResourceAsStream("/mail.task.properties");
				if (in == null) in = MailService.class.getResourceAsStream("mail.properties");
				props = new Properties();
				try {
					props.load(in);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		MailSettings set = new MailSettings(props);
		MailService service = new MailService(set);
		List<Message> msgs = service.receive(folders, delete);
		String today = format(new Date(), DATE_PATTERN);
		String subject = today;
		StringBuilder content = new StringBuilder(100);

		if (msgs == null || msgs.isEmpty()) {
			subject += " 没有收到任何邮件";
			content.append("<h2>").append(subject).append("</h2>");

			subject = today + props.getProperty("mail.to.failure");
		} else {
			subject = " 收到" + msgs.size() + "个邮件";
			content.append("<h2>").append(subject).append("</h2>");

			subject = today + props.getProperty("mail.to.success");

			Set<Folder> folders = new HashSet<Folder>();
			MimeMessage msg = null;
			// 解析所有邮件
			for (int count = msgs.size(), i = 0; i < count; i++) {
				content.append("<div style=\"margin-top: 10px;\">");
				msg = (MimeMessage) msgs.get(i);
				folders.add(msg.getFolder());
				try {
					content.append("<h3><strong>").append(i + 1).append(".");
					content.append(service.decodeText(msg.getHeader("Subject", null)));
					content.append("</strong><small style=\"color: gray; font-size: 12px;\">");
					content.append(format(msg.getSentDate())).append("</small></h3>");
					content.append("<p style=\"color: gray;\">发件人: ");
					content.append(service.decodeText(msg.getFrom()));
					// content.append("\t收件人：" + InternetAddress.toString(msg.getAllRecipients()));
					content.append("&nbsp; 邮件大小：" + (msg.getSize() / 1024) + "KB");
					content.append("</p>");
					content.append("<p>");
					content.append(service.decodeContent(msg));
					content.append("</p>");
				} catch (Exception e) {
					// TODO
					e.printStackTrace();
				}
				content.append("</div>");
			}

			if (delete) {
				// 删除邮件
				for (int count = msgs.size(), i = 0; i < count; i++) {
					msg = (MimeMessage) msgs.get(i);
					try {
						msg.setFlag(Flags.Flag.DELETED, delete);
					} catch (Exception e) {
						// TODO 删除邮件时可能报错，但是服务端已经成功
						e.printStackTrace();
					}
				}
			}

			Store store = null;
			for (Folder folder : folders) {
				store = folder.getStore();
				try {
					folder.close(delete);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				store.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println(content);

		// 发送邮件
		service.send(subject, content.toString(), props.getProperty("mail.to"));
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public String[] getFolders() {
		return folders;
	}

	public void setFolders(String[] folders) {
		this.folders = folders;
	}

	public static void main(String[] args) {
		MailTask task = new MailTask();
		task.run();
	}
}
