package com.sinosoft.efiling.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sinosoft.efiling.hibernate.entity.Company;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.DocumentFile;
import com.sinosoft.efiling.hibernate.entity.File;
import com.sinosoft.efiling.hibernate.entity.FileType;
import com.sinosoft.efiling.hibernate.entity.PrpCInsured;
import com.sinosoft.efiling.hibernate.entity.PrpCItemcar;
import com.sinosoft.efiling.hibernate.entity.PrpCMain;
import com.sinosoft.efiling.hibernate.entity.PrpCMainsub;
import com.sinosoft.efiling.hibernate.entity.User;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.filenet.FileEntry;
import com.sinosoft.filenet.FileIndex;
import com.sinosoft.filenet.FileNetConnection;
import com.sinosoft.util.CustomException;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.FileHelper;
import com.sinosoft.util.Helper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;

public class DocumentImportService extends DocumentAuditService {
	// /** 每次批量操作的数量,即每保存10条数据后提交一次数据库 */
	// private static final int BATCH_COUNT = 10;

	/**
	 * 将PDF文件导入到EFiling系统中.导入逻辑：
	 * <ol>
	 * <li>文件名为保单打印的单证号，通过单证号读取保单</li>
	 * <li>每个保单下需要的文件，按照情况，分别处理：
	 * <ul>
	 * <li>1）个人投保新车(初登日期与投保日期间隔在270天内)：仅需要身份证</li>
	 * <li>2）个人投保旧车：需要身份证和行驶证</li>
	 * <li>3）单位投保10000以下：仅需要组织机构代码证</li>
	 * <li>4）单位投保10000以上：需要组织机构代码证、营业执照、税务登记证</li>
	 * </ul>
	 * </li>
	 * <li>自动上传保单的PDF文件</li>
	 * <li>每个资料的附件均指向刚才上传的PDF文件</li>
	 * </ol>
	 * 
	 * @param f 要操作的文件
	 * @param user 操作人
	 */
	@SuppressWarnings("unchecked")
	public Document save(java.io.File f, User user) {
		if (!f.isFile()) return null; // 仅处理文件

		String fileName = f.getName();
		String visaNo = FileHelper.getSimpleFileName(fileName);

		logger.debug("单证号:" + visaNo);

		List<String[]> list = (List<String[]>) dao.querySQL(
				"SELECT V.BUSINESSNO, P.PROPOSALNO FROM VSMARK V INNER JOIN PRPCMAIN P ON P.POLICYNO = V.BUSINESSNO "
						+ "WHERE P.CLASSCODE = ? AND V.VISASERIALNO = ? AND V.VISASTATUS = '04'",
				new String[] { SystemUtils.RISK_CLASS_MOTOR, visaNo });
		if (Helper.isEmpty(list)) {
			logger.warn(visaNo + " 单证号不存在或已失效!");
			throw new CustomException(visaNo + " 单证号不存在或已失效!");
		}
		Object[] values = list.get(0);
		String policyNo = StringHelper.trim(values[0]); // 保单号
		String no = StringHelper.trim(values[1]);// 投保单号
		logger.debug("保单号:" + policyNo + ", 业务号:" + no);

		Document document = getDocument(policyNo);
		if (document != null) {
			logger.info("保单号:" + policyNo + "已经存在!");

			if (visaNo.equals(document.getVisaNo())) return document;

			logger.info("更改保单号:" + policyNo + "的单证号:" + document.getVisaNo() + " --> " + visaNo);
			// document.setVisaNo(visaNo);
			//
			// dao.update(document);
		}

		String fileContentType = FileHelper.getContentType(fileName);
		String fileId = StringHelper.uuid();

		FileEntry entry = new FileEntry();
		entry.setFiles(new java.io.File[] { f });
		entry.setFilesFileName(new String[] { fileName });
		entry.setFilesContentType(new String[] { fileContentType });

		FileIndex fileIndex = new FileIndex();
		fileIndex.setId(fileId);
		fileIndex.setFileId(fileId);
		fileIndex.setFileNo(fileId);
		fileIndex.setFileTitle(no);
		fileIndex.setFileName(fileName);
		fileIndex.setFileContentType(fileContentType);
		fileIndex.setFileSize(f.length());
		fileIndex.setBusinessNo(no);

		fileIndex.setSystemCode(SystemUtils.SYSTEM_CODE);
		fileIndex.setId(fileId);
		fileIndex.setFileId(fileId);
		fileIndex.setFileNo(fileId);
		fileIndex.setBusinessNo(no);
		fileIndex.setFileTitle(fileName);
		fileIndex.setFileName(fileName);
		fileIndex.setFileContentType(fileContentType);
		fileIndex.setFileSize(f.length());
		fileIndex.setFileCount(1);
		fileIndex.setPageCount(1);

		fileIndex.setOperator(user.getId());
		fileIndex.setOperateTime(new Date());

		fileIndex.setProperty00(SystemUtils.FILE_TYPE_APPLICANT);
		fileIndex.setProperty01(SystemUtils.FILE_MODEL_FILE);

		entry.setFileIndex(fileIndex);

		File file = new File();
		file.setFileModel(SystemUtils.FILE_MODEL_FILE);
		file.setNo(SystemUtils.FILE_NO_IMAGE);
		file.setFileId(fileId);
		file.setFileName(no);
		file.setFileSize((int) f.length());
		file.setPageCount(1);
		file.setLent(SystemUtils.FILE_LENT_NO);
		// file.setFileType(SystemUtils.FILE_TYPE_APPLICANT);
		// file.setEffectiveTime(docFile.getEffectiveTime());
		// file.setDueTime(docFile.getDueTime());
		// file.setPaperCode(docFile.getPaperCode());
		file.setUser(user);
		file.setDepartment(user.getDepartment());
		file.setCompany(CompanyService.getCompany(user.getDepartment()));

		file.setInsertTime(new Date());

		if (document == null) {
			// 新增保单数据
			document = savePolicy(policyNo, file);
		} else {
			// 重新上传文件
			String fileTypeId = SystemUtils.FILE_TYPE_PROPOSAL;
			FileType fileType = fileTypeDao.get(fileTypeId);

			file.setDocument(document);
			file.setFileType(fileType);
			fileService.save(file);

			// 更改保单对应的文件
			String hql = "UPDATE " + DocumentFile.class.getName() + " SET file = ? WHERE document = ?";
			dao.execute(hql, new Object[] { file, document });
		}
		if (document != null) {
			document.setVisaNo(visaNo);
			dao.update(document);
			// documents.add(document);

			// 上传文件至FileNet
			FileNetConnection con = new FileNetConnection();
			con.save(entry);
			con.close();

			// 保存FileIndex
			fileIndexService.save(fileIndex);

			// 共享
			String hql = "FROM " + DocumentFile.class.getName() + " WHERE document = ?";
			List<DocumentFile> files = (List<DocumentFile>) dao.query(hql, new Object[] { document });
			for (DocumentFile docFile : files) {
				this.share(docFile);
			}
			logger.debug("保存成功:document.id=" + document.getId() + ", no=" + document.getNo());
		} else {
			logger.debug("保存失败,保单号:" + policyNo + ", 业务号:" + no);
		}
		return document;
	}

	// 组装保存保单的方法
	@SuppressWarnings("unchecked")
	public Document savePolicy(String policyNo, File file) {
		PrpCMain prpCMain = prpCMainDao.get(policyNo);

		if (prpCMain == null) {
			// 此保单数据在PrpCMain中没找到，可能是大保单，不进行处理，暂时仅处理车险，
			return null;
		}

		String proposalNo = prpCMain.getProposalno(); // 投保单号

		// PrpCMain prpCMain = prpCMainDao.get(proposalNo); // 投保单

		Document document = new Document();
		document.setId(proposalNo);
		document.setNo(policyNo);
		document.setProposalNo(proposalNo);
		document.setPolicyNo(policyNo);

		document.setStatus(SystemUtils.STATUS_VALID);
		document.setType(SystemUtils.DOCUMENT_TYPE_POLICY);

		document.setFileStatus(SystemUtils.DOCUMENT_STATUS_FILE);
		// 存差缺文字
		document.setLacks(null);
		document.setLent(SystemUtils.FILE_LENT_NO);

		// 保单来源 第一位0/1--内网/外网；第二位1--批量投保；第3-4位 01-电子商务 02-意时网 03-蒙代尔
		// 04-网站;第10为1--货运险电子申报单
		// String resouse = prpCMain.getPolisource();
		String resouse = StringHelper.trim(prpCMain.getPolisource());
		if (resouse.length() >= 4 && resouse.substring(2, 4).equals("01")) {
			// 电子商务
			document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
		} else {
			// 核心系统
			document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
		}
		document.setRiskClass(prpCMain.getClassCode());
		document.setRiskCode(prpCMain.getRiskCode());
		document.setRiskType(
				SystemUtils.RISK_CLASS_MOTOR.equals(prpCMain.getClassCode()) ? SystemUtils.RISK_TYPE_AUTO : SystemUtils.RISK_TYPE_NON_AUTO);
		// 产品线
		document.setProduct(getProduct(prpCMain.getRiskCode()));

		// 投保人类型必须从Prptinsured表查出来
		List<PrpCInsured> PrpCInsuredList = (List<PrpCInsured>) this.queryInsureds(prpCMain.getId(), SystemUtils.DOCUMENT_TYPE_POLICY,
				SystemUtils.CLIENT_RELATION_APPLICANT);
		PrpCInsured prpCInsured = null;
		if (!Helper.isEmpty(PrpCInsuredList)) {
			prpCInsured = PrpCInsuredList.get(0);
			document.setApplicantType(prpCInsured.getInsuredType());
			// 证件类型
			document.setApplicantPassportType(prpCInsured.getIdentifyType());
			// 证件号码
			document.setApplicantPassportNo(prpCInsured.getIdentifyNumber());
		}
		if (StringHelper.isEmpty(document.getApplicantPassportNo())) {
			logger.debug(policyNo + "投保人证件号为空!");
		}
		document.setApplicant(prpCMain.getAppliName());

		// 代理人编码
		document.setAgentNo(prpCMain.getAgentCode());
		document.setAgentName(prpCMain.getAgentName());
		// 出单员
		User user = userDao.get(prpCMain.getOperatorCode());
		document.setSales(user);
		document.setSalesTime(prpCMain.getOperateDate());
		// 业务关系代码
		document.setBusinessNo(prpCMain.getAgreementNo());
		// 承保年度
		document.setYear(DateHelper.getYearByDate(prpCMain.getOperateDate()));
		// 内部业务员
		user = userDao.get(prpCMain.getHandler1Code());
		document.setBusinessUser(user);
		// 业务部门
		Company dept = companyDao.get(prpCMain.getComCode());
		document.setBusinessDept(dept);
		Company company = CompanyService.getCompany(dept);
		document.setBusinessCompany(company);
		document.setEffectiveTime(
				DateHelper.set(prpCMain.getStartDate(), Calendar.HOUR_OF_DAY, NumberHelper.intValue(prpCMain.getStarthour())));
		document.setDueTime(DateHelper.set(prpCMain.getEndDate(), Calendar.HOUR_OF_DAY, NumberHelper.intValue(prpCMain.getEndhour())));

		// 保存被保险人,有可能有多个被保险人
		List<PrpCInsured> prpCinsureds = (List<PrpCInsured>) this.queryInsureds(prpCMain.getId(), SystemUtils.DOCUMENT_TYPE_PROPOSAL,
				SystemUtils.CLIENT_RELATION_INSURED);
		String insuredNames = getInusuredNames(prpCinsureds, SystemUtils.DOCUMENT_TYPE_PROPOSAL);
		document.setInsured(insuredNames);

		String riskType = prpCMain.getRiskCode().trim();
		PrpCItemcar prpCItemcar = null; // 车辆数据
		// 如果是联合投保,组装另外一个投保单号的Document对象,联合投保传进来的都是商业险(0501)的单子,交强险单子(0508)会跟着改变状态
		if (SystemUtils.RISK_CODE_MOTOR_0501.equals(riskType) || SystemUtils.RISK_CODE_MOTOR_0508.equals(riskType)) {
			// 判断是否为联合投保商业险、交强险时
			List<PrpCMainsub> list = prpCMainsubDao.queryByProperty(new Object[][] { { "id.policyno", policyNo } });
			PrpCMainsub prpCMainsub = null;
			String combineFlag = null;
			if (!Helper.isEmpty(list)) {
				prpCMainsub = list.get(0);
				combineFlag = prpCMainsub.getCombineFlag();
			}
			// combineFlag:COMBINE 联合投保 MOTOR 商业险 MTPL 交强险
			if (SystemUtils.COMBINEFLAG_COMBINE.equals(combineFlag)) {
				// 联合投保 得到另外一个投保单号
				// anotherProposalNo = prpCMainsub.getMainproposalno();
			}
			List<PrpCItemcar> prpCItemcars = prpCItemcarDao.queryByProperty(new Object[][] { { "id.policyno", prpCMain.getId() } });
			if (!Helper.isEmpty(prpCItemcars)) prpCItemcar = prpCItemcars.get(0);
			if (prpCItemcar != null) {
				// 设置发动机号和车牌号车架号
				document.setVin(prpCItemcar.getVinno());
				document.setEngineNo(prpCItemcar.getEngineno());
				document.setLicenseNo(prpCItemcar.getLicenseno());
			}
		}
		// document.setCreateTime(new Date());
		document.setUpdateTime(new Date());
		dao.save(document);

		file.setDocument(document);

		// 保存证件

		// 保存投保单
		String fileTypeId = SystemUtils.FILE_TYPE_PROPOSAL;
		FileType fileType = fileTypeDao.get(fileTypeId);

		file.setDocument(document);
		file.setFileType(fileType);
		fileService.save(file);

		DocumentFile documentFile = new DocumentFile();
		documentFile.setFile(file);
		documentFile.setId(StringHelper.randomUUID());
		documentFile.setDocument(document);
		documentFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
		documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_FILE);
		documentFile.setFileTypeCode(fileType.getCode());
		documentFile.setFileType(fileType);
		documentFile.setRequired(SystemUtils.YES);
		documentFile.setFileTime(new Date());

		documentFileDao.save(documentFile);

		// 保存身份证或组织机构代码证
		fileTypeId = SystemUtils.FILE_TYPE_APPLICANT;
		fileType = fileTypeDao.get(fileTypeId);
		documentFile = new DocumentFile();
		documentFile.setFile(file);
		documentFile.setId(StringHelper.randomUUID());
		documentFile.setDocument(document);
		documentFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
		documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_FILE);
		documentFile.setFileTypeCode(fileType.getCode());
		documentFile.setFileType(fileType);
		documentFile.setRequired(SystemUtils.YES);
		documentFile.setFileTime(new Date());
		Date dueTime = null;
		if ("1".equalsIgnoreCase(document.getApplicantType())) {
			// 个人
			dueTime = (Date) dao.uniqueResultSQL("SELECT IDINVALIDDATE FROM PRPDCUSTOMERIDV WHERE CUSTOMERCODE = ? AND ROWNUM = 1",
					new Object[] { document.getApplicantPassportNo() });
		} else {// else if ("2".equals(customerType)) {
				// 单位
			dueTime = (Date) dao.uniqueResultSQL("SELECT ORGINVALIDDATE FROM PRPDCUSTOMERUNIT WHERE CUSTOMERCODE = ? AND ROWNUM = 1",
					new Object[] { document.getApplicantPassportNo() });
		}

		if (dueTime == null) {
			// 默认为2024-12-31日
			dueTime = DEFAULT_MAX_DUE_TIME;
		}
		documentFile.setDueTime(dueTime);
		documentFile.setPaperType(document.getApplicantPassportType());
		documentFile.setPaperCode(document.getApplicantPassportNo());
		documentFileDao.save(documentFile);

		if (prpCItemcar == null) return document;
		// 个人需要行驶证
		if ("1".equalsIgnoreCase(document.getApplicantType())) {
			// 判断是否为新车,(初登日期与投保日期间隔在270天内的为新车)
			/* 1）个人投保新车(初登日期与投保日期间隔在270天内)：仅需要身份证 */
			if (270 >= DateHelper.getDays(prpCMain.getInputDate(), prpCItemcar.getEnrollDate())) return document;
			/* 2）个人投保旧车：需要身份证和行驶证 */
			fileTypeId = SystemUtils.FILE_TYPE_VEHICLE_LICENSE;
			fileType = fileTypeDao.get(fileTypeId);
			documentFile = new DocumentFile();
			documentFile.setFile(file);
			documentFile.setId(StringHelper.randomUUID());
			documentFile.setDocument(document);
			documentFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
			documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_FILE);
			documentFile.setFileTypeCode(fileType.getCode());
			documentFile.setFileType(fileType);
			documentFile.setRequired(SystemUtils.YES);
			documentFile.setFileTime(new Date());
			// String userNature = prpCItemcar.getUsenatureCode();
			// Date enrolldate = DateHelper.clear(prpCItemcar.getEnrollDate());
			//
			// String vehicleCodeSQL =
			// "SELECT VEHICLECATEGORYCODE FROM PRPCITEMCAREXT WHERE policyNo = ? AND ITEMNO = ?";
			// String vehicleType = (String) dao.uniqueResultSQL(vehicleCodeSQL, new Object[] { policyNo,
			// prpCItemcar.getId().getItemno() });
			// dueTime = DocumentAuditHelper.getDueDate(userNature, vehicleType, enrolldate);
			documentFile.setDueTime(DEFAULT_DRIVING_MAX_TIME);
			documentFile.setPaperType(document.getVin());
			documentFile.setPaperCode(document.getEngineNo());

			documentFileDao.save(documentFile);
		} else {
			/* 3）单位投保10000以下(如果是联合投保,则是商业+交强险)：仅需要组织机构代码证 */
			String sql = "SELECT MAINPOLICYNO FROM prpCMainsub WHERE POLICYNO = ? AND COMBINEFLAG = 'COMBINE'";
			String anotherPolicyNo = (String) dao.uniqueResultSQL(sql, new Object[] { policyNo });
			double premium = prpCMain.getSumpremium();
			if (!StringHelper.isEmpty(anotherPolicyNo)) {
				// 如果是联合投保,则是premium=商业+交强险
				PrpCMain anotherPrpCMain = prpCMainDao.get(anotherPolicyNo);
				premium += anotherPrpCMain.getSumpremium();
			}
			if (10000 >= premium) return document;

			// 增加280资料类型后代码已不适用2017-02-16
			/* 4）单位投保10000以上：需要组织机构代码证、营业执照、税务登记证 */
			// 单位营业执照、税务登记证
			// fileTypeId = SystemUtils.FILE_TYPE_LICENSE_BUSINESS;
			// fileType = fileTypeDao.get(fileTypeId);
			// documentFile = new DocumentFile();
			// documentFile.setFile(file);
			// documentFile.setId(StringHelper.randomUUID());
			// documentFile.setDocument(document);
			// documentFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
			// documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_FILE);
			// documentFile.setFileTypeCode(fileType.getCode());
			// documentFile.setFileType(fileType);
			// documentFile.setRequired(SystemUtils.YES);
			// documentFile.setFileTime(new Date());
			//
			// documentFile.setDueTime(dueTime);
			// documentFile.setPaperType(document.getApplicantPassportType());
			// documentFile.setPaperCode(document.getApplicantPassportNo());
			//
			// documentFileDao.save(documentFile);
			//
			// fileTypeId = SystemUtils.FILE_TYPE_LICENSE_TAX;
			// fileType = fileTypeDao.get(fileTypeId);
			// documentFile = new DocumentFile();
			// documentFile.setFile(file);
			// documentFile.setId(StringHelper.randomUUID());
			// documentFile.setDocument(document);
			// documentFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
			// documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_FILE);
			// documentFile.setFileTypeCode(fileType.getCode());
			// documentFile.setFileType(fileType);
			// documentFile.setRequired(SystemUtils.YES);
			// documentFile.setFileTime(new Date());
			//
			// documentFile.setDueTime(dueTime);
			// documentFile.setPaperType(document.getApplicantPassportType());
			// documentFile.setPaperCode(document.getApplicantPassportNo());
			//
			// documentFileDao.save(documentFile);
		}

		return document;
	}
}
