package com.sinosoft.efiling.mail;

import java.util.HashMap;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.sinosoft.util.FreeMarkerHelper;
import com.sinosoft.util.StringHelper;

public class Mail extends HashMap<Object, Object> {// implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 559280722438538337L;

	private static Logger logger = LoggerFactory.getLogger("mail");

	/** 邮件标题 */
	protected String title;
	/** 邮件正文 */
	protected String content;
	/**
	 * 使用的邮件模板,如果指定了template参数，且有相关的文件，则会使用template模板内容替换title或content.
	 * title为/mail/template-title.ftl文件
	 * content为/mail/template.ftl文件
	 */
	protected String template;

	/** 发件人名称 */
	protected String sender;
	/** 发件人地址 */
	protected String from;
	/** 收件人名称 */
	protected String recipient;
	/** 收件人地址,必须 */
	protected String to;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.put("title", title);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		this.put("content", content);
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
		this.put("recipient", recipient);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
		this.put("from", from);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
		this.put("sender", sender);
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
		this.put("to", to);
	}

	/**
	 * 得到邮件标题
	 * 
	 * @param template
	 * @return
	 */
	public String parseTitle(String template) {
		template = "/mail/" + template + "-title.ftl";
		String title = FreeMarkerHelper.get(template, this);
		return title;
	}

	/**
	 * 得到邮件正文
	 * 
	 * @param template
	 * @return
	 */
	public String parseContent(String template) {
		template = "/mail/" + template + ".ftl";
		String content = FreeMarkerHelper.get(template, this);
		return content;
	}

	/**
	 * 
	 * 发送邮件
	 * 
	 * @param module 邮件正文模板
	 * @see #parseTitle(String)
	 * @see #parseContent(String)
	 */
	public void send(String template) {
		if (StringHelper.isEmpty(to)) {
			// 收件地址为空
			logger.info("收件地址不能为空");
			return;
		}
		String title = StringHelper.trim(parseTitle(template), this.title);
		String content = StringHelper.trim(parseContent(template), this.content);
		// SendMail.sendMail(recipient.getUserEmail(), title, content);
		MailHelper.send(to, title, content, from);
	}

	/**
	 * 发送邮件
	 */
	public void send() {
		send(this.template);
	}

}
