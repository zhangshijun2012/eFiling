package com.sinosoft.efiling.service;

import java.util.HashSet;
import java.util.Set;

import com.sinosoft.efiling.hibernate.dao.DocumentDao;
import com.sinosoft.efiling.hibernate.dao.DocumentFileDao;
import com.sinosoft.efiling.hibernate.dao.FileDao;
import com.sinosoft.efiling.hibernate.dao.FileLendingDao;
import com.sinosoft.efiling.hibernate.dao.FileLendingDetailDao;
import com.sinosoft.efiling.hibernate.dao.FileLendingReturnDao;
import com.sinosoft.efiling.hibernate.dao.ProductDao;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.DocumentFile;
import com.sinosoft.efiling.hibernate.entity.File;
import com.sinosoft.efiling.hibernate.entity.FileLending;
import com.sinosoft.efiling.hibernate.entity.FileLendingDetail;
import com.sinosoft.efiling.hibernate.entity.FileLendingDetailId;
import com.sinosoft.efiling.hibernate.entity.FileLendingReturn;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.service.ServiceSupport;

public class FileLendingService extends ServiceSupport<FileLending, FileLendingDao> {
	ProductDao productDao;
	FileLendingDetailDao fileLendingDetailDao;
	FileLendingReturnDao fileLendingReturnDao;
	FileDao fileDao;
	DocumentFileDao documentFileDao;
	DocumentDao documentDao;

	public FileDao getFileDao() {
		return fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		this.fileDao = fileDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public DocumentFileDao getDocumentFileDao() {
		return documentFileDao;
	}

	public void setDocumentFileDao(DocumentFileDao documentFileDao) {
		this.documentFileDao = documentFileDao;
	}

	public FileLendingDetailDao getFileLendingDetailDao() {
		return fileLendingDetailDao;
	}

	public void setFileLendingDetailDao(FileLendingDetailDao fileLendingDetailDao) {
		this.fileLendingDetailDao = fileLendingDetailDao;
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	public FileLendingReturnDao getFileLendingReturnDao() {
		return fileLendingReturnDao;
	}

	public void setFileLendingReturnDao(FileLendingReturnDao fileLendingReturnDao) {
		this.fileLendingReturnDao = fileLendingReturnDao;
	}

	/**
	 * 
	 * @param fileLending
	 *            表示FileLending对象
	 * @param ids
	 *            传入的是T_F_DOCUMENT_FILE的资料主键 return
	 */
	public void save(FileLending fileLending, String[] ids) {
		save(fileLending);
		FileLendingDetail fileLendingDetail;
		FileLendingDetailId fileLendingDetailId;
		Document document;
		DocumentFile documentFile;
		File file;
		for (int i = 0; i < ids.length; i++) {
			documentFile = documentFileDao.get(ids[i]);
			// 保存借阅资料明细
			fileLendingDetail = new FileLendingDetail();
			file = documentFile.getFile();

			fileLendingDetailId = new FileLendingDetailId(fileLending.getId(), file.getId());
			if (fileLendingDetailDao.get(fileLendingDetailId) != null) continue; // 同一个附件已经借出

			if (SystemUtils.FILE_LENT_YES.equals(file.getLent())) {
				// 已被借出
				throw new RuntimeException("档案" + file.getFileType().getName() + "(" + file.getNo() + ")已被借出!");
			}

			fileLendingDetail.setId(fileLendingDetailId);
			fileLendingDetail.setDocumentFile(documentFile);
			fileLendingDetail.setStatus(SystemUtils.FILE_LENT_YES);
			fileLendingDetailDao.save(fileLendingDetail);
			// 改变T_F_FILE表LENT字段的状态。表示已被借出
			// file = documentFileDao.get(ids[i]).getFile();
			// if (Helper.isEmpty(file)) throw new RuntimeException("未归档的档案资料不允许借阅操作！");
			file.setLent(SystemUtils.FILE_LENT_YES);
			file.setFileLending(fileLending);
			fileDao.update(file);
			// 改变T_F_DOCUMENT表的LENT字段的状态，表示此单证已被借阅过
			document = documentFileDao.get(ids[i]).getDocument();
			document.setLent(SystemUtils.FILE_LENT_YES);
			document.setFileLending(fileLending);
			documentDao.update(document);
		}
	}

	/**
	 * 判断某单证的所有资料是否已经归还完
	 * 
	 * @param document
	 *            单证
	 * @return
	 */
	/*
	 * public boolean haveReturnAllfile(Document document) {
	 * for (DocumentFile documentFile : document.getDocumentFiles()) {
	 * // 判断某个单证已归档的或者禁用(是指核保资料需要的,在上传附件时,把以前的核保资料给禁用了)的档案资料是否归还完,针对未归档的档案资料
	 * if (!SystemUtils.DOCUMENT_FILE_STATUS_LACK.equals(documentFile.getStatus())
	 * && SystemUtils.FILE_LENT_YES.equals(documentFile.getFile().getLent())
	 * && documentFile.getFile().getNo() != null)
	 * return false;
	 * }
	 * return true;
	 * }
	 */
	/**
	 * 判断某单证的所有资料是否被借阅
	 * 
	 * @param document
	 *            单证
	 * @return
	 */
	/*
	 * public boolean haveLentAllfile(Document document) {
	 * for (DocumentFile documentFile : document.getDocumentFiles()) {
	 * if (!SystemUtils.DOCUMENT_FILE_STATUS_LACK.equals(documentFile.getStatus())
	 * && SystemUtils.FILE_LENT_NO.equals(documentFile.getFile().getLent())
	 * && documentFile.getFile().getNo() != null)
	 * return false;
	 * }
	 * return true;
	 * }
	 */

	/**
	 * 
	 * @param fileLendingReturn
	 *            保存档案资料归还信息
	 * @param ids
	 *            通过传入T_F_FILE_LENDING_DETAIL主键，档案资料现在的状态 return
	 */
	public void save(FileLendingReturn fileLendingReturn, String[] ids) {

		// 保存档案资料归还信息
		fileLendingReturnDao.save(fileLendingReturn);
		DocumentFile documentFile;
		// Document document;
		File file;
		FileLendingDetail fileLendingDetail;
		FileLendingDetailId fileLendingDetailId;
		FileLending fileLending;
		Set<DocumentFile> documentFiles = null;
		Set<Document> documents = new HashSet<Document>();
		Set<FileLending> fileLendings = new HashSet<FileLending>();

		for (int i = 0, l = ids.length; i < l; i++) {
			documentFile = documentFileDao.get(ids[i]);
			// 改变T_F_FILE表LENT字段的状态。表示已归还
			file = documentFile.getFile();
			// if (Helper.isEmpty(file)) throw new RuntimeException("未归档的档案资料不能够归还");
			file.setLent(SystemUtils.FILE_LENT_NO);
			fileDao.update(file);
			documentFiles = file.getDocumentFiles();
			for (DocumentFile dfile : documentFiles) {
				documents.add(dfile.getDocument());
			}
			/*
			 * 如果单证所有的资料全部归还，需要改T_F_DOCUMENT表lent字段为0 document = documentFile.getDocument(); if
			 * (haveReturnAllfile(document)) { document.setLent(SystemUtils.FILE_LENT_NO); documentDao.update(document);
			 * }
			 */
			// 归还档案资料时，改变借阅表中的信息
			fileLending = file.getFileLending();
			fileLendings.add(fileLending);
			// fileLending.setReturnDate(fileLendingReturn.getInsertTime());
			/*
			 * if (haveReturnAllfile(document)) { // 全部归还
			 * fileLending.setStatus(SystemUtils.FILE_LENDING_STATUS_RETURNED); } else if (!haveReturnAllfile(document)
			 * && !haveLentAllfile(document)) { // 部分归还
			 * fileLending.setStatus(SystemUtils.FILE_LENDING_STATUS_RETURN_PART); } else { // 借阅中
			 * fileLending.setStatus(SystemUtils.FILE_LENDING_STATUS_LENDING); }
			 */
			// dao.update(fileLending);
			fileLendingDetailId = new FileLendingDetailId(fileLending.getId(), file.getId());
			fileLendingDetail = fileLendingDetailDao.get(fileLendingDetailId);
			fileLendingDetail.setStatus(SystemUtils.FILE_LENT_NO);
			fileLendingDetail.setFileLendingReturn(fileLendingReturn);
			fileLendingDetailDao.update(fileLendingDetail);
		}

		StringBuffer hql = new StringBuffer();
		// hql.append("SELECT * FROM T_F_DOCUMENT_FILE T ");
		// hql.append(" INNER JOIN T_F_FILE TT ON T.FILE_ID = TT.ID");
		// hql.append(" WHERE TT.LENT=? AND T.DOCUMENT_ID=?");

		hql.append("SELECT COUNT(*) FROM ");
		hql.append(DocumentFile.class.getName()).append(" f WHERE ");
		hql.append("f.file.lent = ? AND f.document = ?");
		// List<Object[]> o = null;
		int count;
		for (Document document : documents) {
			// o = (List<Object []>) dao.querySQL(hql.toString(), new Object[] {SystemUtils.FILE_LENT_YES,
			// document.getId()});
			count = NumberHelper.intValue(dao.uniqueResult(hql.toString(), new Object[] { SystemUtils.FILE_LENT_YES,
					document }));
			// 表示归还完了
			if (count <= 0) {
				document.setLent(SystemUtils.FILE_LENT_NO);
				documentDao.update(document);
			}
		}
		hql = new StringBuffer();
		// hql.append("SELECT * FROM T_F_FILE_LENDING T ");
		// hql.append(" INNER JOIN T_F_FILE_LENDING_DETAIL TT");
		// hql.append(" ON T.ID = TT.LENDING_ID ");
		// hql.append(" WHERE TT.LENDING_ID = ? AND TT.STATUS = ?");

		hql.append("SELECT COUNT(*) FROM ");
		hql.append(FileLendingDetail.class.getName()).append(" f WHERE ");
		hql.append("f.status = ? AND f.fileLending = ?");

		// List<Object[]> objects = null;
		for (FileLending lending : fileLendings) {
			// objects = (List<Object[]>) dao.querySQL(hql.toString(), new Object[] { lending.getId(),
			// SystemUtils.FILE_LENT_YES });
			count = NumberHelper.intValue(dao.uniqueResult(hql.toString(), new Object[] { SystemUtils.FILE_LENT_YES,
					lending }));
			if (count <= 0) {
				// 已经归还完了
				lending.setStatus(SystemUtils.FILE_LENDING_STATUS_RETURNED);
			} else {
				// 部分归还了
				lending.setStatus(SystemUtils.FILE_LENDING_STATUS_RETURN_PART);
			}
			lending.setFileLendingReturn(fileLendingReturn);
			lending.setReturnDate(fileLendingReturn.getInsertTime());
			dao.update(lending);
		}
	}

}
