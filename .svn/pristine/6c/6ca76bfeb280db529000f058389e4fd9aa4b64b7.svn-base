package com.sinosoft.efiling.timer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sinosoft.efiling.hibernate.dao.DocumentDao;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.mail.Mail;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.Helper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;
import com.sinosoft.util.service.ServiceSupport;

/**
 * 单证资料即将到期时发送邮件进行提醒
 * 
 * @author LuoGang
 * 
 */
public class ReminderService extends ServiceSupport<Document, DocumentDao> implements TimerService {
	/** 默认超期要超期就提醒的天数,-10表示还有10天超期就提醒 */
	public static final int REMINDER_DAYS = NumberHelper.intValue(SystemHelper.getProperty("timer.reminder.days"), -10);
	/** 资料到期提醒 */
	public static final String REMINDER_MAIL_TITLE = SystemHelper.getProperty("timer.reminder.title", "资料到期提醒");
	/** 邮件正文模板 */
	public static final String REMINDER_MAIL_TEMPLATE = SystemHelper.getProperty("timer.reminder.template",
			"timer.reminder");

	/** 查询SQL */
	public static final String SQL = SystemHelper.getProperty("timer.reminder.sql");

	/** 邮件提醒的超期天数，负数表示在days天后即将到期 */
	private int days = REMINDER_DAYS;
	/** 邮件标题模板 */
	private String title = REMINDER_MAIL_TITLE;
	/** 邮件正文模板 */
	private String template = REMINDER_MAIL_TEMPLATE;

	@SuppressWarnings("unchecked")
	public void run() {
		// SQL中的查询结果依次为:userId,userName,userMail
		// <th>保/批单号</th>
		// <th>业务关系代码</th>
		// <th>险种（产品线）</th>
		// <th>投保人</th>
		// <th>签单日期</th>
		// <th>生效日期</th>
		// <th>差缺文件清单</th>
		// <th>超期天数(负数表示即将到期天数)</th>
		// String sql = SystemHelper.getProperty("timer.reminder.sql");
		// int days = NumberHelper.intValue(SystemHelper.getProperty("timer.reminder.days"), DEFAULT_REMINDER_DAYS);
		List<Object[]> list = (List<Object[]>) querySQL(SQL, new Object[] { SystemUtils.FILE_DEADLINE_DEFAULT, days });

		logger.info("需要发送到期邮件通知,单证数量:" + list.size() + ",详细信息:\n" + StringHelper.join(list));
		if (Helper.isEmpty(list)) return;
		Map<String, List<Object[]>> map = new LinkedHashMap<String, List<Object[]>>(); // 即将到期的数据
		Map<String, List<Object[]>> expired = new LinkedHashMap<String, List<Object[]>>();// 已超期的数据
		Set<String> ids = new HashSet<String>();
		String userId;
		List<Object[]> data;
		int days;
		Map<String, List<Object[]>> m;
		for (Object[] row : list) {
			userId = (String) row[0];
			ids.add(userId);
			days = NumberHelper.intValue(row[row.length - 1]); // 最后一个数组为超期天数
			if (days > 0) m = expired; // 已超期
			else m = map;
			data = m.get(userId); // 即将到期
			if (data == null) {
				data = new ArrayList<Object[]>();
				m.put(userId, data);
			}
			data.add(row);
		}
		String userName = null;
		String userMail = null;
		Mail mail = new Mail();
		mail.setTitle(title);
		mail.setTemplate(template);
		for (String key : ids) {
			userId = key;
			list = map.get(key);
			mail.clear(); // 每次发送先清空数据
			if (list != null) {
				data = new ArrayList<Object[]>();
				for (Object[] row : list) {
					userName = (String) row[1];
					userMail = (String) row[2];

					Object[] dest = new Object[row.length - 3];
					System.arraycopy(row, 3, dest, 0, dest.length);
					data.add(dest);
				}

				if (!data.isEmpty()) mail.put("data", data);
			}
			list = expired.get(key);
			if (list != null) {
				data = new ArrayList<Object[]>();
				for (Object[] row : list) {
					userName = (String) row[1];
					userMail = (String) row[2];

					Object[] dest = new Object[row.length - 3];
					System.arraycopy(row, 3, dest, 0, dest.length);
					data.add(dest);
				}
				if (!data.isEmpty()) mail.put("expired", data);
			}
			mail.setRecipient(userName);
			mail.setTo(userMail);
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
