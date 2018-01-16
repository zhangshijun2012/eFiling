package com.sinosoft.efiling.timer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sinosoft.efiling.hibernate.dao.FileLendingDao;
import com.sinosoft.efiling.hibernate.entity.FileLending;
import com.sinosoft.efiling.mail.Mail;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.Helper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;
import com.sinosoft.util.service.ServiceSupport;

/**
 * 文件借阅即将到期的邮件提醒
 * 
 * @author LuoGang
 * 
 */
public class FileLendingReminderService extends ServiceSupport<FileLending, FileLendingDao> implements TimerService {
	/** 默认要超期就提醒的天数 */
	public static final int REMINDER_DAYS = NumberHelper.intValue(SystemHelper.getProperty("timer.file.lending.days"));
	/** 邮件标题 */
	public static final String REMINDER_MAIL_TITLE = SystemHelper.getProperty("timer.file.lending.title", "借阅档案到期提醒");
	/** 邮件正文模板 */
	public static final String REMINDER_MAIL_TEMPLATE = SystemHelper.getProperty("timer.file.lending.template",
			"timer.fileLending");
	/** 查询SQL */
	public static final String SQL = SystemHelper.getProperty("timer.file.lending.sql");

	/** 邮件提醒的超期天数，负数表示在days天后即将到期 */
	private int days = REMINDER_DAYS;
	/** 邮件标题模板 */
	private String title = REMINDER_MAIL_TITLE;
	/** 邮件正文模板 */
	private String template = REMINDER_MAIL_TEMPLATE;

	@SuppressWarnings("unchecked")
	public void run() {
		// String sql = SystemHelper.getProperty("timer.file.lending.sql");
		List<Object[]> list = (List<Object[]>) querySQL(SQL, new Object[] { SystemUtils.FILE_LENT_YES, days });

		logger.info("需要发送档案借阅到期邮件通知,档案数量:" + list.size() + ",详细信息:\n" + StringHelper.join(list));
		if (Helper.isEmpty(list)) return;
		Map<String, List<Object[]>> map = new LinkedHashMap<String, List<Object[]>>();
		String userId;
		List<Object[]> data;
		for (Object[] row : list) {
			userId = ((String) row[0]).trim().toLowerCase();
			data = map.get(userId);
			if (data == null) {
				data = new ArrayList<Object[]>();
				map.put(userId, data);
			}
			data.add(row);
		}
		String userName = null;
		String userMail = null;
		Mail mail = new Mail();
		mail.setTitle(title);
		mail.setTemplate(template);
		for (String key : map.keySet()) {
			userId = key;
			data = new ArrayList<Object[]>();
			for (Object[] row : map.get(key)) {
				userName = (String) row[1];
				userMail = (String) row[2];

				Object[] dest = new Object[row.length - 3];
				System.arraycopy(row, 3, dest, 0, dest.length);
				data.add(dest);
			}
			mail.setRecipient(userName);
			mail.setTo(userMail);
			mail.put("data", data);
			mail.send();
		}
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
