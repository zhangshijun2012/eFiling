package com.sinosoft.efiling.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.DocumentFile;
import com.sinosoft.efiling.hibernate.entity.File;
import com.sinosoft.efiling.hibernate.entity.FileType;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.efiling.util.UserSessionEntity;
import com.sinosoft.filenet.FileIndex;
import com.sinosoft.util.Helper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.json.JSONArray;
import com.sinosoft.util.json.JSONObject;

public class FileApproveService extends DocumentService {

	/**
	 * 根据XML字符串保存数据.主要用于需要审核的承保资料上传。对于上传的资料默认为待审核. XML中可能存在飞承保资料的文件
	 * 
	 * @param xmlString
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject save(String xmlString, UserSessionEntity user) {
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
			for (Element doc : docs) {
				documentNo = doc.attributeValue("id");
				entity = getDocument(documentNo);
				anotherDoc = entity.getAnother();
				List<Element> files = doc.elements("file");

				for (Element fileElement : files) {
					count = NumberHelper.intValue(fileElement.elementText("count"));
					// 资料类型的代码
					String fileTypeId = fileElement.attributeValue("type");
					FileType fileType = fileTypeService.get(fileTypeId);

					/* 处理影像文件 */
					if (SystemUtils.FILE_MODEL_IMAGE.equals(fileType.getFileModel())) {
						/* 没有联合投保，则对影像文件不处理 */
						if (anotherDoc == null) continue;
						/* 不允许共享的单证 */
						if (!SystemUtils.YES.equals(fileType.getShared())) continue;

						String fileId = fileElement.elementText("id");
						FileIndex fileIndex = new FileIndex();
						fileIndex.setBusinessNo(anotherDoc.getId());
						fileIndexService.copy(fileId, fileIndex);
						continue;
					}

					/* 处理核保资料 */
					String fileId = fileElement.elementText("id");
					String fileName = fileElement.elementText("name");
					int fileSize = NumberHelper.intValue(fileElement.elementText("size"));
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
					file.setFileApproveStatus(SystemUtils.FILE_APPROVE_STATUS_UNAUDITED); // 待审核
					fileService.save(file);

					DocumentFile docFile = saveDocumentFile(entity, fileType, file, null);

					JSONObject docFileJSON = new JSONObject(docFile);
					docFileJSON.put("fileType", docFile.getFileType());
					docFileJSON.put("file", docFile.getFile());
					docFiles.put(docFileJSON);
				}

				if (!SystemUtils.FILE_APPROVE_STATUS_NOPASSED.equals(entity.getFileApproveStatus())) {
					// 有新上传的资料,则只要此单证状态不为审核不通过就标记为待审核
					entity.setFileApproveStatus(SystemUtils.FILE_APPROVE_STATUS_UNAUDITED);
				}
				// 改变单证的状态
				changeFileApproveStatus(entity);
				changeDocumentFileStatus(entity, user);
				if (anotherDoc != null) {
					if (!SystemUtils.FILE_APPROVE_STATUS_NOPASSED.equals(anotherDoc.getFileApproveStatus())) {
						anotherDoc.setFileApproveStatus(SystemUtils.FILE_APPROVE_STATUS_UNAUDITED);
					}
					changeDocumentFileStatus(anotherDoc, user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.put("success", false);
			json.put("message", e.getMessage());
		}

		return json;
	}

	/**
	 * 根据Document的主键进行审批通过操作
	 * 
	 * @param ids
	 *            Document.id
	 * @param user
	 *            操作人
	 * @return 审批通过的File列表
	 */
	public List<File> approve(String[] ids, UserSessionEntity user) {
		List<File> files = this.queryUnapprovedFilesByDocumentId(ids);
		return this.handle(files, SystemUtils.FILE_APPROVE_STATUS_AUDITED, user);
	}

	/**
	 * 根据DocumentFile的主键进行审批通过操作
	 * 
	 * @param ids
	 *            DocumentFile.id
	 * @param user
	 *            操作人
	 * @return 审批通过的File列表
	 */
	public List<File> approveDetail(String[] ids, UserSessionEntity user) {
		List<File> files = this.queryUnapprovedFilesByDocumentFileId(ids);
		return this.handle(files, SystemUtils.FILE_APPROVE_STATUS_AUDITED, user);
	}

	/**
	 * 根据Document的主键进行审批不通过操作
	 * 
	 * @param ids
	 *            Document.id
	 * @param user
	 *            操作人
	 * @return 审批不通过的File列表
	 */
	public List<File> decline(String[] ids, UserSessionEntity user) {
		List<File> files = this.queryUnapprovedFilesByDocumentId(ids);
		return this.handle(files, SystemUtils.FILE_APPROVE_STATUS_NOPASSED, user);
	}

	/**
	 * 根据DocumentFile的主键进行审批不通过操作
	 * 
	 * @param ids
	 *            DocumentFile.id
	 * @param user
	 *            操作人
	 * @return 审批不通过的File列表
	 */
	public List<File> declineDetail(String[] ids, UserSessionEntity user) {
		List<File> files = this.queryUnapprovedFilesByDocumentFileId(ids);
		return this.handle(files, SystemUtils.FILE_APPROVE_STATUS_NOPASSED, user);
	}

	/**
	 * 根据主键id返回所有的DocumentFile
	 * 
	 * @param ids
	 *            主键id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<File> queryUnapprovedFilesByDocumentFileId(String[] ids) {
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT d.file FROM ").append(DocumentFile.class.getName()).append(" d ");
		hql.append(" WHERE d.status = ?");
		hql.append(" AND d.file.fileApproveStatus = ?");
		hql.append(" AND d.id in (? ").append(StringHelper.copy(",? ", (ids.length - 1))).append(")");
		Object[] parameters = new Object[ids.length + 2];
		parameters[0] = SystemUtils.DOCUMENT_FILE_STATUS_FILE;
		parameters[1] = SystemUtils.FILE_APPROVE_STATUS_UNAUDITED;
		System.arraycopy(ids, 0, parameters, 2, ids.length);
		return (List<File>) documentFileDao.query(hql.toString(), parameters);
	}

	/**
	 * 查询承保资料上传未审核的资料
	 * 
	 * @param ids
	 *            document.id
	 */
	@SuppressWarnings("unchecked")
	public List<File> queryUnapprovedFilesByDocumentId(String[] ids) {
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT d.file FROM ").append(DocumentFile.class.getName()).append(" d ");
		hql.append(" WHERE d.status = ?");
		hql.append(" AND d.file.fileApproveStatus = ?");
		hql.append(" AND d.document.id in (? ").append(StringHelper.copy(",? ", (ids.length - 1))).append(")");
		Object[] parameters = new Object[ids.length + 2];
		parameters[0] = SystemUtils.DOCUMENT_FILE_STATUS_FILE;
		parameters[1] = SystemUtils.FILE_APPROVE_STATUS_UNAUDITED;
		System.arraycopy(ids, 0, parameters, 2, ids.length);
		return (List<File>) documentFileDao.query(hql.toString(), parameters);
	}

	/**
	 * 处理文件的审核通过/不通过状态
	 * 
	 * @param files
	 *            要处理的文件
	 * @param fileApproveStatus
	 *            审核状态
	 * @param user
	 *            操作人
	 * @return files
	 */
	private List<File> handle(List<File> files, String fileApproveStatus, UserSessionEntity user) {
		for (File f : files) {
			f.setFileApproveStatus(fileApproveStatus);
			fileService.update(f);
			Set<DocumentFile> documentFiles = f.getDocumentFiles();
			handleDocumentFiles(documentFiles, fileApproveStatus, user);
		}
		return files;
	}

	/**
	 * 处理DocumentFile列表的审核通过/不通过状态
	 * 
	 * @param documentFiles
	 *            通过File.getDocumentFiles()得到的DocumentFile列表
	 * @param fileApproveStatus
	 *            审核状态
	 * @param user
	 *            操作人
	 * @return
	 * @see #handle(List, String, UserSessionEntity)
	 * @see File#getDocumentFiles()
	 */
	private Collection<DocumentFile> handleDocumentFiles(Collection<DocumentFile> documentFiles,
			String fileApproveStatus, UserSessionEntity user) {
		Set<Document> documents = new HashSet<Document>();
		for (DocumentFile documentFile : documentFiles) {
			if (SystemUtils.FILE_APPROVE_STATUS_NOPASSED.equals(fileApproveStatus)) {
				// 审核不通过
				disableDocumentFile(documentFile, user);
			}
			documents.add(documentFile.getDocument());
		}

		for (Document doc : documents) {
			// 更改document的审核状态
			changeFileApproveStatus(doc);
		}

		return documentFiles;
	}

	/**
	 * 当前状态, 规则: 1.只要有待审核的资料,保单下的状态就是待审核 2.只要在没有待审核的资料，只要有审核不通过的资料，那么保单下面的单子就是审核不通过 3.保单下面的所有资料审核通过,就是审核通过
	 * 
	 * @param entity
	 *            要处理的document对象
	 */
	public void changeFileApproveStatus(Document entity) {
		StringBuffer hql = new StringBuffer();
		// 查询是不是有待审核的资料,只要有待审核的资料,单子的状态就是待审核
		hql.append(" SELECT COUNT(*) FROM ").append(DocumentFile.class.getName()).append(" d ");
		// hql.append(" INNER JOIN d.file").append(" f ");
		hql.append(" WHERE d.document = ? AND d.replaced = ? AND d.file.fileApproveStatus = ? ");
		int start = hql.length();
		hql.append(" AND d.status = ? ");
		int count = NumberHelper.intValue((documentFileDao.uniqueResult(hql.toString(), new Object[] { entity,
				SystemUtils.NO, SystemUtils.FILE_APPROVE_STATUS_UNAUDITED, SystemUtils.DOCUMENT_FILE_STATUS_FILE })));
		if (count > 0) {
			// 待审核
			entity.setFileApproveStatus(SystemUtils.FILE_APPROVE_STATUS_UNAUDITED);
		} else {
			// int start = hql.length();
			hql.delete(start, hql.length());
			count = NumberHelper.intValue(documentFileDao.uniqueResult(hql.toString(), new Object[] { entity,
					SystemUtils.NO, SystemUtils.FILE_APPROVE_STATUS_NOPASSED }));
			if (count > 0) {
				// 审核不通过
				entity.setFileApproveStatus(SystemUtils.FILE_APPROVE_STATUS_NOPASSED);
			} else {
				// 判断是否审核通过
				count = NumberHelper.intValue(documentFileDao.uniqueResult(hql.toString(), new Object[] { entity,
						SystemUtils.NO, SystemUtils.FILE_APPROVE_STATUS_AUDITED }));
				if (count > 0) {
					// 审核通过
					entity.setFileApproveStatus(SystemUtils.FILE_APPROVE_STATUS_AUDITED);
				} else {
					// 没有进行过任何操作
					entity.setFileApproveStatus(null);
				}
			}
		}
		dao.update(entity);
	}

	/**
	 * 审核不通过后禁用单证下面的某个资料
	 * 
	 * @param documentFile 需要禁用的DocumentFile对象
	 * @param user 操作人
	 * @return
	 * @see #handleDocumentFiles(Collection, String, UserSessionEntity)
	 */
	private void disableDocumentFile(DocumentFile documentFile, UserSessionEntity user) {
		// DocumentFile documentFile = dFile;// getDocumentFile(document, dFile.getFileType().getId());
		if (documentFile != null && SystemUtils.DOCUMENT_FILE_STATUS_FILE.equals(documentFile.getStatus())) {
			// 已经归档了的相同资料设置为禁用
			documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_DISABLED);
			// 对于上传的承保资料审核不通过
			// documentFile.setFileApproveStatus(SystemUtils.FILE_APPROVE_STATUS_NOPASSED
			if (SystemUtils.YES.equals(documentFile.getRequired())) {
				// 核保需要的资料,新建一个必须上传的空文件数据
				DocumentFile another = new DocumentFile();
				String[] properties = new String[] { "id", "file", "another", "status", "fileTime" };
				Helper.copy(documentFile, another, properties);
				another.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_LACK);
				documentFileDao.save(another);

				documentFile.setAnother(another); // 由another覆盖此文件

				// 核保需要的资料
				changeDocumentFileStatus(documentFile.getDocument(), user);
			}
			documentFileDao.update(documentFile);
		}
	}
}
