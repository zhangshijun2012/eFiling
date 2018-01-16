package com.sinosoft.efiling.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sinosoft.efiling.hibernate.dao.ConfigureDao;
import com.sinosoft.efiling.hibernate.dao.DocumentDao;
import com.sinosoft.efiling.hibernate.dao.DocumentFileDao;
import com.sinosoft.efiling.hibernate.dao.FileDeadlineDao;
import com.sinosoft.efiling.hibernate.dao.FileTypeDao;
import com.sinosoft.efiling.hibernate.dao.ProductDao;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.DocumentFile;
import com.sinosoft.efiling.hibernate.entity.File;
import com.sinosoft.efiling.hibernate.entity.FileBox;
import com.sinosoft.efiling.hibernate.entity.FileType;
import com.sinosoft.efiling.hibernate.entity.PaperLogDocument;
import com.sinosoft.efiling.hibernate.entity.PrpDUwmtypes;
import com.sinosoft.efiling.hibernate.entity.PrpDUwmtypesId;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.efiling.util.UserSessionEntity;
import com.sinosoft.filenet.FileIndex;
import com.sinosoft.filenet.FileIndexService;
import com.sinosoft.util.CollectionHelper;
import com.sinosoft.util.CustomException;
import com.sinosoft.util.FileHelper;
import com.sinosoft.util.Helper;
import com.sinosoft.util.HttpClientHelper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.json.JSONArray;
import com.sinosoft.util.json.JSONObject;
import com.sinosoft.util.service.ServiceSupport;

/**
 * eFiling的文件保存到FileIndex表时：
 * property00:FileType.id
 * property01:FileType.fileModel
 * property02:DocumentFile.id
 * property03:'upload'表示是使用承保资料上传菜单上传的文件
 * 
 * @author LuoGang
 * 
 */
public class DocumentService extends ServiceSupport<Document, DocumentDao> {

	protected FileIndexService fileIndexService;
	protected ConfigureDao configureDao;
	protected DocumentAuditService documentAuditService;
	protected PaperLogDocumentService paperLogDocumentService;
	

	public PaperLogDocumentService getPaperLogDocumentService() {
		return paperLogDocumentService;
	}

	public void setPaperLogDocumentService(
			PaperLogDocumentService paperLogDocumentService) {
		this.paperLogDocumentService = paperLogDocumentService;
	}

	public FileIndexService getFileIndexService() {
		return fileIndexService;
	}

	public void setFileIndexService(FileIndexService fileIndexService) {
		this.fileIndexService = fileIndexService;
	}

	public ConfigureDao getConfigureDao() {
		return configureDao;
	}

	public void setConfigureDao(ConfigureDao configureDao) {
		this.configureDao = configureDao;
	}

	public DocumentAuditService getDocumentAuditService() {
		return documentAuditService;
	}

	public void setDocumentAuditService(DocumentAuditService documentAuditService) {
		this.documentAuditService = documentAuditService;
	}

	/** 查询影像文件的HQL语句 */
	public static final String HQL_QUERY_IMAGES = "FROM " + FileIndex.class.getName()
			+ " WHERE businessNo = ? AND systemCode = ? AND property01 IN (?, ?, ?, ?)";

	/**
	 * 查询docId对应的影像文件
	 * 
	 * @param docId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FileIndex> queryImages(String docId) {
		return (List<FileIndex>) fileIndexService
				.query(HQL_QUERY_IMAGES,
						new Object[] { docId, SystemUtils.SYSTEM_CODE, SystemUtils.FILE_MODEL_IMAGE,
								SystemUtils.FILE_MODEL_MANUAL, SystemUtils.FILE_MODEL_E_POLICY,
								SystemUtils.FILE_MODEL_OTHER });
	}

	/** 查询保单/投保单影像文件的HQL语句 */
	public static final String HQL_QUERY_POLICY_IMAGES = "FROM " + FileIndex.class.getName()
			+ " WHERE businessNo IN (?, ?) AND systemCode = ? AND property01 IN (?, ?, ?, ?)";

	/**
	 * 查询Document对应的影像文件.如果document是保单,则查询FileIndex中businessNo为保单号或投保单号的数据
	 * 
	 * @param document
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FileIndex> queryImages(Document document) {
		String businessNo = document.getId();
		if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(document.getType())) {
			// 如果是保单,通过投保单号或保单号进行影像文件查询
			return (List<FileIndex>) fileIndexService.query(HQL_QUERY_POLICY_IMAGES,
					new Object[] { businessNo, document.getPolicyNo(), SystemUtils.SYSTEM_CODE,
							SystemUtils.FILE_MODEL_IMAGE, SystemUtils.FILE_MODEL_MANUAL,
							SystemUtils.FILE_MODEL_E_POLICY, SystemUtils.FILE_MODEL_OTHER });
		}

		// 根据document.id查询影像文件
		return this.queryImages(businessNo);
	}

	/**
	 * 根据业务号得到对应的单证类型
	 * 
	 * @param documentNo 投保单号/保单号/批单号/单证号
	 * @return
	 */
	public static String getDocumentType(String documentNo) {
		if (SystemUtils.POLICY_NO_LENGTH == documentNo.length()) {
			// 投保单号/保单号/批单号的长度为22
			if (documentNo.startsWith(SystemUtils.DOCUMENT_TYPE_PROPOSAL)) return SystemUtils.DOCUMENT_TYPE_PROPOSAL;
			if (documentNo.startsWith(SystemUtils.DOCUMENT_TYPE_POLICY)) return SystemUtils.DOCUMENT_TYPE_POLICY;
			if (documentNo.startsWith(SystemUtils.DOCUMENT_TYPE_ENDOR)) return SystemUtils.DOCUMENT_TYPE_ENDOR;
		} else {
			// 以V开头为单证，注意在单证类型的时候，NO存的是单证号，没有加V，ID才是V+单证号
			if (documentNo.startsWith(SystemUtils.DOCUMENT_TYPE_VISA)) return SystemUtils.DOCUMENT_TYPE_VISA;
		}

		return SystemUtils.DOCUMENT_TYPE_VISA;
	}

	/**
	 * 根据业务号得到对应的资料类型
	 * 
	 * @param documentNo
	 * @return
	 */
	public static String getDocumentFileType(String documentNo) {
		String type = getDocumentType(documentNo);
		if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(type)) return SystemUtils.FILE_TYPE_PROPOSAL;
		if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(type)) return SystemUtils.FILE_TYPE_POLICY;
		if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(type)) return SystemUtils.FILE_TYPE_ENDOR;
		if (SystemUtils.DOCUMENT_TYPE_VISA.equals(type)) return SystemUtils.FILE_TYPE_VISA_LOST;
		return SystemUtils.FILE_TYPE_PROPOSAL;
	}

	ProductDao productDao;
	FileBoxVersionService fileBoxVersionService;
	FileBoxService fileBoxService;
	DocumentFileDao documentFileDao;
	FileService fileService;
	FileTypeService fileTypeService;
	FileDeadlineDao fileDeadlineDao;
	List<FileBox> fileBoxs;

	public FileDeadlineDao getFileDeadlineDao() {
		return fileDeadlineDao;
	}

	public void setFileDeadlineDao(FileDeadlineDao fileDeadlineDao) {
		this.fileDeadlineDao = fileDeadlineDao;
	}

	public FileTypeService getFileTypeService() {
		return fileTypeService;
	}

	public void setFileTypeService(FileTypeService fileTypeService) {
		this.fileTypeService = fileTypeService;
	}

	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public DocumentFileDao getDocumentFileDao() {
		return documentFileDao;
	}

	public void setDocumentFileDao(DocumentFileDao documentFileDao) {
		this.documentFileDao = documentFileDao;
	}

	public FileBoxService getFileBoxService() {
		return fileBoxService;
	}

	public void setFileBoxService(FileBoxService fileBoxService) {
		this.fileBoxService = fileBoxService;
	}

	public FileBoxVersionService getFileBoxVersionService() {
		return fileBoxVersionService;
	}

	public void setFileBoxVersionService(FileBoxVersionService fileBoxVersionService) {
		this.fileBoxVersionService = fileBoxVersionService;
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	// /**
	// * 利用dom4j解析传到后台来的XML字符串 xml:xml的路径
	// */
	// public List<Object[]> resolveXml(String xml, UserSessionEntity user) {
	// org.dom4j.Document doc = null;
	// Iterator documentIter, fileIter;
	// String boxNo = null;
	// Object[] row = null;
	// List<Object[]> list = new ArrayList<Object[]>();
	// int pageIndex = 1;
	// try {
	// doc = org.dom4j.DocumentHelper.parseText(xml);
	// // 获取根节点
	// org.dom4j.Element rootElt = doc.getRootElement();
	// // 获取根节点下的所有document节点的迭代
	// documentIter = rootElt.elementIterator("document");
	// // 产生扫描这批单证的批次号
	// String batchNo = StringHelper.randomUUID();
	// while (documentIter.hasNext()) {
	// Element documentElement = (Element) documentIter.next();
	// // 获取每个单证的业务号
	// String documentId = documentElement.attributeValue("id");
	// // 获取单证下的文件集
	// fileIter = documentElement.elementIterator("file");
	// Document document = getDocument(documentId);
	// // 得到当前业务号对应的产品类型
	// String riskType = document.getRiskType();
	// while (fileIter.hasNext()) {
	// Element fileElement = (Element) fileIter.next();
	// // 得到单个文件的文件类型
	// String documentFileType = fileElement.attributeValue("type");
	// // 得到这个文件下面的文件id，name，count，size元素的值
	// String id = fileElement.elementText("id");
	// String name = fileElement.elementText("name");
	// int count = NumberHelper.intValue(fileElement.elementText("count"));
	// String size = fileElement.elementText("size");
	//
	// // 分配档案盒
	// FileBox fileBox = oprateFileBox(user, riskType, NumberHelper.intValue(count));
	// if (boxNo == null || !boxNo.equals(fileBox.getId())) {
	// // 新档案盒
	// pageIndex = row == null ? 1 : (Integer) row[5] + 1;
	// row = new Object[5];
	// row[0] = fileBox.getId() + ";" + batchNo; // 组建的是档案盒号和本次扫描的批次号
	// row[1] = fileBox.getId();
	// row[4] = pageIndex; // 起始页数
	// row[5] = (Integer) row[1] + count; // 终止页数
	// row[2] = "第" + row[4] + " - " + row[5] + "页"; // 装盒信息
	// row[3] = (Integer) row[5] - (Integer) row[4]; // 总页数
	// list.add(row);
	// } else {
	// // 原档案盒
	// row[5] = (Integer) row[5] + count; // 终止页数
	// }
	// boxNo = fileBox.getId();
	//
	// File file = new File();
	// file.setFileSize(NumberHelper.intValue(size));
	// file.setFileName(StringHelper.trim(name));
	// file.setPageCount(NumberHelper.intValue(count));
	// file.setFileId(StringHelper.trim(id));
	// file.setLent("0");
	// file.setFileBox(fileBox);
	// // 设置档案编码
	// // file.setNo(fileService.makeFileCode(fileBox.getId()));
	// // 设置所属单证,业务号唯一，只能够查出一个投保单或者保单或者批单
	// DocumentFile documentFile = getDocumentFile(documentId, documentFileType);
	// file.setDocument(document);
	// file.setFileType(fileTypeService.get(documentFile));
	// file.setEffectiveTime(documentFile.getEffectiveTime());
	// file.setDueTime(documentFile.getDueTime());
	// file.setInsertTime(new Date());
	// file.setCompany(user.getCurrentCompany());
	// file.setPageIndex(pageIndex);
	// file.setBatchNo(batchNo);
	// fileService.save(file);
	//
	// // 回写T_DOCUMENT_FILE
	// documentFile.setFile(file);
	// documentFile.setFileTime(file.getInsertTime());
	// documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_FILE);
	// documentFile.setFileType(fileTypeService.get(documentFile));
	// documentFileDao.saveOrUpdate(documentFile);
	// }
	// // rollBackDocument(document);
	// // 回写document
	// document.setFileTime(new Date());
	// document.setInsertTime(new Date());
	// document.setUpdateTime(new Date());
	// document.setUser(user);
	// document.setDepartment(user.getDepartment());
	// document.setCompany(user.getCurrentCompany());
	// List<DocumentFile> documentFiles = this.archingFiles(document);
	// if (documentFiles != null && (documentFiles.size() > 0)) {
	// // 表示归档不齐
	// document.setFileStatus(SystemUtils.DOCUMENT_STATUS_LACK);
	// Iterator<DocumentFile> it = documentFiles.iterator();
	// String lackMessage = "";
	// while (it.hasNext()) {
	// lackMessage += "差" + it.next().getFileType().getAlias() + ",";
	// }
	// document.setLacks(lackMessage);
	// } else {
	// // 归档齐全
	// document.setFileStatus(SystemUtils.DOCUMENT_STATUS_FILE);
	// }
	// dao.saveOrUpdate(document);
	// }
	// } catch (DocumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return list;
	// }

	// /**
	// * 创建档案盒
	// *
	// * @param User
	// * 当前操作管理员
	// * @param riskType
	// * 险种大类，分车险和非车险
	// * @param fileCount
	// * 档案资料数量
	// * @return 档案盒
	// */
	// public FileBox oprateFileBox(UserSessionEntity User, String riskType, int fileCount) {
	// FileBoxVersion fileBoxVersion = fileBoxVersionService.getCurrentVersion(User.getCurrentCompany().getId());
	// if (fileBoxVersion == null) {
	// // 抛出异常，创建档案盒之前，必须设置分公司档案盒版本
	// throw new RuntimeException("请先设置档案盒版本！");
	// }
	// FileBox fileBox = fileBoxService.haveFileBox(fileBoxVersion, riskType);
	// if (fileBox != null) {
	// boolean isFull = fileBoxService.fullBox(fileBox, fileCount);
	// if (!isFull) {
	// // 有档案盒并且档案盒没有装满
	// fileBox.setCapacity(fileBox.getCapacity() + fileCount);
	// } else {
	// // 这个档案盒不用了
	// // fileBox.setStatus(SystemUtils.FILEBOX_STATUS_NO);
	// fileBoxService.getDao().saveOrUpdate(fileBox);
	// // 创建新的档案盒
	// String fileBoxNo = fileBoxService.makeFileBoxNo(fileBoxVersion.getCompany().getId(), riskType);
	// fileBox = fileBoxService.createFileBox(fileBoxVersion, User, fileBoxNo);
	// fileBox.setCapacity(fileCount);
	// }
	// } else {
	// // 没有档案盒则创建一个新的档案盒
	// String fileBoxNo = fileBoxService.makeFileBoxNo(fileBoxVersion.getCompany().getId(), riskType);
	// fileBox = fileBoxService.createFileBox(fileBoxVersion, User, fileBoxNo);
	// fileBox.setCapacity(fileCount);
	// }
	// fileBox.setUpdateDepartment(User.getDepartment());
	// fileBox.setUpdateUser(User);
	// fileBox.setUpdateTime(new Date());
	// fileBoxService.getDao().saveOrUpdate(fileBox);
	// return fileBox;
	// }

	// /**
	// * @param no
	// * 业务号
	// * @param type
	// * 文件类型
	// * @return
	// */
	// public DocumentFile getDocumentFile(String no, String type) {
	// StringBuffer hql = new StringBuffer();
	// hql.append(" FROM ").append(DocumentFile.class.getName()).append(" d ");
	// hql.append(" WHERE d.document.no=? AND d.fileType.id=?");
	// return (DocumentFile) dao.uniqueResult(hql.toString(), new Object[] { no, type });
	// }
	//
	// /**
	// * @param document
	// * 单证
	// * @return
	// */
	// public List<DocumentFile> archingFiles(Document document) {
	// StringBuffer hql = new StringBuffer();
	// hql.append(" FROM ").append(DocumentFile.class.getName()).append(" d ");
	// hql.append(" WHERE d.document.no = ? AND d.status = ?");
	// List<DocumentFile> documentFiles = (List<DocumentFile>) documentFileDao.query(hql.toString(), new Object[] {
	// document.getNo(), SystemUtils.DOCUMENT_FILE_STATUS_LACK });
	// return documentFiles;
	// }

	/**
	 * 查询在documentNo下所有的核保所需资料类型.非核保所需资料不会查询出来
	 * 
	 * @param documentNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FileType> getFileTypes(Document document) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT DISTINCT f.fileType FROM ");
		hql.append(DocumentFile.class.getName()).append(" f ");
		hql.append("WHERE f.fileModel = ? AND f.document = ? AND f.required = ?");
		return (List<FileType>) dao.query(hql.toString(),
				new Object[] { SystemUtils.FILE_MODEL_FILE, document, SystemUtils.YES });
	}

	/**
	 * 根据业务号查询文档
	 * 
	 * @param documentNo
	 *            业务号,注意此业务号不是no字段,主要是因为在投保单时,业务号no=投保单号,一旦转为保单,则其业务号no为保单号,此时no=投保单号的数据是不存在的
	 * @return
	 * @see #getDocumentType(String)
	 */
	public Document getDocument(String documentNo) {
		Document e = dao.getByProperty("no", documentNo);
		if (e == null) e = dao.get(documentNo);
		if (e != null) return e;
		// 出现需要进入下面查询的主要情况是可能documentNo为投保单号,且已经生成了保单
		String documentType = getDocumentType(documentNo);
		if (!SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(documentType)) return null; // 不存在
		StringBuilder hql = new StringBuilder();
		hql.append("FROM ").append(Document.class.getName()).append(" d WHERE ");
		List<String> parameters = new ArrayList<String>();
		// parameters.add(SystemUtils.FILE_MODEL_FILE);
		parameters.add(documentNo);
		parameters.add(documentType);
		// if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(documentType)) {
		// 投保单号
		hql.append("d.proposalNo = ? AND d.type IN (?, ?) ");
		parameters.add(SystemUtils.DOCUMENT_TYPE_POLICY);
		// } else if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(documentType)) {
		// // 保单号
		// hql.append("d.policyNo = ? AND d.type IN (?) ");
		// // parameters.add(SystemUtils.DOCUMENT_TYPE_POLICY);
		// } else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(documentType)) {
		// // 批单号
		// hql.append("d.endorNo = ? AND d.type IN (?) ");
		// }

		return (Document) dao.uniqueResult(hql.toString(), parameters.toArray());
	}

	/**
	 * 根据业务号查询其下fileTypeId的档案资料数量
	 * 
	 * @param documentNo
	 *            业务号
	 * @param fileTypeId
	 *            档案资料类型
	 * @return
	 * @see #getDocumentType(String)
	 */
	public int countDocumentFiles(String documentNo, String fileTypeId) {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT COUNT(*) FROM ").append(DocumentFile.class.getName()).append(" f JOIN f.document d ");
		hql.append(" WHERE f.fileType.id = ? ");
		List<String> parameters = new ArrayList<String>();
		String documentType = getDocumentType(documentNo);
		parameters.add(fileTypeId);
		parameters.add(documentNo);
		parameters.add(documentType);
		if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(documentType)) {
			// 投保单号
			hql.append("AND d.proposalNo = ? AND d.type IN (?, ?) ");
			parameters.add(SystemUtils.DOCUMENT_TYPE_POLICY);
		} else if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(documentType)) {
			// 保单号
			hql.append("AND d.policyNo = ? AND d.type IN (?) ");
			parameters.add(SystemUtils.DOCUMENT_TYPE_POLICY);
		} else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(documentType)) {
			// 批单号
			hql.append("AND d.endorNo = ? AND d.type IN (?) ");
		}
		return (Integer) dao.uniqueResult(hql.toString(), parameters.toArray());
	}

	/**
	 * 根据业务号查询其下fileTypeId的档案资料信息
	 * 
	 * @param documentNo
	 *            业务号
	 * @param fileTypeId
	 *            档案资料类型,FileType.id
	 * @return
	 * @see #getDocumentType(String)
	 */
	@SuppressWarnings("unchecked")
	public List<DocumentFile> getDocumentFiles(String documentNo, String fileTypeId) {
		StringBuilder hql = new StringBuilder();
		// hql.append(" FROM ").append(DocumentFile.class.getName()).append(" f JOIN f.document d ");
		hql.append(" FROM ").append(DocumentFile.class.getName()).append(" f ");
		hql.append(" WHERE f.fileType.id = ?");
		List<String> parameters = new ArrayList<String>();
		String documentType = getDocumentType(documentNo);
		parameters.add(fileTypeId);
		parameters.add(documentNo);
		parameters.add(documentType);
		if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(documentType)) {
			// 投保单号
			hql.append("AND f.document.proposalNo = ? AND f.document.type IN (?, ?) ");
			parameters.add(SystemUtils.DOCUMENT_TYPE_POLICY);
		} else if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(documentType)) {
			// 保单号
			hql.append("AND f.document.policyNo = ? AND f.document.type IN (?) ");
			parameters.add(SystemUtils.DOCUMENT_TYPE_POLICY);
		} else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(documentType)) {
			// 批单号
			hql.append("AND f.document.endorNo = ? AND f.document.type IN (?) ");
		}
		return (List<DocumentFile>) dao.query(hql.toString(), parameters.toArray());
	}

	/**
	 * 得到一个档案下的fileTypeId类型的有效文件(已归档或差缺)记录,如果有多个返回第一个尚未归档的数据,如果都已归档则返回第一个.
	 * 如果全部都已禁用,则返回一个
	 * 
	 * @param document
	 * @param fileTypeId
	 * @param excludeIds
	 * @return
	 */
	public DocumentFile getDocumentFile(final Document document, final String fileTypeId,
			final Set<String> excludeIds) {
		StringBuffer hql = new StringBuffer();
		List<Object> parameters = new ArrayList<Object>();
		hql.append("FROM ").append(DocumentFile.class.getName()).append(" f ");
		hql.append("WHERE f.status = ? AND f.document = ? AND f.fileType.id = ? ");
		parameters.add(SystemUtils.DOCUMENT_FILE_STATUS_LACK);
		parameters.add(document);
		parameters.add(fileTypeId);
		if (!Helper.isEmpty(excludeIds)) {
			hql.append(" AND f.id NOT IN(?");
			hql.append(StringHelper.copy(", ?", excludeIds.size() - 1));
			hql.append(")");
			parameters.addAll(excludeIds);
		}
		hql.append(" ORDER BY fileTime");

		// 先查找是否有未归档的文件
		List<?> list = dao.query(hql.toString(), parameters.toArray(), 1, 1).list();
		if (!Helper.isEmpty(list)) return (DocumentFile) list.get(0);
		// 如果没有未归档的文件则查找已归档的文件
		parameters.remove(0);
		parameters.add(0, SystemUtils.DOCUMENT_FILE_STATUS_FILE);
		list = dao.query(hql.toString(), parameters.toArray(), 1, 1).list();
		if (!Helper.isEmpty(list)) return (DocumentFile) list.get(0);

		// 返回被禁用的资料
		hql.insert(hql.indexOf(" ORDER BY "), " AND f.another IS NULL ");
		parameters.remove(0);
		parameters.add(0, SystemUtils.DOCUMENT_FILE_STATUS_DISABLED);
		list = dao.query(hql.toString(), parameters.toArray(), 1, 1).list();
		if (!Helper.isEmpty(list)) return (DocumentFile) list.get(0);

		return null;
	}

	/**
	 * 得到一个档案下的fileTypeId类型的有效文件(已归档或差缺)记录,如果有多个返回第一个尚未归档的数据,如果都已归档则返回第一个.
	 * 
	 * @param document
	 * @param fileTypeId FileType.id
	 * @return
	 */
	public DocumentFile getDocumentFile(final Document document, final String fileTypeId) {
		StringBuffer hql = new StringBuffer();
		hql.append("FROM ").append(DocumentFile.class.getName()).append(" f ");
		hql.append("WHERE f.document = ? AND f.fileType.id = ? AND f.status = ? ORDER BY fileTime");
		// 先查找是否有未归档的文件
		List<?> list = dao.query(hql.toString(),
				new Object[] { document, fileTypeId, SystemUtils.DOCUMENT_FILE_STATUS_LACK }, 1, 1).list();
		if (!Helper.isEmpty(list)) return (DocumentFile) list.get(0);
		// 如果没有未归档的文件则查找已归档的文件
		list = dao.query(hql.toString(), new Object[] { document, fileTypeId, SystemUtils.DOCUMENT_FILE_STATUS_FILE },
				1, 1).list();
		if (!Helper.isEmpty(list)) return (DocumentFile) list.get(0);

		// 返回最后一个上传的被禁用的资料
		hql.insert(hql.indexOf(" ORDER BY "), " AND f.another IS NULL ");
		list = dao.query(hql.toString(),
				new Object[] { document, fileTypeId, SystemUtils.DOCUMENT_FILE_STATUS_DISABLED }, 1, 1).list();
		if (!Helper.isEmpty(list)) return (DocumentFile) list.get(0);

		return null;
	}

	/**
	 * 联合投保时共享文件的操作,将source共享至target
	 * 
	 * @param source
	 * @param target
	 * @return target
	 * @see #share(DocumentFile, DocumentFile, String)
	 */
	protected DocumentFile share(DocumentFile source, DocumentFile target) {
		return share(source, target, SystemUtils.YES);
	}

	/**
	 * 共享文件的操作,将source共享至target
	 * 
	 * @param source
	 * @param target
	 * @param shared 是(1)否(0)为联合投保的共享
	 * @return target
	 * @see SystemUtils#YES
	 * @see SystemUtils#NO
	 */
	protected DocumentFile share(DocumentFile source, DocumentFile target, String shared) {
		target.setFile(source.getFile());
		target.setStatus(source.getStatus());
		target.setFileTime(new Date());
		target.setShared(shared);
		target.setSharedFrom(source);
		documentFileDao.update(target);

		if (SystemUtils.YES.equals(shared)) {
			// 联合投保的资料共享
			source.setShared(shared);
			source.setSharedFrom(target);
			documentFileDao.update(source);
		}

		return target;
	}

	/**
	 * 
	 * 将documentFile文件进行共享文件操作.将documentFile共享至其他业务数据中
	 * 
	 * @param documentFile 要被共享的文件,此文件必须是已归档,且paperType,paperCode不为空,且为可共享的文件(shared==1)
	 * @return 共享过的文件
	 */
	@SuppressWarnings("unchecked")
	public List<DocumentFile> share(DocumentFile documentFile) {
		if (!SystemUtils.DOCUMENT_FILE_STATUS_FILE.equals(documentFile.getStatus())) return null;
		FileType fileType = documentFile.getFileType();
		if (!SystemUtils.YES.equals(fileType.getShared())) return null; // 只有是可共享的文件才能共享
		String paperType = documentFile.getPaperType();
		String paperCode = documentFile.getPaperCode();
		if (Helper.isEmpty(paperType) || Helper.isEmpty(paperCode)) return null; // 仅可唯一识别资料的文件可共享

		List<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(DocumentFile.class.getName()).append(" d ");
		hql.append(" WHERE d.paperType = ? AND d.paperCode = ? AND d.status = ? AND ( d.fileType = ? OR ");
		args.add(paperType);
		args.add(paperCode);
		args.add(SystemUtils.DOCUMENT_FILE_STATUS_LACK);
		args.add(fileType);
		if (Helper.contains(SystemUtils.FILE_TYPE_CLIENT_CODES, fileType.getCode())) {
			// 身份证的共享
			hql.append(" d.fileTypeCode IN (?");
			hql.append(StringHelper.copy(", ?", SystemUtils.FILE_TYPE_CLIENT_CODES.length - 1));
			hql.append(")");
			CollectionHelper.add(args, SystemUtils.FILE_TYPE_CLIENT_CODES);
		} else {
			hql.append(" d.fileTypeCode = ? ");
			args.add(documentFile.getFileTypeCode());
		}
		hql.append(" )");
		List<DocumentFile> list = (List<DocumentFile>) dao.query(hql.toString(), args.toArray());
		if (Helper.isEmpty(list)) return list;
		Map<String, Document> documents = new HashMap<String, Document>();
		Document doc;
		String docId;
		File file = documentFile.getFile();
		for (DocumentFile docFile : list) {
			// docFile.setFile(file);
			// // 设置归档状态
			// docFile.setStatus(documentFile.getStatus());
			// // 设置归档时间
			// docFile.setFileTime(new Date());
			// docFile.setShared(SystemUtils.YES);
			// docFile.setSharedFrom(documentFile);
			//
			// documentFileDao.update(docFile);

			share(documentFile, docFile, SystemUtils.NO);

			doc = docFile.getDocument();
			docId = doc.getId();
			if (!documents.containsKey(docId)) documents.put(docId, doc);
		}
		UserSessionEntity user = new UserSessionEntity(file.getUser(), file.getDepartment(), file.getCompany());
		for (Document document : documents.values()) {
			changeDocumentFileStatus(document, user);
		}
		return list;
	}

	/**
	 * 手工共享承保资料文件
	 * 
	 * @param attchNos 要被共享的业务号
	 * @param filesIds 要共享的资料
	 * @param user 系统当前登录人
	 * @return
	 */
	public Object[] shareManually(String[] attchNos, String[] filesIds, UserSessionEntity user) {
		Object[] files = new Object[filesIds.length];
		Document doc;
		// Map<String, DocumentFile> map = new HashMap<String, DocumentFile>();
		for (int i = 0; i < attchNos.length; i++) {
			doc = dao.get(attchNos[i]);
			for (int j = 0; j < filesIds.length; j++) {
				// File shareFile = fileService.get(filesIds[j]);
				DocumentFile shareFile = documentFileDao.get(filesIds[j]);
				files[j] = shareFile.getFileType().getName();
				// String fileTypeId = shareFile.getFileType().getId();
				// DocumentFile documentFile = getDocumentFile(doc, fileTypeId);

				DocumentFile documentFile = saveDocumentFile(doc, shareFile.getFileType(), shareFile.getFile(), null);

				// if (documentFile == null || map.get(documentFile.getId()) != null) {
				// // 新建
				// documentFile = new DocumentFile();
				// Helper.copy(shareFile, documentFile, new String[] { "id", "document", "required" });
				// documentFile.setDocument(doc);
				// documentFile.setRequired(SystemUtils.NO);
				// documentFileDao.save(documentFile);
				// } else {
				// if (SystemUtils.DOCUMENT_FILE_STATUS_FILE.equals(documentFile.getStatus())) {
				// // 已经归档
				// documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_DISABLED);
				// documentFileDao.update(documentFile);
				//
				// String required = documentFile.getRequired();
				// documentFile = new DocumentFile();
				// Helper.copy(shareFile, documentFile, new String[] { "id", "document", "required" });
				// documentFile.setDocument(doc);
				// documentFile.setRequired(required);
				// documentFileDao.save(documentFile);
				// } else {
				// // 未归档
				// Helper.copy(shareFile, documentFile, new String[] { "id", "document", "required" });
				// // 更新原来的文件
				// documentFileDao.update(documentFile);
				// }
				// }

				share(shareFile, documentFile, SystemUtils.NO);
				// map.put(documentFile.getId(), documentFile);
			}
			changeDocumentFileStatus(doc, user);
		}
		return files;
	}

	/**
	 * 改变单证的归档状态
	 * 
	 * @param entity 单证对象
	 * @param user 操作人,可以为null
	 */
	public void changeDocumentFileStatus(Document entity, UserSessionEntity user) {
		StringBuilder lacks = new StringBuilder(); // 差缺信息
		List<DocumentFile> files = documentFileDao.queryByProperty(
				new Object[][] { { "document", entity }, { "status", SystemUtils.DOCUMENT_FILE_STATUS_LACK } });
		Set<String> names = new HashSet<String>(); // 差缺的资料类型，不重复
		for (DocumentFile f : files) {
			if (f.getFileType() == null) continue;
			if (names.contains(f.getFileType().getName())) continue; // 已有此查缺资料
			names.add(f.getFileType().getName());
			lacks.append(",").append(f.getFileType().getName());
		}

		String documentFileStatus = entity.getFileStatus();
		if (StringHelper.isEmpty(lacks)) {
			// 归档齐全
			entity.setLacks(null);
			entity.setFileStatus(SystemUtils.DOCUMENT_STATUS_FILE);
		} else if (!SystemUtils.DOCUMENT_STATUS_FILE_MANUAL.equals(documentFileStatus)) { // 特批归档不进行处理
			String hql = "SELECT COUNT(*) FROM " + DocumentFile.class.getName() + " WHERE document = ? AND status = ?";
			int filedCount = NumberHelper.intValue(dao.uniqueResult(hql, new Object[] { entity, SystemUtils.DOCUMENT_FILE_STATUS_FILE }));
			if (filedCount < 1) {
				// 尚未归档
				entity.setLacks(SystemUtils.LACK_MESSAGE);
				entity.setFileStatus(SystemUtils.DOCUMENT_STATUS_UNFILE);
			} else {
				// 有差缺,归档不齐
				lacks.deleteCharAt(0);
				entity.setLacks(StringHelper.substringLeft(lacks.toString(), 1000));
				entity.setFileStatus(SystemUtils.DOCUMENT_STATUS_LACK);
			}
		}

		Date now = new Date();
		if (user != null) {
			entity.setUpdateUser(user.getInstance());
			entity.setUpdateDepartment(user.getCurrentDepartment());
			if (!SystemUtils.DOCUMENT_STATUS_UNFILE.equals(entity.getFileStatus())
					&& (SystemUtils.DOCUMENT_STATUS_UNFILE.equals(documentFileStatus) || entity.getUser() == null)) {
				// 第一次归档
				entity.setInsertTime(now);
				entity.setUser(user.getInstance());
				entity.setDepartment(user.getCurrentDepartment());
				entity.setCompany(user.getCurrentCompany());
			}
		}

		if (SystemUtils.DOCUMENT_STATUS_UNFILE.equals(entity.getFileStatus())) {
			entity.setInsertTime(null);
			entity.setFileTime(null);
			entity.setUser(null);
			entity.setDepartment(null);
			entity.setCompany(null);
		} else {
			entity.setFileTime(now);
		}
		entity.setUpdateTime(now);
		dao.update(entity);
	}

	/**
	 * 单份档案处理时,手动归档
	 * 
	 * @param ids
	 *            资料类型主键
	 * @param reason
	 *            手动归档原因
	 */
	public Document manual(String id, String reason) {
		Document document = dao.get(id);
		/*
		 * List<DocumentFile> documentFiles = new ArrayList<DocumentFile>(); for (int i = 0; i < ids.length; i++) {
		 * DocumentFile documentFile = documentFileDao.get(ids[i]); document = documentFile.getDocument();
		 * documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_FILE); documentFiles.add(documentFile);
		 * documentFileDao.update(documentFile); }
		 */
		document.setFileStatus(SystemUtils.DOCUMENT_STATUS_FILE_MANUAL);
		document.setManuallyReason(reason);
		dao.update(document);
		return document;
	}

	/**
	 * 根据XML字符串保存数据.单份上传的保存
	 * 
	 * @param xmlString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject saveForUpload(String xmlString, UserSessionEntity user) {
		JSONObject json = new JSONObject();
		json.put("success", true);
		try {
			org.dom4j.Document xml = DocumentHelper.parseText(xmlString);
			Element root = xml.getRootElement();

			List<Element> docs = root.elements("document");
			int count = 0;
			String documentNo;
			Document entity = null;
			Document anotherDoc;
			JSONArray docFiles = new JSONArray();
			json.put("files", docFiles);
			FileType fileType;
			for (Element doc : docs) {
				if (StringHelper.isTrue(doc.elementText("remove"))) continue; // 删除此数据
				documentNo = doc.attributeValue("id");
				entity = getDocument(documentNo);
				anotherDoc = entity.getAnother();
				List<Element> files = doc.elements("file");

				for (Element fileElement : files) {
					count = NumberHelper.intValue(fileElement.elementText("count"));
					String fileTypeId = fileElement.attributeValue("type");
					fileType = fileTypeService.get(fileTypeId);

					String fileId = fileElement.elementText("id");
					String fileName = fileElement.elementText("name");
					int fileSize = NumberHelper.intValue(fileElement.elementText("size"));

					String anotherId = fileElement.elementText("another");
					DocumentFile another = null;
					if (!StringHelper.isEmpty(anotherId)) { // 要被覆盖的文件
						another = documentFileDao.get(anotherId);
					}
					File file = new File();
					file.setFileModel(SystemUtils.FILE_MODEL_FILE);
					file.setNo(SystemUtils.FILE_NO_IMAGE);
					file.setFileId(fileId);
					file.setFileName(fileName);
					file.setFileSize(fileSize);
					file.setDocument(entity);
					file.setPageCount(count);
					file.setLent(SystemUtils.FILE_LENT_NO);
					file.setFileType(fileType);
					file.setUser(user.getInstance());
					file.setDepartment(user.getCurrentDepartment());
					file.setCompany(user.getCurrentCompany());
					file.setInsertTime(new Date());
					fileService.save(file);

					DocumentFile docFile = saveDocumentFile(entity, fileType, file, another);

					JSONObject docFileJSON = new JSONObject(docFile);
					docFileJSON.put("fileType", fileType);
					docFileJSON.put("file", docFile.getFile());
					docFiles.put(docFileJSON);

					// /* 保存到联合投保的另外一张保单上 */
					// if (anotherDoc != null) copyDocumentFile(anotherDoc, docFile);

					// 每个文件一条装盒明细
					// details.put(new Object[] { fileBox.getId(), pageIndex, count, file.getNo(),
					// file.getFileType().getName(), entity.getNo(), entity.getApplicant() });
				}

				changeDocumentFileStatus(entity, user);
				if (anotherDoc != null) changeDocumentFileStatus(anotherDoc, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.put("success", false);
			json.put("message", e.getMessage());
		}

		return json;
	}

	/**
	 * 保存影像文件.此方法主要用于将已经上传的影像文件保存至联合投保的另一保单下.
	 * 注意:影像文件仅存放在影像数据库FILE_INDEX中
	 * 
	 * @param xmlString XML格式的数据
	 * @param user 操作人
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FileIndex> saveImages(String xmlString, UserSessionEntity user) {
		try {
			org.dom4j.Document xml = DocumentHelper.parseText(xmlString);
			Element root = xml.getRootElement();

			List<Element> docs = root.elements("document");
			// int count = 0;
			String documentId;
			Document entity = null;
			Document anotherDoc;
			FileIndex fileIndex;
			List<FileIndex> files = new ArrayList<FileIndex>();
			FileType fileType;
			for (Element doc : docs) {
				documentId = doc.attributeValue("id");
				entity = get(documentId);
				anotherDoc = entity.getAnother();
				if (anotherDoc == null) continue; // 不是联合投保则不进行处理
				List<Element> fileDocs = doc.elements("file");
				for (Element fileElement : fileDocs) {
					String fileTypeId = fileElement.attributeValue("type");
					fileType = fileTypeService.get(fileTypeId);
					/* 不允许共享的单证 */
					if (!SystemUtils.YES.equals(fileType.getShared())) continue;

					String fileId = fileElement.elementText("id");
					fileIndex = new FileIndex();
					fileIndex.setBusinessNo(anotherDoc.getId());
					fileIndexService.copy(fileId, fileIndex);
					files.add(fileIndex);
				}
			}

			return files;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 为doc保存一个新的DocumentFile对象.
	 * 
	 * @param doc
	 * @param fileType
	 * @param file
	 * @param another 被覆盖的对象,可以为null
	 * @return {保存的DocumentFile对象, 被覆盖的DocumentFile对象或null}
	 * @see #saveDocumentFile(Document, FileType, File, DocumentFile)
	 */
	private DocumentFile[] saveDocumentFileInternal(final Document doc, final FileType fileType, final File file,
			DocumentFile another) {
		String fileTypeId = fileType.getId();
		if (another == null) {
			// 未指定被覆盖的文件,则需要查询是否有需要被覆盖的文件
			another = getDocumentFile(doc, fileTypeId);
		}
		DocumentFile docFile = null; // 需要保存的DocumentFile对象
		boolean hasAnother = another != null; // 是否有需要覆盖的对象
		boolean hasFile = false; // 次资料是否有原始的差缺对象
		if (hasAnother) {
			// anotherDoc = another.getDocument();
			if (!doc.getId().equals(another.getDocument().getId())) {
				// 参数有误
				throw new CustomException(
						"参数错误,another.document(" + another.getDocument().getId() + ")与doc(" + doc.getId() + ")不一致!");
			}
			if (!fileTypeId.equals(another.getFileType().getId())) {
				// 参数有误
				throw new CustomException(
						"参数错误,扫描文件类型(" + fileTypeId + ")与被覆盖文件类型(" + another.getFileType().getId() + ")不一致!");
			}
			if (SystemUtils.DOCUMENT_FILE_STATUS_LACK.equals(another.getStatus())) {
				// another为差缺,则直接保存docFile
				docFile = another;
				hasAnother = false;
				hasFile = true;
			} else { // if (SystemUtils.DOCUMENT_FILE_STATUS_FILE.equals(another.getStatus())) {
				// 如果不差缺文件,则进行覆盖.禁用原来的文件
				docFile = new DocumentFile();
				Helper.copyValues(another, docFile, new String[] { "id", "another", "shared", "sharedFrom" });
				// documentFileDao.save(docFile);
			}
		} else {
			// doc中没有fileType对应的文件对象,属于新增资料，则需要对
			PrpDUwmtypes prpDUwmtypes = new PrpDUwmtypes();
			prpDUwmtypes.setId(new PrpDUwmtypesId(doc.getId(), fileType.getCode())); // 新增的资料
			Set<DocumentFile> docFiles = documentAuditService.createDocumntFiles(doc, prpDUwmtypes);
			if (Helper.isEmpty(docFiles)) {
				// 理论上是不会出现这种情况，documentAuditService.createDocumntFiles(doc, prpDUwmtypes)至少会返回一个DocumentFile对象
				docFile = new DocumentFile();
				docFile.setDocument(doc);
				docFile.setFileType(fileType);
				docFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
				docFile.setFileTypeCode(fileType.getCode());
			} else docFile = docFiles.iterator().next();
			docFile.setRequired(SystemUtils.NO);
		}

		docFile.setFile(file);
		docFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_FILE);
		docFile.setFileTime(file.getInsertTime());

		if (hasFile) documentFileDao.update(docFile);
		else documentFileDao.save(docFile);

		DocumentFile target = null;
		if (hasAnother) {
			// 有需要被覆盖的文件
			another.setAnother(docFile);
			another.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_DISABLED);
			another.setReplaced(SystemUtils.YES); // 此文件已经被覆盖
			documentFileDao.update(another);
			target = another;
		} else if (hasFile) {
			// 此资料为差缺，如果差缺的资料覆盖了其他文件，则被覆盖的资料其replaced=0，这里需要更改为1
			// 查找被docFile覆盖的文件置为已替换.这里主要是用于承保上传资料被审核不通过后的重新上传,可能不会更新任何数据
			String hql = "UPDATE " + DocumentFile.class.getName()
					+ " SET replaced = ? WHERE status = ? AND another = ?";
			dao.execute(hql, new Object[] { SystemUtils.YES, SystemUtils.DOCUMENT_FILE_STATUS_DISABLED, docFile });
		}

		return new DocumentFile[] { docFile, target };

	}

	/**
	 * 为doc保存一个新的DocumentFile对象.注意此方法调用之后应当再调用changeDocumentFileStatus(Document, UserSessionEntity)方法更改doc的差缺状态
	 * 
	 * @param doc 要保存数据的Document
	 * @param fileType 资料类型
	 * @param file DocumentFile中要保存的文件对象
	 * @param another 要被覆盖的另一单证,如果不为空,则其fileType应该与参数fileType一致
	 * @return
	 * 
	 * @see #share(DocumentFile, DocumentFile)
	 * @see #share(DocumentFile)
	 * @see #changeDocumentFileStatus(Document, UserSessionEntity)
	 */
	protected DocumentFile saveDocumentFile(final Document doc, final FileType fileType, final File file,
			final DocumentFile another) {

		DocumentFile[] docFiles = saveDocumentFileInternal(doc, fileType, file, another);
		DocumentFile source = docFiles[0];
		DocumentFile target = docFiles[1];
		Document anotherDoc = doc.getAnother(); // doc对应的联合投保单

		if (anotherDoc != null && SystemUtils.YES.equals(fileType.getShared())) {
			// 为联合投保的保单也保存相应的数据,必须是允许共享的资料类型才会共享
			if (target != null && SystemUtils.YES.equals(target.getShared())) {
				// 被覆盖的文件是联合投保共享的文件,则target的
				target = target.getSharedFrom();
			} else {
				target = null;
			}

			docFiles = saveDocumentFileInternal(anotherDoc, fileType, file, target);
			target = docFiles[0];
			// 二者为联合投保共享的文件
			share(source, target);
		}

		// 将文件共享给其他单证
		share(source);

		return source;
	}
	/**
	 * 根据DocumentFile的主键进行审批通过操作
	 * @param ids DocumentFile.id
	 * @param user 操作人
	 * @return 审批通过的File列表
	 */
	public List<DocumentFile> collectDetail(String no, String[] ids, UserSessionEntity user) {
		DocumentFile documentFile = null;
		Document document = null;
		Set<DocumentFile> documentFils = new HashSet<DocumentFile>();
		FileType fileType = null;
		for (String id : ids) {
			documentFile = documentFileDao.get(id);
			if (Helper.isEmpty(documentFile)) {
				//在单份档案查询菜单中，通过新增资料进去
				document = dao.get(no);
				fileType = fileTypeService.get(id);
				if (Helper.isEmpty(fileType)) {
					continue;
				} 
				documentFile = new DocumentFile();
				documentFile.setDocument(document);
				documentFile.setFileModel(SystemUtils.FILE_MODEL_IMAGE);
				documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_LACK);

				documentFile.setFileTypeCode(id);
				documentFile.setFileType(fileType);
			}
			documentFils.add(documentFile);
		}
		return collectDocument(documentFils, user);
	}
	/**
	 * @param businessNo 
	 * @param user
	 * @return
	 */
	public List<DocumentFile> collectDocument(Set<DocumentFile> documentFiles, UserSessionEntity user) {
		File file = null;
		DocumentFile another = null;
		Document anotherDoc = null;
		DocumentFile docFile = null;
		PaperLogDocument paperLogDocument = null;
		FileType fileType = null;
		FileIndex fileIndex = null;
		String companyId = user.getCurrentCompany().getId();
		FileBox fileBox = fileBoxService.getValidFileBox(companyId, "1", 1);
		List<DocumentFile> collectFiles = new ArrayList<DocumentFile>();
		Set<String> visaNos = new HashSet<String>();
		for (DocumentFile dfile : documentFiles) {
			another = dfile.getAnother();
			anotherDoc = dfile.getDocument().getAnother();
			file = dfile.getFile();
			fileType = dfile.getFileType();
			//已经归档了并且有档案盒号和档案编码的
			//if (SystemUtils.DOCUMENT_FILE_STATUS_FILE.equals(dfile.getStatus()) && !"e-Document".equals(file.getNo())) continue;
			if (SystemUtils.DOCUMENT_TYPE_VISA.equals(dfile.getDocument().getType())) {
				//单证类型
				visaNos.add(dfile.getDocument().getNo());
			}
			//if (StringHelper.isEmpty(file)) {
				//未归档的文件
			file = new File();
			file.setFileModel(SystemUtils.FILE_MODEL_FILE); // 核保资料文件
			file.setFileId(StringHelper.randomUUID());
			file.setFileName(dfile.getFileType().getName());
			file.setFileSize(1);
			file.setBatchNo("");
			file.setDocument(dfile.getDocument());
			file.setPageCount(1);
			file.setBatchPageIndex(1);
			file.setLent(SystemUtils.FILE_LENT_NO);
			file.setFileType(fileType);
			file.setUser(user.getInstance());
			file.setDepartment(user.getCurrentDepartment());
			file.setCompany(user.getCurrentCompany());
			file.setInsertTime(new Date());
			file.setFileApproveStatus("3");
			fileBox = fileBoxService.save(fileBox, file);
			docFile = saveDocumentFile(dfile.getDocument(), fileType, file, another);
			
			// 传到FileIndex临时库中
			fileIndex = new FileIndex();
			fileIndex.setSystemCode(SystemUtils.SYSTEM_CODE);
			fileIndex.setId(file.getFileId());
			fileIndex.setFileId(file.getFileId());
			fileIndex.setFileNo(file.getFileId());
			fileIndex.setBusinessNo(dfile.getDocument().getId());
			fileIndex.setBatchNo("");
			fileIndex.setFileTitle(fileType.getName());
			fileIndex.setFileContentType(FileHelper.getContentType(file.getFileName()));
			fileIndex.setFileName(file.getFileName());
			fileIndex.setFileSize(1);
			fileIndex.setFileCount(1);
			fileIndex.setPageCount(1);
			
			fileIndex.setOperator(user.getId());
			fileIndex.setOperateTime(file.getInsertTime());
			
			fileIndex.setProperty00(fileType.getId());
			
			fileIndex.setProperty01(dfile.getFileModel());
			fileIndex.setProperty02(docFile.getId());
			fileIndexService.save(fileIndex);
			/*} else {
				//原来档案资料归档过,手动归档了
				file.setFileApproveStatus("3");
				file.setUser(user.getInstance());
				if ("e-Document".equals(file.getNo())) {
					//如果原来是上传承保资料归档,需要存一个字段表示现在做了手动人工归档,避免做手动人工撤销归档的时候，值回原来状态
					file.setProperty01("e-Document");
				}
				fileBox = fileBoxService.save(fileBox, file);
				docFile = saveDocumentFile(dfile.getDocument(), fileType, file, another);
			}*/
			collectFiles.add(docFile);
			paperLogDocument = new PaperLogDocument();
			paperLogDocument.setNo(dfile.getDocument().getNo());
			paperLogDocument.setId(StringHelper.uuid().replace("-", ""));
			paperLogDocument.setFileType(fileType);
			paperLogDocument.setFileTypeCode(fileType.getCode());
			paperLogDocument.setUser(user.getInstance());
			paperLogDocument.setDepartment(user.getCurrentDepartment());
			paperLogDocument.setCompany(user.getCurrentCompany());
			paperLogDocument.setDueTime(new Date());
			paperLogDocument.setStatus(SystemUtils.DOCUMENT_PAPER_STATUS_1);
			paperLogDocumentService.save(paperLogDocument);
			
			changeDocumentFileStatus(dfile.getDocument(), user);
			if (anotherDoc != null) {
				changeDocumentFileStatus(anotherDoc, user);
			}
		}
		if (!visaNos.isEmpty()) {
			logger.debug("请求的单证号:" + StringHelper.join(visaNos));
			String url = configureDao.getProperty(SystemUtils.SYSTEM_CODE, "visa.resold");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("visaNos", visaNos.toArray());
			parameters.put("type", "04");  //04:单证系统做会销操作  "":空字符串表示会销操作撤销
			String response = HttpClientHelper.get(url, parameters);
			logger.debug("请求的单证号:" + StringHelper.join(visaNos) + ",单证系统返回的数据:" + response);
		}
		return collectFiles;
	}
}
