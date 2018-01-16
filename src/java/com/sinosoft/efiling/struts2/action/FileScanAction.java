package com.sinosoft.efiling.struts2.action;

import java.io.File;
import java.util.ArrayList;

import com.ecm.webservice.ECMWebServiceHelper;
import com.sinosoft.efiling.hibernate.dao.DocumentDao;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.service.DocumentImportService;
import com.sinosoft.efiling.service.FileScanService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.FileHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.json.JSONObject;
import com.sinosoft.util.struts2.action.EntityActionSupport;

/**
 * 文件扫描处理类
 * 
 * @author LuoGang
 * 
 */
public class FileScanAction extends EntityActionSupport<Document, DocumentDao, FileScanService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3877331149815087419L;

	/**
	 * 校验ECM返回的xml文档,提示错误信息
	 * 
	 * @return
	 */
	public String read() {
		// xml = ECMWebServiceHelper.read(url, batchId);
		xml = FileHelper.getText(SystemUtils.getServerPath("ecm.xml"));
		// System.out.println(DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		// 返回的是有错误信息的单证的单证号和对应单证有错误的文件类型
		JSONObject json = service.read(xml);
		// json.put("xml", xml); // XML字符串
		// System.out.println(DateHelper.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		return dispatchSuccess(json);
	}

	/**
	 * 确定保存扫描结果,上传XML文档至WebService
	 */
	public String update() {
		// boolean success = ECMWebServiceHelper.update(url, batchId, xml);
		JSONObject json = service.save(xml, batchId, riskType, getCurrentUserSession());
		boolean success = json.optBoolean("success");
		if (success) {
			// json = service.save(xml, batchId, riskType, getCurrentUserSession());
			// success = ECMWebServiceHelper.update(url, batchId, xml);
			if (!success) {
				json.put("success", false);
				json.put("message", "扫描确认服务保存失败,请重试!");
			}
		}

		if (!success) {
			// 事务回滚
			service.rollback();
		}
		return dispatchSuccess(json);
	}

	/**
	 * 取消扫描结果
	 * 
	 * @return
	 */
	public String cancel() {
		boolean success = ECMWebServiceHelper.cancel(url, batchId);
		return dispatchSuccess("{\"success\": " + success + "}");
	}

	/** 用于重庆公共盘影像迁移 */
	private DocumentImportService documentImportService;

	/**
	 * 导入影像文件
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String imp() {
		if (StringHelper.isEmpty(url)) {
			url = SystemUtils.getServerPath("WEB-INF/classes/pdf");
		}
		logger.debug("要导入的目录：", url);
		File dir = new File(url);
		logger.debug("dir=" + dir.getAbsolutePath());
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files == null) {
				logger.debug("目录为空，没有需要导入的数据!");
				write("\"" + dir.getAbsolutePath() + "\"目录为空，没有需要导入的数据!");
			} else {
				java.io.File importedDir = new java.io.File(dir.getParentFile(), dir.getName() + "-imported");
				if (!importedDir.exists()) importedDir.mkdirs();
				logger.debug("已导入文件的备份目录：" + importedDir.getAbsolutePath());
				list = new ArrayList<Document>();
				int count = 0;
				StringBuilder txt = new StringBuilder();
				for (java.io.File file : dir.listFiles()) {
					logger.debug("file=" + file.getAbsolutePath());
					Document doc = documentImportService.save(file, getCurrentUserSession());
					if (doc == null) continue;
					list.add(doc);
					// 将文件移动至已导入的目录
					FileHelper.moveTo(file, importedDir);
					count++;
					txt.append(count).append("\t");
					txt.append(doc.getVisaNo()).append(",");
					txt.append(doc.getId()).append(",");
					txt.append(doc.getPolicyNo()).append(",");
					txt.append(doc.getProposalNo()).append("\n");
				}
				txt.append("成功导入" + count + "个保单文件:\n");
				logger.debug(txt.toString());
				write(txt.toString());
			}
		} else {
			logger.debug("\"" + dir.getAbsolutePath() + "\"不为目录，无法导入!");
			write("\"" + dir.getAbsolutePath() + "\"不为目录，无法导入!");
		}
		return null;
	}

	public DocumentImportService getDocumentImportService() {
		return documentImportService;
	}

	public void setDocumentImportService(DocumentImportService documentImportService) {
		this.documentImportService = documentImportService;
	}

	/** webservice的地址,因为各个分公司在不同的地址 */
	private String url;
	private String batchId;
	private String xml;
	private String riskType;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}
}
