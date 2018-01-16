package com.sinosoft.efiling.timer;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.User;
import com.sinosoft.efiling.service.DocumentImportService;
import com.sinosoft.efiling.util.SpringUtils;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.FileHelper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;
import com.sinosoft.util.timer.TimerScheduler;
import com.sinosoft.util.timer.TimerSchedulerTask;

/**
 * 重庆影像文件迁移
 * 
 * @author LuoGang
 * 
 */
public class CQDocumentImportTimerTask extends TimerSchedulerTask {
	/** 日志记录对象 */
	public static final Logger logger = LoggerFactory.getLogger(CQDocumentImportTimerTask.class);

	class CQDocumentImportThread extends Thread {
		File[] files;
		File importedDir;
		int fromIndex;
		int count;
		DocumentImportService documentImportService;
		User user;
		CQDocumentImportTimerTask task;

		public CQDocumentImportThread(File[] files, File importedDir, int fromIndex, int count,
				DocumentImportService documentImportService, User user, CQDocumentImportTimerTask task) {
			super();
			this.files = files;
			this.importedDir = importedDir;
			this.fromIndex = fromIndex;
			this.count = count;
			this.documentImportService = documentImportService;
			this.user = user;
			this.task = task;
		}

		@Override
		public void run() {
			int index = fromIndex;
			int endIndex = Math.min(files.length, fromIndex + count);
			File file;
			StringBuilder txt = new StringBuilder();
			int count = 0;
			logger.debug(index + "," + endIndex + ",length=" + files.length);
			for (; index < endIndex; index++) {
				file = files[index];
				logger.debug("file=" + file.getAbsolutePath());
				if (!file.exists() || !file.isFile()) {
					logger.warn(file.getAbsolutePath() + "不存在或已被删除!");
					continue; // 文件不存在或已删除
				}

				Document doc = null;
				try {
					doc = documentImportService.save(file, user);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(file.getAbsolutePath() + "数据保存失败!", e);
					continue;
				}
				try {
					// 将文件移动至已导入的目录
					FileHelper.moveTo(file, importedDir);
				} catch (Exception e) {
					// TODO
					logger.warn(file.getAbsolutePath() + "文件移动失败!", e);
					// continue;
				}
				count++;
				txt.setLength(0);
				txt.append(count).append("\t");
				txt.append(doc.getVisaNo()).append(",");
				txt.append(doc.getId()).append(",");
				txt.append(doc.getPolicyNo()).append(",");
				txt.append(doc.getProposalNo());
				logger.debug(txt.toString());
			}
			logger.info("已成功导入" + count + "个保单文件!");

			task.threadCount--;

			if (task.threadCount < 0) {
				TimerScheduler.getInstance().cancel(task);
			}
		}
	}

	private int threadCount;

	@SuppressWarnings("unchecked")
	@Override
	protected void execute() {
		if (threadCount > 0) return; // 此任务正在执行

		DocumentImportService documentImportService = SpringUtils.getBean(DocumentImportService.class);
		String url = SystemHelper.getProperty("cq.import.dir"); // 导入文件目录
		if (StringHelper.isEmpty(url)) {
			url = SystemUtils.getServerPath("WEB-INF/classes/pdf");
		}
		logger.info("要导入的目录：" + url);
		File dir = new File(url);
		logger.debug("dir=" + dir.getAbsolutePath());
		if (!dir.isDirectory()) {
			logger.warn("\"" + dir.getAbsolutePath() + "\"不是目录，无法导入!");
			// TimerScheduler.getInstance().cancel(this);
			return;
		}
		User user = documentImportService.getUserDao().get("0000000000");
		java.io.File importedDir = new java.io.File(dir.getParentFile(), dir.getName() + "-imported");
		if (!importedDir.exists()) importedDir.mkdirs();
		logger.info("备份导入文件的目录：" + importedDir.getAbsolutePath());

		File[] files = dir.listFiles();
		if (files == null || files.length <= 0) {
			// 没有要导入的文件共享资料
			List<String> nos = (List<String>) documentImportService.querySQL(
					DocumentComplementTimerTask.SQL_SHARE_QUERY, new Object[] { SystemUtils.DOCUMENT_FILE_STATUS_LACK,
							SystemUtils.DOCUMENT_FILE_STATUS_FILE, SystemUtils.STATUS_VALID });
			int size = nos == null ? 0 : nos.size();
			try {
				logger.info("可共享差缺的资料数量：" + nos.size());
				if (size > 0) {
					for (String businessNo : nos) {
						documentImportService.share(businessNo);
						logger.info("共享" + businessNo + "的资料成功!");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("共享差缺资料失败", e);
			}

			// 删除无效数据
			Date date = DateHelper.clear(DateHelper.add(Calendar.DATE, 0 - 180));
			Object[] args = new Object[] { SystemUtils.STATUS_INVALID, SystemUtils.DOCUMENT_FILE_STATUS_FILE,
					SystemUtils.DOCUMENT_TYPE_PROPOSAL, SystemUtils.DOCUMENT_TYPE_ENDOR, date };
			logger.debug("删除无效数据:sql=" + DocumentComplementTimerTask.SQL_DELETE + ",args=" + StringHelper.join(args));
			try {
				if ((new File("d:\\deleteInvliad.txt")).isFile()) {
					int count = documentImportService.executeSQL(DocumentComplementTimerTask.SQL_DELETE, args);
					logger.debug("成功删除" + count + "条无效数据!");
				} else {
					int count = NumberHelper.intValue(documentImportService.getDao().uniqueResultSQL(
							"SELECT COUNT(*) " + DocumentComplementTimerTask.SQL_DELETE.substring(6), args));
					logger.debug("可删除" + count + "条无效数据!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("删除无效数据失败", e);
			}
		} else {
			int count = 500;
			threadCount = (int) Math.ceil(((double) files.length) / count);
			logger.info(files.length + "个文件,共" + threadCount + "个线程");
			for (int i = 0; i < threadCount; i++) {
				new CQDocumentImportThread(files, importedDir, i * count, count, documentImportService, user, this)
						.start();
			}
		}
		// TimerScheduler.getInstance().cancel(this);
	}
}
