package com.sinosoft.efiling.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sinosoft.efiling.service.DocumentAuditService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.CollectionHelper;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.Helper;
import com.sinosoft.util.LoggableImpl;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;

/**
 * 检查在eFiling系统中是否有遗漏的保单数据,如果有遗漏的数据则进行补充
 * 
 * @author LuoGang
 * 
 */
public class DocumentComplementTimerTask extends LoggableImpl implements TimerService {

	/** 查询出有资料可共享的单证数据 */
	public static final String SQL_SHARE_QUERY = SystemHelper.getProperty("timer.document.share.sql");

	/** 开始补数日期，应该配置为系统上线日期 */
	public static final Date MIN_DATE = SystemUtils.SYSTEM_GO_LIVE_TIME;
	// DateHelper.parse(SystemHelper.getProperty("timer.document.complement.date"),
	// DateHelper.clear(new Date()));
	/** 删除在数据库中半年以前的无效数据 */
	public static final int DAYS = NumberHelper.intValue(SystemHelper.getProperty("timer.document.complement.days"),
			180);
	// /** 删除无效数据的HQL */
	// public static final String HQL_DELETE = "DELETE FROM " + Document.class.getName()
	// + " d WHERE d.status = ? AND d.fileStatus = ? AND d.createTime < ?";

	/** 删除数据库中的无效且没有上传任何归档资料的单证数据 */
	public static final String SQL_DELETE = "DELETE FROM T_F_DOCUMENT D WHERE D.STATUS = ? AND NOT EXISTS ("
			+ "		SELECT * FROM T_F_DOCUMENT_FILE A WHERE A.DOCUMENT_ID = D.ID AND A.STATUS = ? AND A.SHARED_FROM_ID IS NULL"
			+ "	) AND D.TYPE IN (?, ?) AND D.CREATE_TIME < ? ";
	/**
	 * 查询SQL
	 * 
	 * <pre>
	 * SELECT POLICYNO, ? AS TYPE FROM PRPCMAIN C \
	 * 	WHERE EXISTS (SELECT POLICY_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.PROPOSAL_NO \= C.PROPOSALNO) \
	 * UNION ALL \
	 * SELECT POLICYNO, ? AS TYPE FROM PRPCMAIN C \
	 * WHERE C.OPERATEDATE >= ? \
	 * 	AND NOT EXISTS (SELECT PROPOSAL_NO FROM T_F_DOCUMENT D WHERE D.PROPOSAL_NO = C.PROPOSALNO) \
	 * UNION ALL \
	 * SELECT POLICYNO, ? FROM PRPCMAINCOVERNOTE C \
	 * 	WHERE OPERATEDATE >\= ? AND ( \
	 * 		(UNDERWRITEFLAG IN ('1', '3') \
	 * 			AND NOT EXISTS (SELECT POLICY_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.STATUS = ? AND D.POLICY_NO \= C.POLICYNO) ) \
	 * 		OR (UNDERWRITEFLAG NOT IN ('1', '3') \
	 * 			AND NOT EXISTS (SELECT POLICY_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.POLICY_NO \= C.POLICYNO) ) ) \
	 * UNION ALL \
	 * SELECT ENDORSENO, ? FROM PRPPHEAD C \
	 * 	WHERE INPUTDATE >\= ? AND ((UNDERWRITEFLAG IN ('1', '3') \
	 * 		AND NOT EXISTS (SELECT NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.STATUS = ? AND D.NO \= C.ENDORSENO)) \
	 * 		OR (UNDERWRITEFLAG NOT IN ('1', '3') \
	 * 			AND NOT EXISTS (SELECT NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.NO \= C.ENDORSENO) ) ) \
	 * UNION ALL \
	 * SELECT ENDORSENO, ? FROM PRPPHEADCOVERNOTE C \
	 * 	WHERE INPUTDATE >\= ? AND ((UNDERWRITEFLAG IN ('1', '3') \
	 * 		AND NOT EXISTS (SELECT ENDOR_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.STATUS = ? AND D.NO \= C.ENDORSENO)) \
	 * 		OR (UNDERWRITEFLAG NOT IN ('1', '3') \
	 * 			AND NOT EXISTS (SELECT ENDOR_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.NO \= C.ENDORSENO) ) ) \
	 * UNION ALL \
	 * SELECT PROPOSALNO, ? FROM PRPTMAIN C \
	 * 	WHERE OPERATEDATE >= ? AND UNDERWRITEFLAG NOT IN ('1', '3') \
	 * 		AND NOT EXISTS (SELECT PROPOSAL_NO FROM T_F_DOCUMENT D WHERE D.TYPE IN (?, ?) AND D.PROPOSAL_NO \= C.PROPOSALNO)
	 * </pre>
	 */
	public static final String SQL = SystemHelper.getProperty("timer.document.complement.sql");
	/**
	 * 将SQL按照UNION ALL分段进行执行
	 */
	public static final String[] SQLS = SQL.split("UNION ALL");
	private DocumentAuditService service;
	/** 补数的最早日期 */
	private Date date;
	/** 删除无效数据的天数 */
	private int days = DAYS;

	/** 当前是否正在执行run方法 */
	protected int running = 0;
	/** 用于同步 */
	private Object lock = new Object();

	public void run() {
		synchronized (lock) {
			if (this.running > 0) {
				logger.warn("任务正在运行，稍候重试!");
				this.running++;
				return;
			}

			this.running = 1; // 每次运行都视为最后一次运行,除非外部再次调用run方法增加running
		}

		Exception ex = null;
		while (this.running > 0) {
			try {
				this.doRun();
			} catch (Exception e) {
				// 一般情况下，只可能捕获到RuntimeException
				logger.error("任务运行出现异常:" + e);
				ex = e;
			} finally {
				this.running = Math.max(this.running - 1, 0);
			}
		}

		if (ex != null) {
			if (ex instanceof RuntimeException) throw (RuntimeException) ex;
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	private void doRun() {
		days = Math.abs(days);
		date = DateHelper.addDays(new Date(), -days); // 对于未生效数据的补录时间，默认为当前days天
		if (date.before(MIN_DATE)) date = MIN_DATE;

		List<Object[]> list = new ArrayList<Object[]>();
		int index = 0;
		String sql = SQLS[index++];
		Object[] args = new Object[] { SystemUtils.DOCUMENT_TYPE_POLICY, SystemUtils.DOCUMENT_TYPE_PROPOSAL };
		// 需要补录的保单
		// SELECT POLICYNO, ? AS TYPE FROM PRPCMAIN C \
		// WHERE EXISTS (SELECT POLICY_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.PROPOSAL_NO \= C.PROPOSALNO) \
		logger.debug("补录保单SQL(已生成保单T_F_DOCUMENT表未同步的数据):" + sql + ", args=" + StringHelper.join(args));
		List<Object[]> c = (List<Object[]>) service.querySQL(sql, args);
		logger.debug("补录保单数据(已生成保单T_F_DOCUMENT表未同步的数据):" + StringHelper.join(c));
		CollectionHelper.add(list, c);

		// SELECT POLICYNO, ? AS TYPE FROM PRPCMAIN C \
		// WHERE C.OPERATEDATE >= ? \
		// AND NOT EXISTS (SELECT PROPOSAL_NO FROM T_F_DOCUMENT D WHERE D.PROPOSAL_NO = C.PROPOSALNO AND D.TYPE IN (?,
		// ?)) \
		sql = SQLS[index++];
		args = new Object[] { SystemUtils.DOCUMENT_TYPE_POLICY, MIN_DATE, SystemUtils.DOCUMENT_TYPE_POLICY,
				SystemUtils.DOCUMENT_TYPE_PROPOSAL };
		logger.debug("补录保单SQL(已生成保单未生成T_F_DOCUMENT表的数据):" + sql + ", args=" + StringHelper.join(args));
		c = (List<Object[]>) service.querySQL(sql, args);
		logger.debug("补录保单数据(已生成保单未生成T_F_DOCUMENT表的数据):" + StringHelper.join(c));
		CollectionHelper.add(list, c);

		// 大保单
		// SELECT POLICYNO, ? FROM PRPCMAINCOVERNOTE C \
		// WHERE ( \
		// (OPERATEDATE >\= ? AND UNDERWRITEFLAG IN ('1', '3') \
		// AND NOT EXISTS (SELECT POLICY_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.STATUS = ? AND D.POLICY_NO \=
		// C.POLICYNO) ) \
		// OR (OPERATEDATE >\= ? AND UNDERWRITEFLAG NOT IN ('1', '3') \
		// AND NOT EXISTS (SELECT POLICY_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.POLICY_NO \= C.POLICYNO) ) ) \
		sql = SQLS[index++];
		args = new Object[] { SystemUtils.DOCUMENT_TYPE_POLICY, MIN_DATE, SystemUtils.DOCUMENT_TYPE_POLICY,
				SystemUtils.STATUS_VALID, date, SystemUtils.DOCUMENT_TYPE_POLICY };
		logger.debug("补录大保单SQL:" + sql + ", args=" + StringHelper.join(args));
		c = (List<Object[]>) service.querySQL(sql, args);
		logger.debug("补录大保单数据:" + StringHelper.join(c));
		CollectionHelper.add(list, c);

		// 批单
		// SELECT ENDORSENO, ? FROM PRPPHEAD C \
		// WHERE ( \
		// (INPUTDATE >\= ? AND UNDERWRITEFLAG IN ('1', '3') \
		// AND NOT EXISTS (SELECT NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.STATUS = ? AND D.NO \= C.ENDORSENO)) \
		// OR (INPUTDATE >\= ? AND UNDERWRITEFLAG NOT IN ('1', '3') \
		// AND NOT EXISTS (SELECT NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.NO \= C.ENDORSENO) ) ) \
		sql = SQLS[index++];
		args = new Object[] { SystemUtils.DOCUMENT_TYPE_ENDOR, MIN_DATE, SystemUtils.DOCUMENT_TYPE_ENDOR,
				SystemUtils.STATUS_VALID, date, SystemUtils.DOCUMENT_TYPE_ENDOR };
		logger.debug("补录批单SQL:" + sql + ", args=" + StringHelper.join(args));
		c = (List<Object[]>) service.querySQL(sql, args);
		logger.debug("补录批单数据:" + StringHelper.join(c));
		CollectionHelper.add(list, c);

		// 大保单批单
		// SELECT ENDORSENO, ? FROM PRPPHEADCOVERNOTE C \
		// WHERE ( \
		// (INPUTDATE >\= ? AND UNDERWRITEFLAG IN ('1', '3') \
		// AND NOT EXISTS (SELECT ENDOR_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.STATUS = ? AND D.NO \=
		// C.ENDORSENO)) \
		// OR (INPUTDATE >\= ? AND UNDERWRITEFLAG NOT IN ('1', '3') \
		// AND NOT EXISTS (SELECT ENDOR_NO FROM T_F_DOCUMENT D WHERE D.TYPE \= ? AND D.NO \= C.ENDORSENO) ) ) \
		sql = SQLS[index++];
		args = new Object[] { SystemUtils.DOCUMENT_TYPE_ENDOR, MIN_DATE, SystemUtils.DOCUMENT_TYPE_ENDOR,
				SystemUtils.STATUS_VALID, date, SystemUtils.DOCUMENT_TYPE_ENDOR };
		logger.debug("补录大保单批单SQL:" + sql + ", args=" + StringHelper.join(args));
		c = (List<Object[]>) service.querySQL(sql, args);
		logger.debug("补录大保单批单数据:" + StringHelper.join(c));
		CollectionHelper.add(list, c);

		// 投保单
		// SELECT PROPOSALNO, ? FROM PRPTMAIN C \
		// WHERE OPERATEDATE >= ? AND UNDERWRITEFLAG NOT IN ('1', '3') \
		// AND NOT EXISTS (SELECT PROPOSAL_NO FROM T_F_DOCUMENT D WHERE D.TYPE IN (?, ?) AND D.PROPOSAL_NO \=
		// C.PROPOSALNO)
		sql = SQLS[index++];
		args = new Object[] { SystemUtils.DOCUMENT_TYPE_PROPOSAL, date, SystemUtils.DOCUMENT_TYPE_PROPOSAL,
				SystemUtils.DOCUMENT_TYPE_POLICY };
		logger.debug("补录投保单SQL:" + sql + ", args=" + StringHelper.join(args));
		c = (List<Object[]>) service.querySQL(sql, args);
		logger.debug("补录投保单数据:" + StringHelper.join(c));
		CollectionHelper.add(list, c);

		// List<Object[]> list = (List<Object[]>) service.querySQL(SQL, new Object[] { policy, proposal, // 遗漏的保单
		// policy, proposal, // 遗漏的大保单
		// endor, date, endor, SystemUtils.STATUS_VALID, endor, // 遗漏的批单
		// endor, date, endor, SystemUtils.STATUS_VALID, endor, // 遗漏的大保单的批单
		// proposal, date, proposal, policy });

		if (Helper.isEmpty(list)) {
			logger.debug("没有需要补数的数据!");
			// return;
		} else {
			int count = list.size();
			logger.debug("有" + count + "条需要补数的数据!");
			index = 0;
			for (Object[] data : list) {
				logger.debug("第" + (++index) + "条数据,no:" + data[0] + ",type=" + data[1]);
				try {
					service.save((String) data[0], (String) data[1]);
					// service.commit(); // 提交事务
					logger.debug("第" + index + "条数据成功,no:" + data[0] + ",type=" + data[1]);
					// service.beginTransaction(); // 重开事务
				} catch (Exception e) {
					// 内部嵌套事务失败,可以重新进行另外一个保存
					e.printStackTrace();
					logger.error("第" + index + "条数据失败,no:" + data[0] + ",type=" + data[1], e);
				} finally {
					// TODO
				}
			}
		}

		// 删除无效数据
		Date date = DateHelper.clear(DateHelper.add(Calendar.DATE, 0 - days));
		args = new Object[] { SystemUtils.STATUS_INVALID, SystemUtils.DOCUMENT_FILE_STATUS_FILE,
				SystemUtils.DOCUMENT_TYPE_PROPOSAL, SystemUtils.DOCUMENT_TYPE_ENDOR, date };
		logger.debug("删除无效数据:sql=" + SQL_DELETE + ",args=" + StringHelper.join(args));
		try {
			int count = service.executeSQL(SQL_DELETE, args);
			logger.debug("成功删除" + count + "条无效数据!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除无效数据失败", e);
		}

		// 自动共享资料
		args = new Object[] { SystemUtils.DOCUMENT_FILE_STATUS_LACK, SystemUtils.DOCUMENT_FILE_STATUS_FILE,
				SystemUtils.STATUS_VALID };
		logger.debug("自动共享差缺的资料：sql=" + SQL_SHARE_QUERY);
		List<String> nos = (List<String>) service.querySQL(SQL_SHARE_QUERY, args);
		int size = nos == null ? 0 : nos.size();
		try {
			logger.debug("可共享差缺的资料数量：" + nos.size());
			if (size > 0) {
				for (String businessNo : nos) {
					service.share(businessNo);
					logger.debug("共享" + businessNo + "的资料成功!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("共享差缺资料失败", e);
		}
	}

	// /**
	// * 会被Spring内部事务配置拦截的方法,这样方便在其他方法中可以单独处理每一次的保存事务
	// *
	// * @param no
	// * @param type
	// * @return
	// */
	// private Document saveNested(String no, String type) {
	// return service.save(no, type);
	// }

	public DocumentAuditService getService() {
		return service;
	}

	public void setService(DocumentAuditService service) {
		this.service = service;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

}
