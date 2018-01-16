﻿package com.sinosoft.efiling.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sinosoft.efiling.hibernate.dao.FileTypeDao;
import com.sinosoft.efiling.hibernate.dao.ProductDao;
import com.sinosoft.efiling.hibernate.dao.ProductRiskDao;
import com.sinosoft.efiling.hibernate.dao.PrpCInsuredDao;
import com.sinosoft.efiling.hibernate.dao.PrpCItemcarDao;
import com.sinosoft.efiling.hibernate.dao.PrpCMainDao;
import com.sinosoft.efiling.hibernate.dao.PrpCMainsubDao;
import com.sinosoft.efiling.hibernate.dao.PrpDCoderiskDao;
import com.sinosoft.efiling.hibernate.dao.PrpDUwmtypesDao;
import com.sinosoft.efiling.hibernate.dao.PrpPInsuredDao;
import com.sinosoft.efiling.hibernate.dao.PrpPItemcarDao;
import com.sinosoft.efiling.hibernate.dao.PrpPMainDao;
import com.sinosoft.efiling.hibernate.dao.PrpPMainsubDao;
import com.sinosoft.efiling.hibernate.dao.PrpTInsuredDao;
import com.sinosoft.efiling.hibernate.dao.PrpTItemcarDao;
import com.sinosoft.efiling.hibernate.dao.PrpTMainDao;
import com.sinosoft.efiling.hibernate.dao.PrpTMainsubDao;
import com.sinosoft.efiling.hibernate.dao.PrpcCustomerDao;
import com.sinosoft.efiling.hibernate.dao.PrpcMaincovernoteDao;
import com.sinosoft.efiling.hibernate.dao.PrppCustomerDao;
import com.sinosoft.efiling.hibernate.dao.PrppMaincovernoteDao;
import com.sinosoft.efiling.hibernate.dao.PrptcarshiptaxDao;
import com.sinosoft.efiling.hibernate.dao.UserDao;
import com.sinosoft.efiling.hibernate.entity.Company;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.DocumentFile;
import com.sinosoft.efiling.hibernate.entity.FileType;
import com.sinosoft.efiling.hibernate.entity.Product;
import com.sinosoft.efiling.hibernate.entity.ProductRisk;
import com.sinosoft.efiling.hibernate.entity.PrpCInsured;
import com.sinosoft.efiling.hibernate.entity.PrpCItemcar;
import com.sinosoft.efiling.hibernate.entity.PrpCMain;
import com.sinosoft.efiling.hibernate.entity.PrpDUwmtypes;
import com.sinosoft.efiling.hibernate.entity.PrpPInsured;
import com.sinosoft.efiling.hibernate.entity.PrpPMain;
import com.sinosoft.efiling.hibernate.entity.PrpTInsured;
import com.sinosoft.efiling.hibernate.entity.PrpTItemcar;
import com.sinosoft.efiling.hibernate.entity.PrpTMain;
import com.sinosoft.efiling.hibernate.entity.PrpTMainsub;
import com.sinosoft.efiling.hibernate.entity.PrpcCustomer;
import com.sinosoft.efiling.hibernate.entity.PrpcMaincovernote;
import com.sinosoft.efiling.hibernate.entity.PrppMaincovernote;
import com.sinosoft.efiling.hibernate.entity.Prptcarshiptax;
import com.sinosoft.efiling.hibernate.entity.User;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.efiling.util.UserSessionEntity;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.Helper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;
import com.sinosoft.util.hibernate.paging.PagingEntity;

/**
 * 单证自动审核流程的调用
 * 
 * @author ZhangJun
 */
@SuppressWarnings("unchecked")
public class DocumentAuditService extends DocumentService {
	/**
	 * 保存数据,共享相同资料,判断是否自动单证审核
	 * 
	 * @param businessNo 业务号
	 * @return 返回自动单证审核的状态标志。 false:不自动单证审核 true:自动单证审核
	 * @see #saveDocumntFiles(Document)
	 * @see #share(String)
	 */
	public boolean handle(String businessNo) {
		// 保存数据,共享资料在saveDocumentFiles(Document)方法中已调用
		save(businessNo);
		// 调用是否自动单证审核的方法
		return isAutoApprove(businessNo);
	}

	/**
	 * 为businessNo单证共享资料。将其他已经上传的资料共享到此业务号中
	 * 
	 * @param businessNo 业务号
	 */
	public void share(String businessNo) {
		Document document = this.getDocument(businessNo);
		Document anotherDocument = document.getAnother();
		List<DocumentFile> list = new ArrayList<DocumentFile>();
		List<DocumentFile> documentFiles = documentFileDao.queryByProperty("document", document);
		list.addAll(documentFiles);

		List<DocumentFile> anotherDocumentFiles = null;
		if (anotherDocument != null) {
			anotherDocumentFiles = documentFileDao.queryByProperty("document", anotherDocument);
			list.addAll(anotherDocumentFiles);
		}
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(DocumentFile.class.getName()).append(" d ");
		hql.append(" WHERE d.paperType = ? AND d.paperCode = ? AND d.status = ? ");
		hql.append(" AND (d.dueTime IS NOT NULL AND d.dueTime > ?) ");
		final String queryString = hql.toString();
		Date now = new Date();
		Object[] parameters;
		String paperType;
		String paperCode;
		FileType fileType;
		for (DocumentFile documentFile : list) {
			if (!SystemUtils.DOCUMENT_FILE_STATUS_LACK.equals(documentFile.getStatus())) continue; // 仅差缺文件可共享
			fileType = documentFile.getFileType();
			if (!SystemUtils.YES.equals(fileType.getShared())) continue; // 只有是可共享的文件才能共享
			paperType = documentFile.getPaperType();
			paperCode = documentFile.getPaperCode();
			if (Helper.isEmpty(paperType) || Helper.isEmpty(paperCode)) continue; // 仅可唯一识别资料的文件可共享
			hql.setLength(0);
			hql.append(queryString);
			hql.append("AND ( d.fileType = ? ");
			if (Helper.contains(SystemUtils.FILE_TYPE_CLIENT_CODES, fileType.getCode())) {
				// 身份证的共享
				hql.append("OR d.fileTypeCode IN (?");
				hql.append(StringHelper.copy(", ?", SystemUtils.FILE_TYPE_CLIENT_CODES.length - 1));
				hql.append(")");

				parameters = new Object[5 + SystemUtils.FILE_TYPE_CLIENT_CODES.length];
				parameters[0] = paperType;
				parameters[1] = paperCode;
				parameters[2] = SystemUtils.DOCUMENT_FILE_STATUS_FILE;
				parameters[3] = now;
				parameters[4] = fileType;
				System.arraycopy(SystemUtils.FILE_TYPE_CLIENT_CODES, 0, parameters, 5, SystemUtils.FILE_TYPE_CLIENT_CODES.length);
			} else {
				hql.append(" OR d.fileTypeCode = ? ");
				parameters = new Object[] { paperType, paperCode, SystemUtils.DOCUMENT_FILE_STATUS_FILE, now, fileType,
						documentFile.getFileTypeCode() };
			}
			hql.append(") ");
			hql.append(" ORDER BY d.fileTime DESC, d.id DESC"); // 按归档时间倒序排序
			List<DocumentFile> c = ((PagingEntity<DocumentFile>) dao.query(hql.toString(), parameters, 1, 1)).list();
			if (Helper.isEmpty(c)) continue;
			share(c.get(0), documentFile, SystemUtils.NO);
		}
		// 更新document对象的差缺信息/归档状态
		if (!documentFiles.isEmpty()) {
			changeDocumentFileStatus(document, null);
		}
		if (anotherDocument != null && !anotherDocumentFiles.isEmpty()) {
			changeDocumentFileStatus(anotherDocument, null);
		}
	}

	/** 抽取已归档的单证进行手工单证审核的比例基数,默认为100 */
	public static final int MANUAL_RATE_MODULUS = NumberHelper.intValue(SystemHelper.getProperty("document.audit.manual.modulus"), 100);
	/** 抽取已归档的单证进行手工单证审核的比率,默认为1,即1% */
	public static final int MANUAL_RATE = NumberHelper.intValue(SystemHelper.getProperty("document.audit.manual.rate"), 1);

	/**
	 * 判断是否走自动单证审核
	 * 
	 * @return
	 */
	public boolean isAutoApprove(String businessNo) {
		Document document = this.getDocument(businessNo);
		String approveStatus = SystemUtils.APPROVE_AUTO_STATUS; // 自动审核
		boolean filed = false; // 是否自动单证审核
		// 批单不处理
		if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(document.getType())) {
			// 手工单证审核
			approveStatus = SystemUtils.APPROVE_NOAUTO_STATUS;
			filed = false;
		} else if (SystemUtils.DOCUMENT_STATUS_FILE_MANUAL.equals(document.getFileStatus())
				|| SystemUtils.DOCUMENT_SOURCE_B2C.equals(document.getSource())) {
			// 特批归档和电商的自动单证审核
			filed = true;
		} else if ("50000000".equals(document.getBusinessCompany())) {
			// 2014-04-01 暂时先将重庆的所有单证均不自动审核
			approveStatus = SystemUtils.APPROVE_NOAUTO_STATUS;
			filed = false;
		} else {
			if (SystemUtils.DOCUMENT_STATUS_FILE.equals(document.getFileStatus())) {
				// 单证已归档齐全
				filed = true;
			} else {
				// fileType.signed,原件资料的状态 0:非原件资料 1:原件资料
				String hql = "SELECT COUNT(*) FROM " + DocumentFile.class.getName()
						+ " f WHERE f.status = ? AND f.fileType.signed <> ? AND f.document = ?";
				filed = 0 >= NumberHelper
						.intValue(dao.uniqueResult(hql, new Object[] { SystemUtils.DOCUMENT_FILE_STATUS_LACK, SystemUtils.YES, document }));
			}
			if (!filed) {
				// 手工单证审核
				approveStatus = SystemUtils.APPROVE_NOAUTO_STATUS;
			} else {
				if (MANUAL_RATE <= 0) {
					filed = true; // 满足自动单证审核的全部自动单证审核
				} else if (MANUAL_RATE >= MANUAL_RATE_MODULUS) {
					filed = false;// 全部手工单证审核
				} else {
					filed = Math.floor(Math.random() * MANUAL_RATE_MODULUS) + 1 > MANUAL_RATE;
				}
				if (!filed) {
					// 按概率抽出来的手动单证审核
					approveStatus = SystemUtils.APPROVE_AUTO_RATE_STATUS;
				}
			}
		}
		// 改变单证审核的状态
		document.setApproveStatus(approveStatus);
		dao.update(document);
		return filed;
	}

	/**
	 * 将承保数据保存到eFiling数据库中
	 * 
	 * @param no 投保单号,保单,批单
	 */
	public Document save(String no) {
		return save(no, getDocumentType(no));
	}

	/**
	 * 核保通过之后,保存eFiling系统中相应单证和承保资料的数据
	 * 
	 * @param no
	 *            投保单号,保单,批单
	 * @param 单号类型
	 *            7.批单 8.保单 9.投保单
	 */
	public Document save(String no, String type) {
		System.out.println(no + " 开始保存数据");
		logger.info(no + " 开始保存数据");
		type = StringHelper.trim(type);
		Document document = null;
		try {
			if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(type)) {
				// 投保单
				// PrpTMain prpTMain = prpTMainDao.get(no);
				String policyNo = null;// prpTMain.getPolicyno();

				String queryPolicyNoSql = "SELECT policyNo FROM PrpCMain WHERE proposalNo = ?";// '9105013301140008781000';
				try {
					policyNo = (String) dao.uniqueResultSQL(queryPolicyNoSql, new Object[] { no });
				} catch (Exception e) {
					// TODO
					e.printStackTrace();
				}

				if (!StringHelper.isEmpty(policyNo)) {
					// 已经生成了保单
					document = this.savePolicy(policyNo);
				} else {
					document = this.saveProposal(no);
				}
			} else if (type.equals(SystemUtils.DOCUMENT_TYPE_POLICY)) {
				// 普通保单从PRPCMAIN表取数据, 大保单从PRPCMAINCOVERNOTE,大保单没有投保单,只有保单和批单的说法
				document = this.savePolicy(no);
			} else if (type.equals(SystemUtils.DOCUMENT_TYPE_ENDOR)) {
				// 普通批单从PRPPMAIN表取数据 , 大保单批单从PRPPMAINCOVERNOTE表取数据
				document = this.saveEndor(no);
			} else if (type.equals(SystemUtils.DOCUMENT_TYPE_VISA)) {
				//保存单证系统过来的单证流水号
			}

			StringBuilder buf = new StringBuilder(no);
			buf.append(" 数据保存完成,需要的承保资料清单:\n");
			int i = 1;
			if (document.getDocumentFiles() != null) {
				try {
					for (DocumentFile df : document.getDocumentFiles()) {
						buf.append("\n\t").append(i++).append(".").append(df.getFileType().getId()).append(" - ")
								.append(df.getFileType().getName());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println(buf);
			logger.info(buf.toString());
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			logger.error("数据保存失败:no=" + no + ",type=" + type, e);
		}
		return document;
	}
	public boolean isSpecialStatus(PrpTMain prpTMain, String proposalNo) {
		//RE17081500007  四川地区不允许跨年收取车船税, 主要因为四川跨年不允许收取车船税是指提前购买第二年的保险，录单完成与平台交互后，平台会显示已完税，保险公司无法进行代收第二年的车船税
		//对于平台返回已完税的数据且不要求录入完税证明号的保单，即跨年投保+已完税+完税凭证号为空，在资料归档的处理为特殊归档。
		boolean isStatus = false;
		String comCode = prpTMain.getComCode();
		String riskCode = prpTMain.getRiskCode();
		Date inputdate = prpTMain.getInputDate();
		Date startdate = prpTMain.getStartDate();
		int nowYear = DateHelper.getYearByDate(inputdate);
		int startYear = DateHelper.getYearByDate(startdate);
		if (comCode.startsWith("51") && "0508".equals(riskCode)) {
			Prptcarshiptax prptcarshiptax = prptcarshiptaxDao.get(proposalNo);
			if (prptcarshiptax != null && nowYear != startYear) {
				//跨年的投保的交强险
				if ("P".equals(prptcarshiptax.getTaxconditioncode()) && 
				    ("".equals(prptcarshiptax.getTaxdocumentnumber()) || prptcarshiptax.getTaxdocumentnumber() == null)) {
					//为完税类型并且完税凭证号为空
					isStatus = true;
				}
			}
		}
		return isStatus;
	}
	/**
	 * 保存投保单数据
	 * 
	 * @param proposalNo 投保单号
	 */
	public Document saveProposal(String proposalNo) {
		PrpTMain prpTMain = prpTMainDao.get(proposalNo);
		PrpCMain prpCMain = null;// prpTMainDao.get(proposalNo);
		Document document = dao.get(proposalNo);
		// Set<DocumentFile> documentFiles = new HashSet<DocumentFile>();
		if (prpTMain == null) return new Document(); 
		String riskCode = prpTMain.getRiskCode();// 险种
		String anotherProposalNo = null; // 主要针对车险的联合投保用
		Document another = null;
		if (document == null) {
			// 保存新的投保单
			document = new Document();
			document.setId(proposalNo);
			document.setNo(proposalNo);
			document.setProposalNo(proposalNo);
			document.setStatus(SystemUtils.STATUS_INVALID);
			document.setType(SystemUtils.DOCUMENT_TYPE_PROPOSAL);// 单证类型：投保单
			if (isSpecialStatus(prpTMain, proposalNo)) {
				// 设置为手动归档
				document.setFileStatus(SystemUtils.DOCUMENT_STATUS_FILE_MANUAL);
				document.setLacks(SystemUtils.FILE_MANUAL_MESSAGE);
			} else {
				// 默认为未归档
				document.setFileStatus(SystemUtils.DOCUMENT_STATUS_UNFILE);
				// 存差缺文字
				document.setLacks(SystemUtils.LACK_MESSAGE);
			}
			document.setLent(SystemUtils.FILE_LENT_NO);
		} else {
			another = document.getAnother();
		}

		// 判断是否已经生产了保单
		if (!SystemUtils.DOCUMENT_TYPE_POLICY.equals(document.getType())) {
			String queryPolicyNoSql = "SELECT policyNo FROM PrpCMain WHERE proposalNo = ?";// '9105013301140008781000';
			try {
				String policyNo = (String) dao.uniqueResultSQL(queryPolicyNoSql, new Object[] { proposalNo });
				if (!StringHelper.isEmpty(policyNo)) {
					// 已经生成保单
					document.setNo(policyNo);
					document.setPolicyNo(policyNo);
					document.setStatus(SystemUtils.STATUS_VALID);
					document.setType(SystemUtils.DOCUMENT_TYPE_POLICY);
				}
			} catch (Exception e) {

			}
		}

		if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(document.getType())) {
			// 已生成保单
			prpCMain = prpCMainDao.get(document.getPolicyNo());
		}

		// 保单来源 第一位0/1--内网/外网；第二位1--批量投保；第3-4位 01-电子商务 02-意时网 03-蒙代尔
		// 04-网站;第10为1--货运险电子申报单
		// String resouse = prpTMain.getPolisource();
		// 如果为3104 E通卡 判断prpTmain 表 papolicyno websource 两字段皆为空为 核心出单  其余情况皆为第三方出单
		String source = StringHelper.trim(prpCMain == null ? prpTMain.getPolisource() : prpCMain.getPolisource());
		if ("3104".equals(riskCode)) {
			if (prpCMain == null && StringHelper.isEmpty(prpTMain.getPapolicyno()) && StringHelper.isEmpty(prpTMain.getWebsource())) {
				// 核心系统
				document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
			} else if (prpCMain != null && StringHelper.isEmpty(prpCMain.getPapolicyno())
					&& StringHelper.isEmpty(prpCMain.getWebsource())) {
				// 核心系统
				document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
			} else {
				// 电子商务
				document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
			}
		} else {
			/*if (source.length() >= 4 && "00".equals(source.substring(2, 4))) {
				// 核心系统
				document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
			} else {
				// 电子商务
				document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
			}*/
			if ("0050000003".equals(source) || "0050000002".equals(source)) {
				//网电销为电子商务
				document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
			} else {
				document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
			}
		}
		document.setRiskType(SystemUtils.RISK_CLASS_MOTOR.equals(prpTMain.getClassCode()) ? SystemUtils.RISK_TYPE_AUTO : SystemUtils.RISK_TYPE_NON_AUTO);
		document.setRiskClass(prpTMain.getClassCode());
		document.setRiskCode(prpTMain.getRiskCode());
		// 产品线
		document.setProduct(this.getProduct(document.getRiskCode()));
		// 承保年度
		if (prpCMain == null) {
			// 投保人类型必须从Prptinsured表查出来
			List<PrpTInsured> PrpTInsuredList = (List<PrpTInsured>) this.queryInsureds(document.getProposalNo(),
					SystemUtils.DOCUMENT_TYPE_PROPOSAL, SystemUtils.CLIENT_RELATION_APPLICANT);
			PrpTInsured prpTInsured = null;
			if (!Helper.isEmpty(PrpTInsuredList)) {
				prpTInsured = PrpTInsuredList.get(0);
				// 1.个人,2.单位
				document.setApplicantType(prpTInsured.getInsuredType());
				// 证件类型
				document.setApplicantPassportType(prpTInsured.getIdentifyType());
				// 证件号码
				document.setApplicantPassportNo(prpTInsured.getIdentifyNumber());
			}

			// 保存被保险人,有可能有多个被保险人
			List<PrpTInsured> prpTinsureds = (List<PrpTInsured>) this.queryInsureds(document.getProposalNo(),
					SystemUtils.DOCUMENT_TYPE_PROPOSAL, SystemUtils.CLIENT_RELATION_INSURED);
			String insuredNames = getInusuredNames(prpTinsureds, SystemUtils.DOCUMENT_TYPE_PROPOSAL);
			document.setInsured(insuredNames);

			// 生效日期
			document.setEffectiveTime(
					DateHelper.set(prpTMain.getStartDate(), Calendar.HOUR_OF_DAY, NumberHelper.intValue(prpTMain.getStarthour())));
			document.setDueTime(DateHelper.set(prpTMain.getEndDate(), Calendar.HOUR_OF_DAY, NumberHelper.intValue(prpTMain.getEndhour())));
		} else {
			List<PrpCInsured> PrpCInsuredList = (List<PrpCInsured>) this.queryInsureds(document.getPolicyNo(),
					SystemUtils.DOCUMENT_TYPE_POLICY, SystemUtils.CLIENT_RELATION_APPLICANT);
			PrpCInsured prpCInsured = null;
			if (!Helper.isEmpty(PrpCInsuredList)) {
				prpCInsured = PrpCInsuredList.get(0);
				// 1.个人,2.单位
				document.setApplicantType(prpCInsured.getInsuredType());
				// 证件类型
				document.setApplicantPassportType(prpCInsured.getIdentifyType());
				// 证件号码
				document.setApplicantPassportNo(prpCInsured.getIdentifyNumber());
			}

			// 保存被保险人,有可能有多个被保险人
			List<PrpCInsured> prpCinsureds = (List<PrpCInsured>) this.queryInsureds(document.getPolicyNo(),
					SystemUtils.DOCUMENT_TYPE_POLICY, SystemUtils.CLIENT_RELATION_INSURED);
			String insuredNames = getInusuredNames(prpCinsureds, SystemUtils.DOCUMENT_TYPE_POLICY);
			document.setInsured(insuredNames);

			// 生效日期
			document.setEffectiveTime(
					DateHelper.set(prpCMain.getStartDate(), Calendar.HOUR_OF_DAY, NumberHelper.intValue(prpCMain.getStarthour())));
			document.setDueTime(DateHelper.set(prpCMain.getEndDate(), Calendar.HOUR_OF_DAY, NumberHelper.intValue(prpCMain.getEndhour())));
		}

		document.setApplicant(prpCMain == null ? prpTMain.getAppliName() : prpCMain.getAppliName());
		// 代理人编码
		document.setAgentNo(prpCMain == null ? prpTMain.getAgentCode() : prpCMain.getAgentCode());
		document.setAgentName(prpCMain == null ? prpTMain.getAgentName() : prpCMain.getAgentName());
		// 出单员
		User user = userDao.get(prpTMain.getOperatorCode());
		document.setSales(user);
		document.setSalesTime(prpTMain.getOperateDate());

		// 业务关系代码
		document.setBusinessNo(prpCMain == null ? prpTMain.getAgreementNo() : prpCMain.getAgreementNo());
		// 内部业务员
		user = userDao.get(prpCMain == null ? prpTMain.getHandler1Code() : prpCMain.getHandler1Code());
		document.setBusinessUser(user);
		// 业务部门
		Company dept = companyDao.get(prpCMain == null ? prpTMain.getComCode() : prpCMain.getComCode());
		document.setBusinessDept(dept);
		Company company = CompanyService.getCompany(dept);
		document.setBusinessCompany(company);

		// if (prpCMain == null) {
		// document.setEffectiveTime(DateHelper.set(prpTMain.getStartDate(), Calendar.HOUR_OF_DAY,
		// NumberHelper.intValue(prpTMain.getStarthour())));
		// document.setDueTime(DateHelper.set(prpTMain.getEndDate(), Calendar.HOUR_OF_DAY,
		// NumberHelper.intValue(prpTMain.getEndhour())));
		// } else {
		// document.setEffectiveTime(DateHelper.set(prpCMain.getStartDate(), Calendar.HOUR_OF_DAY,
		// NumberHelper.intValue(prpCMain.getStarthour())));
		// document.setDueTime(DateHelper.set(prpCMain.getEndDate(), Calendar.HOUR_OF_DAY,
		// NumberHelper.intValue(prpCMain.getEndhour())));
		// }

		// 承保年度,按照生效日期开始算
		document.setYear(DateHelper.getYearByDate(document.getEffectiveTime()));

		// 数据生成时间
		// document.setCreateTime(new Date());

		// 如果是联合投保,组装另外一个投保单号的Document对象,联合投保传进来的都是商业险(0501)的单子,交强险单子(0508)会跟着改变状态
		// if (SystemUtils.CAR_RISK_0508.equals(riskCode) || SystemUtils.CAR_RISK_0501.equals(riskCode)) {
		if (SystemUtils.RISK_TYPE_AUTO.equals(document.getRiskType())) {
			// 判断是否为联合投保商业险、交强险时.
			// 联合投保仅通过投保单数据判断
			List<PrpTMainsub> list = prpTMainsubDao.queryByProperty(new Object[][] { { "id.proposalno", document.getProposalNo() } });
			PrpTMainsub prpTMainsub = null;
			String combineFlag = null;
			if (!Helper.isEmpty(list)) {
				prpTMainsub = list.get(0);
				combineFlag = prpTMainsub.getCombineFlag();
				// combineFlag:COMBINE 联合投保 MOTOR 商业险 MTPL 交强险
				if (SystemUtils.COMBINEFLAG_COMBINE.equals(combineFlag)) {
					// 联合投保 得到另外一个投保单号
					anotherProposalNo = prpTMainsub.getMainproposalno();
				}
			}

			if (prpCMain == null) {
				// 读取投保单数据
				List<PrpTItemcar> prpTItemcars = prpTItemcarDao
						.queryByProperty(new Object[][] { { "id.proposalno", document.getProposalNo() } });
				PrpTItemcar prpTItemcar = null;
				if (!Helper.isEmpty(prpTItemcars)) prpTItemcar = prpTItemcars.get(0);
				if (prpTItemcar != null) {
					// 设置发动机号和车牌号车架号
					document.setVin(prpTItemcar.getVinno());
					document.setEngineNo(prpTItemcar.getEngineno());
					document.setLicenseNo(prpTItemcar.getLicenseno());
				}
			} else {
				// 读取保单数据
				// List<PrpCMainsub> list = prpCMainsubDao.queryByProperty(new Object[][] { { "id.policyno",
				// document.getPolicyNo() } });
				// PrpCMainsub prpCMainsub = null;
				// String combineFlag = null;
				// if (!Helper.isEmpty(list)) {
				// prpCMainsub = list.get(0);
				// combineFlag = prpCMainsub.getCombineFlag();
				// // combineFlag:COMBINE 联合投保 MOTOR 商业险 MTPL 交强险
				// if (SystemUtils.COMBINEFLAG_COMBINE.equals(combineFlag)) {
				// // 联合投保 得到另外一个投保单号
				// // anotherProposalNo = prpCMainsub.getMainproposalno();
				// PrpCMain cmain = prpCMainDao.get(prpCMainsub.getId().getMainpolicyno());
				// if (cmain != null) anotherProposalNo = cmain.getProposalno();
				// }
				// }

				List<PrpCItemcar> prpCItemcars = prpCItemcarDao
						.queryByProperty(new Object[][] { { "id.policyno", document.getPolicyNo() } });
				PrpCItemcar prpCItemcar = null;
				if (!Helper.isEmpty(prpCItemcars)) prpCItemcar = prpCItemcars.get(0);
				if (prpCItemcar != null) {
					// 设置发动机号和车牌号车架号
					document.setVin(prpCItemcar.getVinno());
					document.setEngineNo(prpCItemcar.getEngineno());
					document.setLicenseNo(prpCItemcar.getLicenseno());
				}

			}
		}
		document.setAnother(null);

		dao.saveOrUpdate(document);

		// 保存单证的档案资料明细,这些资料是通过核保系统审批时候勾选的提交的承保档案资料
		saveDocumntFiles(document);

		// if (another != null && !another.getId().equals(anotherProposalNo)) {
		// // 可能是由联合投保变为非联合投保,因此需要删除联合投保的单子
		// dao.delete(another);
		// another = null;
		// }

		if (another != null && !another.getId().equals(anotherProposalNo)) {
			// 可能是由联合投保变为非联合投保,因此需要删除联合投保的单子
			if (SystemUtils.STATUS_VALID.equals(another.getStatus())) {
				// 尚未生效的单子
				dao.delete(another);
			} else {
				another.setAnother(null);
				dao.update(another);
			}

			another = null;
		}

		// 由于联合投保是两张单号同时生成, 所以需要在eFiling系统中保存两张单子的数据,传进来的单号都是商业险(0501)的单号
		if (!StringHelper.isEmpty(anotherProposalNo)) {
			another = saveAnother(document, anotherProposalNo);
		}
		return document;
	}
	
	/**
	 * 保存单证流水号
	 * @param  单证流水号
	 */
	public Document saveVisaNo(String visaNo) {
		Document document = dao.getByProperty("visaNo", visaNo);
		if (!Helper.isEmpty(document)) return document;
		
		// 保存新的投保单
		document = new Document();
		String visaStatus = ""; //06:单证系统对应作废单证  05：遗失单证 04：已使用单证
		String userCode = "";	//用户代码
		String businessNo = "";
		String sql = "SELECT visastatus, visacode, businessno, usercode, comcode FROM VSMARK WHERE visaserialno = ?";
		List<?> list = dao.querySQL(sql, new String[] { visaNo });
		if (!Helper.isEmpty(list)) {
			Object [] visas = (Object[]) list.get(0);
			visaStatus = StringHelper.trim(visas[0]);
			businessNo = StringHelper.trim(visas[2]);
			userCode = StringHelper.trim(visas[3]);
			if (!SystemUtils.VISA_STATUS_VOIDED.equals(visaStatus) && !SystemUtils.VISA_STATUS_LOST.equals(visaStatus)
				&& !SystemUtils.VISA_STATUS_VALID.equals(visaStatus)) {
				//只有06:作废单证 05:遗失单证 04:已使用单证才会进入数据到电子档案
				return document;
			}
			if (SystemUtils.VISA_STATUS_VOIDED.equals(visaStatus)) {
				//作废单证
				document.setApplicant(SystemUtils.APPLICANT_VISA_VOIDED);
				document.setVisaStatus(SystemUtils.FILE_TYPE_VISA_VOIDED);
			} else if (SystemUtils.VISA_STATUS_LOST.equals(visaStatus)) {
				//遗失单证
				document.setApplicant(SystemUtils.APPLICANT_VISA_LOST);
				document.setVisaStatus(SystemUtils.FILE_TYPE_VISA_LOST);
			} 
			if (SystemUtils.VISA_STATUS_VALID.equals(visaStatus)) {
				//已使用单证需要保存关联的保单号和批单号
				document.setNo(businessNo);
			}
		}
		document.setId(SystemUtils.DOCUMENT_TYPE_VISA + visaNo.trim());
		document.setNo(visaNo.trim());
		document.setStatus(SystemUtils.STATUS_INVALID);
		document.setType(SystemUtils.DOCUMENT_TYPE_VISA);// 单证类型：单证类型
		// 默认为未归档
		document.setFileStatus(SystemUtils.DOCUMENT_STATUS_UNFILE);
		// 存差缺文字
		document.setLacks(SystemUtils.LACK_MESSAGE);
		document.setLent(SystemUtils.FILE_LENT_NO);
		document.setVisaNo(visaNo);
		Date now = new Date();
		document.setInsertTime(now);
		// 出单员
		User user = userDao.get(userCode);
		Company company = new Company();
		//UserSessionEntity userEntity = (UserSessionEntity) user;
		//UserSessionEntity user = userDao.get(userCode);
		document.setCreateTime(new Date());
		document.setUser(user);
		document.setDepartment(user.getDepartment());
		//document.setCompany();
		document.setUpdateTime(now);
		document.setUpdateUser(user);
		document.setUpdateDepartment(user.getDepartment());
		dao.save(document);
		saveDocumntFiles(document);
		
		//保存档案资料
		DocumentFile documentFile = new DocumentFile();

		return document;
	}

	/**
	 * 得到保险人名字,如果有多人时返回最长不超过500字节的字符串
	 * 
	 * @param prpInsureds PrpTInsured/PrpPInsured/PrpCInsured
	 * @param type 9投保单,7.批单,8保单
	 * @return 组合在一起的保险人名称,多个之间通过','隔开的
	 */
	protected String getInusuredNames(List<?> prpInsureds, String type) {
		if (Helper.isEmpty(prpInsureds)) return null;
		StringBuffer insuredNames = new StringBuffer();

		for (Object insured : prpInsureds) {
			insuredNames.append(",");
			if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(type)) {
				insuredNames.append(((PrpTInsured) insured).getInsuredName());
			} else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(type)) {
				insuredNames.append(((PrpPInsured) insured).getInsuredName());
			} else {
				insuredNames.append(((PrpCInsured) insured).getInsuredName());
			}
		}
		if (insuredNames.length() > 0) insuredNames.deleteCharAt(0);
		return StringHelper.substringLeft(insuredNames.toString(), 500);
	}

	/**
	 * 主要用于联合投保,保存交强险(0508)的数据
	 * 
	 * @param document 已经保存的数据
	 * @param anotherNo 联合投保的另外一张单子的投保单号
	 * @return 0508的单证
	 */
	public Document saveAnother(Document document, String anotherNo) {
		Document another = dao.get(anotherNo);
		if (another == null) {
			// 新单子
			another = new Document();
			another.setId(anotherNo);
			another.setNo(anotherNo);
			another.setProposalNo(anotherNo);
			another.setStatus(SystemUtils.STATUS_INVALID);
			another.setType(SystemUtils.DOCUMENT_TYPE_PROPOSAL);// 单证类型：投保单

			// 默认为未归档
			another.setFileStatus(SystemUtils.DOCUMENT_STATUS_UNFILE);
			// 存差缺文字
			another.setLacks(SystemUtils.LACK_MESSAGE);
			another.setLent(SystemUtils.FILE_LENT_NO);
		}

		Helper.copyValues(document, another, new String[] { "id", "no", "policyNo", "proposalNo", "type", "fileStatus", "lacks", "lent",
				"fileLending", "files", "documentFiles" });

		// 判断是否已经生产了保单
		String queryPolicyNoSql = "SELECT policyNo FROM PrpCMain WHERE proposalNo = ?";// '9105013301140008781000';
		try {
			String policyNo = (String) dao.uniqueResultSQL(queryPolicyNoSql, new Object[] { anotherNo });
			if (!StringHelper.isEmpty(policyNo)) {
				// 已经生成保单
				another.setNo(policyNo);
				another.setPolicyNo(policyNo);
				another.setStatus(SystemUtils.STATUS_VALID);
				another.setType(SystemUtils.DOCUMENT_TYPE_POLICY);
			}
		} catch (Exception e) {

		}

		// another.setFileStatus(SystemUtils.DOCUMENT_STATUS_UNFILE);
		// another.setLent(SystemUtils.FILE_LENT_NO);

		// 不用判断是否生成保单，保单和投保单的险种代码肯定是一致的
		PrpTMain prpTMain = prpTMainDao.get(anotherNo);
		another.setRiskCode(prpTMain.getRiskCode());
		another.setProduct(getProduct(prpTMain.getRiskCode()));

		another.setAnother(document);

		dao.saveOrUpdate(another);
		saveDocumntFiles(another);

		document.setAnother(another);
		dao.update(document);

		return another;
	}

	// 组装保存保单的方法
	public Document savePolicy(String policyNo) {
		PrpCMain prpCMain = prpCMainDao.get(policyNo);
		// 表示大保单
		if (prpCMain == null) return saveCovernotePolicy(policyNo);

		// String applicationNo = policyNo;
		Document document = getDocument(prpCMain.getProposalno());
		Document another = null;
		// if (document == null) {
		// 没有保存投保单的数据。
		document = saveProposal(prpCMain.getProposalno());
		// } else {
		// 保存单证的档案资料明细,这些资料是通过核保系统审批时候勾选的提交的承保档案资料
		// saveDocumntFiles(document);

		// 判断联合投保标识是否有变化
		another = document.getAnother();

		// PrpTMain prpTMain = prpTMainDao.get(prpCMain.getProposalno());
		// String anotherProposalNo = null;
		// // 判断是否为联合投保商业险、交强险时
		// List<PrpTMainsub> list = prpTMainsubDao.queryByProperty("id.proposalno", prpCMain.getProposalno());
		// PrpTMainsub prpTMainsub = null;
		// String combineFlag = null;
		// if (!Helper.isEmpty(list)) {
		// prpTMainsub = list.get(0);
		// combineFlag = prpTMainsub.getCombineFlag();
		// // combineFlag:COMBINE 联合投保 MOTOR 商业险 MTPL 交强险
		// if (SystemUtils.COMBINEFLAG_COMBINE.equals(combineFlag)) {
		// // 联合投保 得到另外一个投保单号
		// anotherProposalNo = prpTMainsub.getMainproposalno();
		// }
		// }
		//
		// if (another != null && !another.getId().equals(anotherProposalNo)) {
		// // 可能是由联合投保变为非联合投保,因此需要删除联合投保的单子
		// if (SystemUtils.STATUS_VALID.equals(another.getStatus())) {
		// // 尚未生效的单子
		// dao.delete(another);
		// } else {
		// another.setAnother(null);
		// dao.update(another);
		// }
		//
		// another = null;
		// }
		//
		// // 由于联合投保是两张单号同时生成, 所以需要在eFiling系统中保存两张单子的数据,传进来的单号都是商业险(0501)的单号
		// if (!StringHelper.isEmpty(anotherProposalNo)) {
		// another = saveAnother(document, anotherProposalNo);
		// } else if (another != null) {
		// // 保存资料
		// saveDocumntFiles(another);
		// }
		// }

		document.setStatus(SystemUtils.STATUS_VALID);
		document.setPolicyNo(policyNo);
		document.setNo(policyNo);
		document.setType(SystemUtils.DOCUMENT_TYPE_POLICY);
		// document.setCreateTime(new Date());

		document.setSalesTime(prpCMain.getUnderwriteendDate()); // 生成保单后更改出单日期为保单生成日期
		document.setUpdateTime(new Date());

		Date date = document.getEffectiveTime();
		if (date.before(document.getSalesTime())) date = document.getSalesTime();
		// 承保年度,按照生效日期开始算,如果倒签,则按照核保日期算
		document.setYear(DateHelper.getYearByDate(date));

		dao.update(document);

		if (another != null) {
			// 联合投保
			// 查联合投保的保单号
			// String mainPolicyno = (String) dao.uniqueResultSQL(
			// "SELECT P.MAINPOLICYNO FROM PRPCMAINSUB P WHERE P.POLICYNO = ?", new Object[] { policyNo });
			prpCMain = prpCMainDao.getByProperty("proposalno", another.getProposalNo());

			if (prpCMain != null) {
				another.setPolicyNo(prpCMain.getId());
				// 联合投保在网银转账的时候,单子是一张一张批准的。如果第一张单子生成保单时候，第二张单子还没有生成保单号,可能会出问题
				another.setNo(another.getPolicyNo());
				another.setType(SystemUtils.DOCUMENT_TYPE_POLICY);
				another.setAnother(document);
				another.setStatus(SystemUtils.STATUS_VALID);
				another.setSalesTime(prpCMain.getUnderwriteendDate()); // 生成保单后更改出单日期为保单生成日期
				another.setUpdateTime(document.getUpdateTime());

				date = another.getEffectiveTime();
				if (date.before(another.getSalesTime())) date = another.getSalesTime();
				// 承保年度,按照生效日期开始算,如果倒签,则按照核保日期算
				another.setYear(DateHelper.getYearByDate(date));

				dao.update(another);
			}
		}

		return document;
	}

	// 组装保存批单的方法
	public Document saveEndor(String endorseNo) {
		PrpPMain prpPMain = prpPMainDao.get(endorseNo);
		// 表示大保单批单
		if (prpPMain == null) return saveCovernoteEndor(endorseNo);
		// 判断eFiling中是否已经插入了该批单的数据,如果插入了，不需要插入第二次
		Document document = dao.getByProperty("no", endorseNo);
		// 批单是从PRPPHEAD表查处核保的状态
		String sql = "SELECT UNDERWRITEFLAG, NVL(UNDERWRITEENDDATE, ENDORDATE), OPERATORCODE, VALIDDATE, VALIDHOUR, ENDORSOURCE FROM PRPPHEAD WHERE ENDORSENO = ?";
		Object[] results = ((List<Object[]>) dao.querySQL(sql, new Object[] { endorseNo })).get(0);
		String underWriteFlag = (String) results[0];
		String status = SystemUtils.STATUS_INVALID;
		// 判断批单是否有效,如果批单已经生效,改变批单的状态
		if (SystemUtils.UNDER_FLAG_YES_1.equals(underWriteFlag) || SystemUtils.UNDER_FLAG_YES_3.equals(underWriteFlag)) {
			status = SystemUtils.STATUS_VALID;
			// 重新同步保单数据
			savePolicy(prpPMain.getPolicyno());
		}

		Date salesTimes = (Date) results[1];
		if (document == null) {
			document = new Document();
			document.setId(endorseNo);
		}
		// 查询批单的出单来源
		sql = "SELECT POLISOURCE, PAPOLICYNO, WEBSOURCE, AGREEMENTNO, HANDLER1CODE, COMCODE, STARTDATE, STARTHOUR FROM PRPPMAIN WHERE ENDORSENO = ? ";
		Object[] sources = ((List<Object[]>) dao.querySQL(sql, new Object[] { endorseNo })).get(0);
		document.setType(SystemUtils.DOCUMENT_TYPE_ENDOR);// 单证类型：批单
		document.setNo(endorseNo);
		document.setProposalNo(prpPMain.getProposalno());
		document.setPolicyNo(prpPMain.getPolicyno());
		document.setEndorNo(endorseNo);
		// 保单来源 第一位0/1--内网/外网；第二位1--批量投保；第3-4位 01-电子商务 02-意时网 03-蒙代尔
		// 04-网站;第10为1--货运险电子申报单
		String source = StringHelper.trim(results[5]);
		if ("3104".equals(prpPMain.getRiskCode())) {
			if (StringHelper.isEmpty(sources[1]) && StringHelper.isEmpty(sources[2])) {
				// 核心系统
				document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
			} else {
				// 电子商务
				document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
			}
			// } else if ("2726".equals(prpPMain.getRiskCode())) {
			// if (source.length() >= 4 && (source.substring(2, 4).equals("00") || source.substring(2, 4).equals("03")))
			// {
			// // 核心系统
			// document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
			// } else {
			// // 电子商务
			// document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
			// }
		} else {
			if ("00".equals(source)) {
				// 核心系统
				document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
			} else {
				// 电子商务
				document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
			}
		}
		// 出单员
		User user = userDao.get(String.valueOf(results[2]));
		document.setSales(user);
		document.setStatus(status);
		document.setFileStatus(SystemUtils.DOCUMENT_STATUS_UNFILE);
		document.setLent(SystemUtils.FILE_LENT_NO);

		document.setRiskType(
				SystemUtils.RISK_CLASS_MOTOR.equals(prpPMain.getClassCode()) ? SystemUtils.RISK_TYPE_AUTO : SystemUtils.RISK_TYPE_NON_AUTO);
		document.setRiskClass(prpPMain.getClassCode());
		document.setRiskCode(prpPMain.getRiskCode());
		// 产品线
		document.setProduct(getProduct(prpPMain.getRiskCode()));

		// 设置被保险人,被保险人可能有多个
		List<PrpPInsured> prpPinsureds = (List<PrpPInsured>) this.queryInsureds(prpPMain.getId(), SystemUtils.DOCUMENT_TYPE_ENDOR,
				SystemUtils.CLIENT_RELATION_INSURED);
		List<PrpCInsured> prpCinsureds = null;
		String insuredNames = "";
		if (Helper.isEmpty(prpPinsureds)) {
			prpCinsureds = prpCInsuredDao.queryByProperty(
					new Object[][] { { "id.policyno", prpPMain.getPolicyno() }, { "insuredFlag", SystemUtils.CLIENT_RELATION_INSURED } });
			insuredNames = getInusuredNames(prpCinsureds, SystemUtils.DOCUMENT_TYPE_POLICY);
		} else {
			insuredNames = getInusuredNames(prpPinsureds, SystemUtils.DOCUMENT_TYPE_ENDOR);
		}
		document.setInsured(insuredNames);
		// 批单的投保人类型如果为null,表示没有修改投保人的资料,应该从原来的生成保单的时候投保人表查询出投保人的类型
		List<PrpPInsured> prpPInsureds = (List<PrpPInsured>) this.queryInsureds(prpPMain.getPolicyno(), SystemUtils.DOCUMENT_TYPE_ENDOR,
				SystemUtils.CLIENT_RELATION_APPLICANT);
		List<PrpCInsured> prpCInsureds = null;
		if (Helper.isEmpty(prpPInsureds)) {
			prpCInsureds = prpCInsuredDao.queryByProperty(
					new Object[][] { { "id.policyno", prpPMain.getPolicyno() }, { "insuredFlag", SystemUtils.CLIENT_RELATION_APPLICANT } });
		}
		String applicantType = "";
		String applicantNo = "";
		String passportType = "";
		PrpPInsured prpPInsured = null;
		PrpCInsured prpCInsured = null;
		if (!Helper.isEmpty(prpPInsureds)) {
			// IDENTIFYTYPE, IDENTIFYNUMBER, INSUREDTYPE
			prpPInsured = prpPInsureds.get(0);
			applicantType = prpPInsured.getInsuredType();
			applicantNo = prpPInsured.getIdentifyNumber();
			passportType = prpPInsured.getIdentifyType();
		} else if (!Helper.isEmpty(prpCInsureds) && !Helper.isEmpty(prpCInsureds.get(0))) {
			prpCInsured = prpCInsureds.get(0);
			applicantType = prpCInsured.getInsuredType();
			applicantNo = prpCInsured.getIdentifyNumber();
			passportType = prpCInsured.getIdentifyType();
		}
		document.setApplicantType(applicantType);
		// 证件类型
		document.setApplicantPassportType(passportType);
		// 证件号码
		document.setApplicantPassportNo(applicantNo);
		// 代理人编码
		document.setAgentNo(prpPMain.getAgentCode());
		// 存差缺文字
		document.setLacks(SystemUtils.LACK_MESSAGE);
		document.setAgentName(prpPMain.getAgentName());
		// 投保人
		document.setApplicant(prpPMain.getAppliName());

		if (salesTimes == null) salesTimes = prpPMain.getOperateDate(); // 尚未核保通过则使用批单录入日期
		document.setSalesTime(salesTimes);

		// 业务关系代码
		document.setBusinessNo(String.valueOf(sources[3]));
		// 内部业务员
		user = userDao.get(String.valueOf(sources[4]));
		document.setBusinessUser(user);
		// 业务部门
		Company dept = companyDao.get(String.valueOf(sources[5]));
		document.setBusinessDept(dept);
		Company company = CompanyService.getCompany(dept);
		document.setBusinessCompany(company);

		Date effectiveTime = (Date) results[3];
		effectiveTime = DateHelper.set(effectiveTime, Calendar.HOUR_OF_DAY, NumberHelper.intValue(results[4]));
		document.setEffectiveTime(effectiveTime);

		Date date = document.getEffectiveTime();
		if (date.before(document.getSalesTime())) date = document.getSalesTime();
		// 承保年度,按照生效日期开始算,如果倒签,则按照核保日期算
		document.setYear(DateHelper.getYearByDate(date));

		// 数据生成时间
		// document.setCreateTime(new Date());
		// 保存单证
		dao.saveOrUpdate(document);
		saveDocumntFiles(document);
		return document;
	}

	/**
	 * 查询业务号businessNo所需要的单证类型
	 * 
	 * @param businessNo 投保单号或批单号
	 * @return
	 */
	public List<PrpDUwmtypes> queryNeedDocumentFiles(String businessNo) {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(PrpDUwmtypes.class.getName()).append(" p ");
		hql.append(" WHERE p.id.policyno=? AND p.sort=?  ");
		// hql.append(" AND p.id.type NOT IN (?, ?, ?, ?, ?)"); // 去除001,002,003,007,008这几种资料类型
		// hql.append(" AND p.type IN (SELECT d.id.codeCode FROM ");
		// hql.append(PrpdCode.class.getName()).append(" d ");
		// hql.append(" WHERE d.id.codeType = 'DocmentManageType' and d.validStatus = '1')"); // 承保资料中有效的数据
		List<PrpDUwmtypes> prpDUwmtypes = (List<PrpDUwmtypes>) prpDUwmtypesDao.query(hql.toString(),
				new Object[] { businessNo, SystemUtils.UNDWRT_SORTED_STATUS });
		// SystemUtils.FILE_TYPE_COVER, // 特殊的资料类型.不需要从承保系统得来
		// SystemUtils.FILE_TYPE_POLICY, SystemUtils.FILE_TYPE_ENDOR, SystemUtils.FILE_TYPE_VISA_VOIDED,
		// SystemUtils.FILE_TYPE_VISA_LOST });
		// 直接返回查询的资料,如果为null就表示没有勾选任何资料,也没有默认资料
		return prpDUwmtypes;
		// if (!Helper.isEmpty(prpDUwmtypes)) return prpDUwmtypes;
		//
		// // prpDUwmtypes为空,则查询默认的资料
		// prpDUwmtypes = new ArrayList<PrpDUwmtypes>();
		// List<String> mustCodeType = queryDefaultDocumentFiles(businessNo, type.trim());
		// for (String codeType : mustCodeType) {
		// PrpDUwmtypes p = new PrpDUwmtypes();
		// p.setId(new PrpDUwmtypesId(businessNo, codeType));
		// prpDUwmtypes.add(p);
		// }
		// return prpDUwmtypes;

	}

	/**
	 * @param riskCode
	 *            险种代码
	 * @return 产品线
	 */
	public Product getProduct(String riskCode) {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT d.product FROM ").append(ProductRisk.class.getName()).append(" d ");
		hql.append("WHERE d.id = ?");
		return (Product) productRiskDao.uniqueResult(hql.toString(), new Object[] { riskCode });
	}

	/**
	 * 查询投保人或被保人信息
	 * 
	 * @param no 投保单号或者保单号或者批单号
	 * @param type 7.批单 8.保单 9.投保单
	 * @param insuredFlag 保险人类型 1：被保险人 2：保险人
	 * @return 投保人/百宝人等
	 */
	public List<?> queryInsureds(String no, String type, String insuredFlag) {
		List<?> list = null;
		if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(type.trim())) {
			// 投保单
			list = prpTInsuredDao.queryByProperty(new Object[][] { { "id.proposalno", no }, { "insuredFlag", insuredFlag } });
		} else if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(type.trim())) {
			// 保单
			list = prpCInsuredDao.queryByProperty(new Object[][] { { "id.policyno", no }, { "insuredFlag", insuredFlag } });
		} else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(type.trim())) {
			// 批单,应该从PRPCPINSURED表查询,这个表示保存的最新的批单批改的数据
			list = prpPInsuredDao.queryByProperty(new Object[][] { { "id.endorseno", no }, { "insuredFlag", insuredFlag } });
		}
		return list;
	}

	/**
	 * 对于是9999或者9997的险种的批单做特殊处理。保存eFiling系统数据
	 * 
	 * @param endorseNo
	 *            大保单批单号
	 * @param type
	 *            大保单批单类型
	 * @return
	 */
	public Document saveCovernoteEndor(String endorseNo) {
		PrppMaincovernote prppMaincovernote = prppMaincovernoteDao.get(endorseNo);
		Document document;
		document = dao.getByProperty("no", endorseNo);
		String sql = "SELECT UNDERWRITEFLAG, NVL(UNDERWRITEENDDATE, ENDORDATE), OPERATORCODE, VALIDDATE, VALIDHOUR FROM PRPPHEADCOVERNOTE WHERE ENDORSENO = ?";
		Object[] results = ((List<Object[]>) dao.querySQL(sql, new Object[] { endorseNo })).get(0);
		String underWriteFlag = (String) results[0];
		String status = SystemUtils.STATUS_INVALID;
		// 判断批单是否有效,如果批单已经生效,改变批单的状态
		if (SystemUtils.UNDER_FLAG_YES_1.equals(underWriteFlag) || SystemUtils.UNDER_FLAG_YES_3.equals(underWriteFlag)) {
			status = SystemUtils.STATUS_VALID;
			// 重新同步保单数据
			saveCovernotePolicy(prppMaincovernote.getPolicyno());
		}
		Date salesTime = (Date) results[1];

		if (document == null) {
			document = new Document();
			document.setId(endorseNo);
		}

		document.setType(SystemUtils.DOCUMENT_TYPE_ENDOR);// 单证类型：批单
		document.setNo(endorseNo);
		document.setProposalNo(prppMaincovernote.getProposalno());
		document.setPolicyNo(prppMaincovernote.getPolicyno());
		document.setEndorNo(endorseNo);

		// 保单来源 第一位0/1--内网/外网；第二位1--批量投保；第3-4位 01-电子商务 02-意时网 03-蒙代尔
		// 04-网站;第10为1--货运险电子申报单
		String source = StringHelper.trim(prppMaincovernote.getPolisource());
		if (source.length() >= 4 && "00".equals(source.substring(2, 4))) {
			// 核心系统
			document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
		} else {
			// 电子商务
			document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
		}
		document.setStatus(status);
		document.setFileStatus(SystemUtils.DOCUMENT_STATUS_UNFILE);
		document.setLent(SystemUtils.FILE_LENT_NO);
		document.setRiskType(SystemUtils.RISK_TYPE_NON_AUTO);
		document.setRiskClass(prppMaincovernote.getClasscode());
		document.setRiskCode(prppMaincovernote.getRiskcode());
		// // 产品线
		// document.setProduct(this.getProduct(prppMaincovernote.getRiskcode()));
		// 承保年度
		document.setYear(DateHelper.getYearByDate(prppMaincovernote.getOperatedate()));
		document.setApplicant(prppMaincovernote.getAppliname());
		List<Object[]> prpPcustomer = (List<Object[]>) dao.querySQL(
				"SELECT IDENTIFYTYPE, IDENTIFYNUMBER, INSUREDTYPE " + "FROM PRPPCUSTOMER WHERE ENDORSENO=? AND INSUREDFLAG=?",
				new Object[] { endorseNo, SystemUtils.CLIENT_RELATION_APPLICANT });
		List<Object[]> prpCcustomer = null;
		if (Helper.isEmpty(prpPcustomer)) {
			prpCcustomer = (List<Object[]>) dao.querySQL(
					"SELECT IDENTIFYTYPE, IDENTIFYNUMBER, INSUREDTYPE " + "FROM PRPCCUSTOMER WHERE POLICYNO=? AND INSUREDFLAG=?",
					new Object[] { prppMaincovernote.getPolicyno(), SystemUtils.CLIENT_RELATION_APPLICANT });
		}
		String applicantType = "";
		String applicantNo = "";
		String passportType = "";
		Object[] object = null;
		if (!Helper.isEmpty(prpPcustomer) && (object = prpPcustomer.get(0)) != null) {
			passportType = Helper.trim(object[0]);
			applicantNo = Helper.trim(object[1]);
			applicantType = Helper.trim(object[2]);
		} else {
			object = prpCcustomer.get(0);
			passportType = Helper.trim(object[0]);
			applicantNo = Helper.trim(object[1]);
			applicantType = Helper.trim(object[2]);
		}
		// 保存被保险人
		String insuredName = getCovernoteInsured(endorseNo, prppMaincovernote.getPolicyno(), SystemUtils.DOCUMENT_TYPE_ENDOR);
		document.setInsured(insuredName);
		// 投保人类型
		document.setApplicantType(applicantType);
		// 证件类型
		document.setApplicantPassportType(passportType);
		// 证件号码
		document.setApplicantPassportNo(applicantNo);
		// 代理人编码
		document.setAgentNo(prppMaincovernote.getAgentcode());
		document.setAgentName(prppMaincovernote.getAgentname());
		// 出单员
		// User user = userDao.get(prppMaincovernote.getOperatorcode());
		User user = userDao.get(String.valueOf(results[2]));
		document.setSales(user);
		// document.setSalesTime(prppMaincovernote.getOperatedate());
		if (salesTime == null) salesTime = prppMaincovernote.getOperatedate();
		document.setSalesTime(salesTime);

		// 业务关系代码
		document.setBusinessNo(prppMaincovernote.getAgreementno());
		// 内部业务员
		user = userDao.get(prppMaincovernote.getHandler1code());
		document.setBusinessUser(user);

		Company dept = companyDao.get(prppMaincovernote.getComcode());
		// 业务部门
		document.setBusinessDept(dept);
		// Company company = CompanyService.getCompany(dept);
		document.setBusinessCompany(CompanyService.getCompany(dept));

		// document.setEffectiveTime(DateHelper.set(prppMaincovernote.getStartdate(), Calendar.HOUR_OF_DAY,
		// NumberHelper.intValue(prppMaincovernote.getStarthour())));

		Date effectiveTime = (Date) results[3];
		effectiveTime = DateHelper.set(effectiveTime, Calendar.HOUR_OF_DAY, NumberHelper.intValue(results[4]));
		document.setEffectiveTime(effectiveTime);

		Date date = document.getEffectiveTime();
		if (date.before(document.getSalesTime())) date = document.getSalesTime();
		// 承保年度,按照生效日期开始算,如果倒签,则按照核保日期算
		document.setYear(DateHelper.getYearByDate(date));

		// 存差缺文字
		document.setLacks(SystemUtils.LACK_MESSAGE);
		// 数据生成时间
		// document.setCreateTime(new Date());
		// 保存单证
		dao.saveOrUpdate(document);
		// }
		saveDocumntFiles(document);
		return document;
	}

	/**
	 * 得到保险人名称,如果有多个最大返回500字节长度的字符串
	 * 
	 * @param no 大保单保单或者大保单批单
	 * @param type 8 ：保单类型 7：批单类型
	 * @param policyNo 保单号
	 * @return 组合在一起的保险人名称,多个之间通过','隔开的
	 */
	protected String getCovernoteInsured(String no, String policyNo, String type) {
		StringBuffer insuredName = new StringBuffer();
		StringBuffer endorse = new StringBuffer();
		StringBuffer policy = new StringBuffer();
		endorse.append("SELECT INSUREDNAME FROM PRPPCUSTOMER WHERE ENDORSENO=? AND INSUREDFLAG=?");
		policy.append("SELECT INSUREDNAME FROM PRPCCUSTOMER WHERE POLICYNO=? AND INSUREDFLAG=?");
		List<String> insuredNames = null;
		insuredNames = (List<String>) dao.querySQL(endorse.toString(), new Object[] { no, SystemUtils.CLIENT_RELATION_INSURED });
		if (Helper.isEmpty(insuredNames)) {
			insuredNames = (List<String>) dao.querySQL(policy.toString(),
					new Object[] { SystemUtils.DOCUMENT_TYPE_ENDOR.equals(type) ? policyNo : no, SystemUtils.CLIENT_RELATION_INSURED });
		}
		if (!Helper.isEmpty(insuredNames)) {
			for (int i = 0; i < insuredNames.size(); i++) {
				insuredName.append(insuredNames.get(i)).append(",");
			}
		}
		insuredName.deleteCharAt(insuredName.lastIndexOf(","));
		return StringHelper.substringLeft(insuredName.toString(), 500);
	}

	/**
	 * 对于是9999或者9997的险种的保单做特殊处理。保存eFiling系统数据
	 * 
	 * @param policyNo
	 *            大保单号
	 * @return
	 */
	public Document saveCovernotePolicy(String policyNo) {
		PrpcMaincovernote prpcMaincovernote = prpcMaincovernoteDao.get(policyNo);
		Document document = getDocument(policyNo);

		// 判断是否有效
		String status = SystemUtils.STATUS_INVALID;
		if (SystemUtils.UNDER_FLAG_YES_1.equals(prpcMaincovernote.getUnderwriteflag())
				|| SystemUtils.UNDER_FLAG_YES_3.equals(prpcMaincovernote.getUnderwriteflag())) {
			status = SystemUtils.STATUS_VALID;
		}
		Date salesTime = prpcMaincovernote.getUnderwriteenddate(); // 出单日期
		if (document == null) {
			document = new Document();
			document.setId(policyNo);
		}

		document.setType(SystemUtils.DOCUMENT_TYPE_POLICY);// 单证类型：保单
		document.setNo(prpcMaincovernote.getId());
		document.setProposalNo(prpcMaincovernote.getProposalno());
		document.setPolicyNo(prpcMaincovernote.getId());
		// 保单来源 第一位0/1--内网/外网；第二位1--批量投保；第3-4位 01-电子商务 02-意时网 03-蒙代尔
		// 04-网站;第10为1--货运险电子申报单
		String source = StringHelper.trim(prpcMaincovernote.getPolisource());
		if (source.length() >= 4 && "00".equals(source.substring(2, 4))) {
			// 核心系统
			document.setSource(SystemUtils.DOCUMENT_SOURCE_CORE);
		} else {
			// 电子商务
			document.setSource(SystemUtils.DOCUMENT_SOURCE_B2C);
		}
		document.setStatus(status);
		document.setFileStatus(SystemUtils.DOCUMENT_STATUS_UNFILE);
		document.setLent(SystemUtils.FILE_LENT_NO);
		document.setRiskType(SystemUtils.RISK_TYPE_NON_AUTO);
		document.setRiskClass(prpcMaincovernote.getClasscode());
		document.setRiskCode(prpcMaincovernote.getRiskcode());
		document.setEffectiveTime(DateHelper.set(prpcMaincovernote.getStartdate(), Calendar.HOUR_OF_DAY,
				NumberHelper.intValue(prpcMaincovernote.getStarthour())));
		document.setDueTime(DateHelper.set(prpcMaincovernote.getEnddate(), Calendar.HOUR_OF_DAY,
				NumberHelper.intValue(prpcMaincovernote.getEndhour())));
		// 产品线
		document.setProduct(this.getProduct(prpcMaincovernote.getRiskcode()));
		// // 承保年度
		// document.setYear(DateHelper.getYearByDate(prpcMaincovernote.getOperatedate()));

		document.setApplicant(prpcMaincovernote.getAppliname());

		List<PrpcCustomer> prpcCustomers = prpcCustomerDao
				.queryByProperty(new Object[][] { { "id.policyno", policyNo }, { "insuredFlag", SystemUtils.CLIENT_RELATION_INSURED } });
		PrpcCustomer prpcCustomer = null;

		if (!Helper.isEmpty(prpcCustomers)) {
			prpcCustomer = prpcCustomers.get(0);
			document.setApplicantType(prpcCustomer.getInsuredtype());
			// 证件类型
			document.setApplicantPassportType(prpcCustomer.getIdentifytype());
			// 证件号码
			document.setApplicantPassportNo(prpcCustomer.getIdentifynumber());
		}
		// 代理人编码
		document.setAgentNo(prpcMaincovernote.getAgentcode());
		document.setAgentName(prpcMaincovernote.getAgentname());
		// 出单员
		User user = userDao.get(prpcMaincovernote.getOperatorcode());
		document.setSales(user);

		if (salesTime == null) salesTime = prpcMaincovernote.getOperatedate();
		document.setSalesTime(salesTime);

		Date date = document.getEffectiveTime();
		if (date.before(document.getSalesTime())) date = document.getSalesTime();
		// 承保年度,按照生效日期开始算,如果倒签,则按照核保日期算
		document.setYear(DateHelper.getYearByDate(date));

		// 业务关系代码
		document.setBusinessNo(prpcMaincovernote.getAgreementno());
		// 内部业务员
		user = userDao.get(prpcMaincovernote.getHandler1code());
		document.setBusinessUser(user);

		// 业务部门
		Company dept = companyDao.get(prpcMaincovernote.getComcode());
		// 业务部门
		document.setBusinessDept(dept);
		document.setBusinessCompany(CompanyService.getCompany(dept));

		// 存差缺文字
		document.setLacks(SystemUtils.LACK_MESSAGE);
		// 数据生成时间
		// document.setCreateTime(new Date());
		// 保存被保险人
		String insuredName = getCovernoteInsured(prpcMaincovernote.getId(), prpcMaincovernote.getId(), SystemUtils.DOCUMENT_TYPE_POLICY);
		document.setInsured(insuredName);
		// }

		// 保存单证
		dao.saveOrUpdate(document);
		// 查询是否经过人工核保,T_DOCUMENT_FILE已经有了档案资料数据
		saveDocumntFiles(document);
		return document;
	}

	public static final String CHECK_COMMIT_SQL = SystemHelper.getProperty("sql.check.commit");

	/**
	 * 这个方法针对承保系统某个业务部门下的业务关系代码.
	 * 承保提交核保时，需要校验是否有业务关系代码超期尚未提交资料。
	 * 注意，因为签章资料的差缺不控制，所以这里的单号可能会少于差缺报表的数量
	 * 
	 * @param businessNo 业务关系代码
	 */
	public List<String> checkCommitUndwrt(String businessNo) {
		// String checkSql = SystemHelper.getProperty("sql.check.commit");
		// 规则: 不校验没有生成保单的投保单的业务关系代码超期
		Object[] parameters = new Object[] { SystemUtils.FILE_DEADLINE_DEFAULT, // 默认天数
				SystemUtils.DOCUMENT_STATUS_FILE, // 归档齐全
				SystemUtils.DOCUMENT_STATUS_UNFILE, // 未归档
				SystemUtils.DOCUMENT_STATUS_LACK, // 差缺
				SystemUtils.STATUS_VALID, // 只查询有效状态的数据
				// SystemUtils.DOCUMENT_TYPE_POLICY, // 保单类型
				// SystemUtils.DOCUMENT_TYPE_ENDOR, // 批单类型
				SystemUtils.YES, // 1表示原件资料,原件资料不判断业务关系代码是否超期
				businessNo.trim(), // 业务关系代码
				// SystemUtils.FILE_TYPE_NOT_SHARE_CODES[0], // 其他
				// SystemUtils.FILE_TYPE_NOT_SHARE_CODES[1], // 其他1
				// SystemUtils.FILE_TYPE_NOT_SHARE_CODES[2], // 其他2
				// SystemUtils.FILE_TYPE_NOT_SHARE_CODES[3], // 其他3
				// SystemUtils.FILE_TYPE_NOT_SHARE_CODES[4], // 其他4
				// SystemUtils.FILE_TYPE_NOT_SHARE_CODES[5], // 其他5
				// SystemUtils.FILE_TYPE_CODE_PROPOSAL, //投保单
				// SystemUtils.DOCUMENT_FILE_STATUS_LACK // 未归档
				SystemUtils.DOCUMENT_STATUS_FILE, SystemUtils.DOCUMENT_STATUS_UNFILE, SystemUtils.DOCUMENT_STATUS_UNFILE };
		return (List<String>) dao.querySQL(CHECK_COMMIT_SQL, parameters);
	}

	/**
	 * 根据业务号查询对应的documentFile对象。主要针对承保系统的单证信息链接到eFiling系统
	 * 
	 * @param no 业务号：投保单号,保单号,批单号
	 * @return 对应业务号下的所有的档案资料
	 */
	public List<DocumentFile> queryDocumentFiles(String no) {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(DocumentFile.class.getName()).append(" d ");
		hql.append(" WHERE d.document.no=?");
		List<DocumentFile> documentFiles = (List<DocumentFile>) documentFileDao.query(hql.toString(), new Object[] { no });
		return documentFiles;
	}

	/** 投保人资料到期日如果为null,则默认为最大化的一个日期2024-12-31 */
	public static final Date DEFAULT_MAX_DUE_TIME = DateHelper.parse("2024-12-31 23:59:59");

	/**
	 * 设置行驶证的最大日期
	 */
	public static final Date DEFAULT_DRIVING_MAX_TIME = DateHelper.parse("2099-12-31 23:59:59");

	/**
	 * 根据单证的prpDUwmtypes核保资料创建DocumentFile资料对象,如果是投保人/被保人资料,可能会有多个
	 * 
	 * @param document 单证对象
	 * @param prpDUwmtypes 核保资料
	 * @return 创建的DocumentFile对象列表，此时尚未存入数据库
	 */
	public Set<DocumentFile> createDocumntFiles(Document document, PrpDUwmtypes prpDUwmtypes) {
		final String fileTypeCode = prpDUwmtypes.getId().getType();
		FileType fileType = fileTypeDao.getByCode(fileTypeCode);
		System.out.println("fileType=" + fileType);
		// if (SystemUtils.FILE_TYPE_INSURED.equals(fileTypeCode)) { // 2014-10-14上线用,上线后删除
		// // 被保人资料
		// fileType = fileTypeDao.get(SystemUtils.FILE_TYPE_INSURED);
		// }
		if (fileType == null) return null; // 可能是老数据,没有的资料类型则不保存
		// final String no = document.getNo(); // 业务号
		final String type = document.getType(); // 业务类型
		String prpNo = document.getNo();
		Set<DocumentFile> documentFiles = new HashSet<DocumentFile>();

		Date dueTime = null; // 资料失效日期
		String paperType = null; // 资料类型
		String paperCode = null; // 资料代码. 资料类型 + 资料代码一致的视为同一资料，可以共享

		if (Helper.contains(SystemUtils.FILE_TYPE_CLIENT_CODES, fileTypeCode)
				|| Helper.contains(SystemUtils.FILE_TYPE_UNIT_CODES, fileTypeCode)
				|| Helper.contains(SystemUtils.FILE_TYPE_CAR_OWNER_CODES, fileTypeCode)) {
			System.out.println("fileTypeCode=" + fileTypeCode);
			// if (SystemUtils.FILE_TYPE_APPLICANT.equals(fileTypeCode)) {
			// // 如果需要201,表示投保人和被保人两种资料.先加入被保人资料
			// prpDUwmtypes.getId().setType(SystemUtils.FILE_TYPE_INSURED);
			// documentFiles = createDocumntFiles(document, prpDUwmtypes);
			// }

			// 客户资料信息,投保人或被保人身份证
			// if (SystemUtils.FILE_TYPE_APPLICANT.equals(fileTypeCode)) {
			// // 投保人资料类型
			// fileType = fileTypeDao.getByCode(SystemUtils.FILE_TYPE_APPLICANT);
			// }
			// 投保人、被保人的资料类型，如身份证/组织机构代码、税务登记证、营业执照
			String sql = null;
			List<Object[]> clients = new ArrayList<Object[]>(); // 查询到的客户信息
			if (Helper.contains(SystemUtils.FILE_TYPE_CAR_OWNER_CODES, fileTypeCode)) {
				// 查询车主信息
				if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(type)) {
					// 投保单
					sql = SystemHelper.getProperty("sql.queryCarOwner.proposal");
				} else if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(type)) {
					// 保单
					sql = SystemHelper.getProperty("sql.queryCarOwner.policy");
				} else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(type)) {
					// 批单,通过保单号到cp表查询最新的数据
					prpNo = document.getPolicyNo();
					sql = SystemHelper.getProperty("sql.queryCarOwner.endor");
				}
				List<Object[]> c = (List<Object[]>) dao.querySQL(sql, new Object[] { prpNo });
				if (c != null) clients.addAll(c);
			} else {
				if (SystemUtils.CLASS_CODE_COVER_NOTE.equalsIgnoreCase(document.getRiskClass())) {
					// 大保单,注意大保单没有投保单
					if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(type) || SystemUtils.DOCUMENT_TYPE_POLICY.equals(type)) {
						// 投保单/保单
						sql = SystemHelper.getProperty("sql.queryClient.proposal.converNote");
					} else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(type)) {
						// 批单
						prpNo = document.getPolicyNo();
						sql = SystemHelper.getProperty("sql.queryClient.endor.converNote");
					}
				} else if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(type)) {
					// 投保单
					sql = SystemHelper.getProperty("sql.queryClient.proposal");
				} else if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(type)) {
					// 保单
					sql = SystemHelper.getProperty("sql.queryClient.policy");
				} else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(type)) {
					// 批单,通过保单号到cp表查询最新的数据
					prpNo = document.getPolicyNo();
					sql = SystemHelper.getProperty("sql.queryClient.endor");
				}

				if (Helper.contains(SystemUtils.FILE_TYPE_APPLICANT_CODES, fileTypeCode)) {
					// 投保人的身份证/组织机构代码等
					List<Object[]> c = (List<Object[]>) dao.querySQL(sql, new Object[] { prpNo, SystemUtils.CLIENT_RELATION_APPLICANT });
					if (c != null) clients.addAll(c);
				}

				if (Helper.contains(SystemUtils.FILE_TYPE_INSURED_CODES, fileTypeCode)) {
					// 被保人的身份证/组织机构代码等.
					List<Object[]> c = (List<Object[]>) dao.querySQL(sql, new Object[] { prpNo, SystemUtils.CLIENT_RELATION_INSURED });
					if (c != null) clients.addAll(c);
				}
				
				if (Helper.contains(SystemUtils.FILE_TYPE_AGENT_APPLICANT_CODES, fileTypeCode)) {
					//投保人实际控制人或法定代理人或负责人身份证件
					List<Object[]> c = (List<Object[]>) dao.querySQL(sql, new Object[] { prpNo, SystemUtils.CLIENT_RELATION_APPLICANT });
					if (c != null) clients.addAll(c);
				}
				
				if (Helper.contains(SystemUtils.FILE_TYPE_AGENT_INSURED_CODES, fileTypeCode)) {
					// 被保险人实际控制人或法定代理人或负责人身份证件
					List<Object[]> c = (List<Object[]>) dao.querySQL(sql, new Object[] { prpNo, SystemUtils.CLIENT_RELATION_INSURED });
					if (c != null) clients.addAll(c);
				}
			}

			// if (Helper.contains(SystemUtils.FILE_TYPE_UNIT_CODES, fileTypeCode)) {
			// // 如果是250 营业执照复印件或251 税务登记证复印件,优先取被保人的资料
			// List<Object[]> c = (List<Object[]>) dao.querySQL(sql + " AND T.INSUREDTYPE = ?",
			// new Object[] { prpNo, SystemUtils.CLIENT_RELATION_APPLICANT, SystemUtils.CLIENT_TYPE_UNIT });
			// // if (c == null || c.isEmpty()) {
			// // // 可能被保人不是机构，投保人是机构的情况
			// // c = (List<Object[]>) dao.querySQL(sql + " AND T.INSUREDTYPE = ?", new Object[] { prpNo,
			// // SystemUtils.CLIENT_RELATION_INSURED, SystemUtils.CLIENT_TYPE_UNIT });
			// // }
			// if (c != null) clients.addAll(c);
			// }

			// System.out.println("clientsSize=" + (clients.size()));

			/* 有可能有多个在录单的时候会录取多个投保资料,但是这种情况的概率一般很小. 如果有多少条投保人资料就在eFiling保存多少条 */
			String clientType; // 1:个人 2:单位
			// String clientCode; // 客户代码
			String identifyType; // 证件类型
			String identifyNumber; // 证件号码 2wsx3edc
			// String insuredType; // 1: 被保险人 2：投保人
			// FileType fileTypeInsured = null;
			// String fileTypeCodeCopy;

			// if (applicants != null) clients.addAll(applicants);
			// if (insureds != null) {
			// // 2014-08-07 罗刚 即便投保人和被保人是同一个人,还是必须要200(被保人身份证)和201(投保人身份证)两份资料,
			// // 否则如果只保留201,则有可能客户选择200进行上传,此时可能会出现required=0的现象
			// // if (applicants != null && applicants.size() == 1 && insureds.size() == 1) {
			// // // 判断投保人和被保人是否只有同一人,如果是同一人，则不添加被保人资料
			// // Object[] client = applicants.get(0);
			// // clientCode = StringHelper.trim(client[1]);
			// // identifyType = StringHelper.trim(client[2]);
			// // identifyNumber = StringHelper.trim(client[3]);
			// //
			// // client = insureds.get(0);
			// // if (clientCode.equals(StringHelper.trim(client[1])) // 数据库中是同一条数据
			// // || (identifyType.equalsIgnoreCase(StringHelper.trim(client[2])) // 客户的资料类型和资料代码一致则视为同一客户
			// // && identifyNumber.equalsIgnoreCase(StringHelper.trim(client[3])))) {
			// // // 投保人和被保人是同一人则不对被保人做处理
			// // insureds.clear();
			// // }
			// // }
			// clients.addAll(insureds);
			// }

			Iterator<Object[]> it = clients.iterator();
			List<Object[]> distinctClients = new ArrayList<Object[]>();
			while (it.hasNext()) {
				Object[] client = it.next();
				clientType = StringHelper.trim(client[0]);
				identifyType = StringHelper.trim(client[2]);
				identifyNumber = StringHelper.trim(client[3]);
				// if ("N/A".equalsIgnoreCase(identifyNumber) || identifyNumber.length() < 3) {
				// // 视为没有录入证件号
				// identifyNumber = "";
				// }
				if (SystemUtils.CLIENT_TYPE_PERSON.equals(clientType) && Helper.contains(SystemUtils.FILE_TYPE_UNIT_CODES, fileTypeCode)) {
					// 人员为个人,但资料类型为组织机构,则不需要这个client信息
					it.remove();
					continue;
				}

//				if (SystemUtils.CLIENT_TYPE_PERSON.equals(clientType)) {
//					// 承保系统的问题，为个人客户时，可以选择51表示组织结构代码证,因此需要切换为53.
//					if (SystemUtils.PAPER_TYPE_51.equals(identifyType)) {
//						identifyType = SystemUtils.PAPER_TYPE_53;
//						client[2] = identifyType;
//					}
//				}

				// if (distinctClients.isEmpty()) distinctClients.add(client);
				// else {
				boolean exists = false;

				for (Object[] cli : distinctClients) {
					exists = clientType.equals(StringHelper.trim(cli[0])) && identifyType.equals(StringHelper.trim(cli[2]))
							&& identifyNumber.equals(StringHelper.trim(cli[3]));
					if (exists) {
						// 有重复的client
						it.remove();
						break;
					}
				}

				if (!exists) distinctClients.add(client);
				// }
			}

			// System.out.println("clients=" + Arrays.toString(clients.get(0)));

			for (Object[] client : clients) {
				// fileTypeCodeCopy = fileTypeCode;
				clientType = StringHelper.trim(client[0]);
				// clientCode = StringHelper.trim(client[1]);
				identifyType = StringHelper.trim(client[2]);
				identifyNumber = StringHelper.trim(client[3]);
				if ("N/A".equalsIgnoreCase(identifyNumber) || identifyNumber.length() < 3) {
					// 视为没有录入证件号
					identifyNumber = "";
				}

				paperType = identifyType;
				paperCode = identifyNumber;

				// insuredType = StringHelper.trim(client[4]);
				dueTime = (Date) client[5];
				if (dueTime == null) {
					// 默认为2024-12-31日
					dueTime = DEFAULT_MAX_DUE_TIME;
				}

				DocumentFile documentFile = new DocumentFile();
				documentFile.setDocument(document);
				documentFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
				documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_LACK);

				documentFile.setFileTypeCode(fileTypeCode);
				documentFile.setFileType(fileType);

				// if (SystemUtils.FILE_TYPE_APPLICANT.equals(fileTypeCode)) {
				// // 承保系统中将投保人/被保人合并为了一个，eFiling中需要单独处理,扫描的时候才能区分是投保人还是被保人的资料
				// // 被保人身份证/组织机构代码证的资料编号为200，与承保代码不会冲突
				// if (SystemUtils.CLIENT_RELATION_INSURED.equals(insuredType)) {
				// fileTypeCodeCopy = SystemUtils.FILE_TYPE_INSURED;
				// if (fileTypeInsured == null) fileTypeInsured = fileTypeDao.get(fileTypeCodeCopy);
				// documentFile.setFileType(fileTypeInsured);
				// }
				// }
				//
				// if (SystemUtils.FILE_TYPE_APPLICANT.equals(fileTypeCodeCopy)) {
				// // 201.投保人
				// if (applicants != null && applicants.size() > 1) {
				// // 多个投保人，不进行资料共享
				// paperType = null;
				// paperCode = null;
				// dueTime = null;
				// }
				// } else if (SystemUtils.FILE_TYPE_INSURED.equals(fileTypeCodeCopy)) {
				// // 200.被保人
				// if (insureds != null && insureds.size() > 1) {
				// // 多个被保人，不进行资料共享
				// paperType = null;
				// paperCode = null;
				// dueTime = null;
				// }
				// }
				// if (SystemUtils.CLIENT_TYPE_PERSON.equals(clientType)) {
				// // 承保系统的问题，为个人客户时，可以选择51表示组织结构代码证,因此需要切换为53.
				// if (SystemUtils.PAPER_TYPE_51.equals(paperType)) {
				// paperType = SystemUtils.PAPER_TYPE_53;
				// }
				// }
				if (clients.size() > 1) {
					// 多个人，不进行资料共享
					paperType = null;
					paperCode = null;
					dueTime = null;
				}
				documentFile.setPaperType(paperType);
				documentFile.setPaperCode(paperCode);
				documentFile.setDueTime(dueTime);

				// 设置资料类型
				documentFiles.add(documentFile);

				// if (Helper.contains(SystemUtils.FILE_TYPE_UNIT_CODES, fileTypeCode)) {
				// // 单位相关资料,只需要一份
				// break;
				// }
			}
		} else if (Helper.contains(SystemUtils.FILE_TYPE_VEHICLE_CODES, fileTypeCode)) {
			// 车辆相关证件的处理
			// 204--行驶证或机动车登记证书
			// 202--车辆合格证或购车发票
			// 258--货物进出口证明书/购车发票（进口)
			String sql = null;
			if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(type)) {
				// 投保单
				sql = SystemHelper.getProperty("sql.queryVehicle.proposal");
			} else if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(type)) {
				// 保单
				sql = SystemHelper.getProperty("sql.queryVehicle.policy");
			} else if (SystemUtils.DOCUMENT_TYPE_ENDOR.equals(type)) {
				// 批单,通过保单号在CP表查询
				prpNo = document.getPolicyNo();
				sql = SystemHelper.getProperty("sql.queryVehicle.endor");
			}
			// String useNature = ""; // 使用性质 8开头的表示:非营业 9开头的表示营业
			// String vehicleType = ""; // 交管车辆种类
			// Date enrolldate = null; // 初登日期
			List<Object[]> vehicles = (List<Object[]>) dao.querySQL(sql, new Object[] { prpNo });
			String vinNo;
			String engineNo;
			for (Object[] vehicle : vehicles) {
				dueTime = null;

				vinNo = StringHelper.trim(vehicle[3]);
				engineNo = StringHelper.trim(vehicle[4]);
				if (vehicles.size() == 1) {
					// 仅有一辆车时，才会写入车辆的发动机号和VIN码，多个车辆也不共享行驶证
					paperType = vinNo;
					paperCode = engineNo;
					// 2014-10-27行驶证不过期
					// if (SystemUtils.FILE_TYPE_VEHICLE_LICENSE.equals(fileTypeCode)) {
					// // 行驶证的到期日
					// dueTime = DocumentAuditHelper.getDueDate(useNature, vehicleType, enrolldate);
					// }
				}
				DocumentFile documentFile = new DocumentFile();
				documentFile.setDocument(document);
				documentFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
				documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_LACK);

				documentFile.setFileTypeCode(fileTypeCode);
				documentFile.setFileType(fileType);
				documentFile.setPaperType(paperType);
				documentFile.setPaperCode(paperCode);
				documentFile.setDueTime(DEFAULT_DRIVING_MAX_TIME); // 行驶证证件日期为很大视为有效,只要交了一次的，都视为无限期有效
				documentFiles.add(documentFile);
			}
		} else {
			// 其他资料
			DocumentFile documentFile = new DocumentFile();
			documentFile.setDocument(document);
			documentFile.setFileModel(SystemUtils.FILE_MODEL_FILE);
			documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_LACK);

			documentFile.setFileTypeCode(fileTypeCode);
			documentFile.setFileType(fileType);
			documentFile.setPaperType(paperType);
			documentFile.setPaperCode(paperCode);
			documentFile.setDueTime(dueTime);
			documentFiles.add(documentFile);
		}

		return documentFiles;
	}

	/**
	 * 投保单/保单/批单document保存DocumntFile表的数据
	 * 
	 * @param document Document表对象
	 * @return
	 */
	public Set<DocumentFile> saveDocumntFiles(Document document) {
		// 注意保单需要的资料类型仅能通过投保单号/批单号查,所以使用id查询
		List<PrpDUwmtypes> prpDUwmtypesList = null;
		Date salesTime = document.getSalesTime();
		if (salesTime == null || SystemUtils.SYSTEM_GO_LIVE_TIME.before(salesTime)) {
			// 如果不是上线之前的数据则查询其资料类型,否则如果是上线之前的数据则不迁移任何数据
			prpDUwmtypesList = queryNeedDocumentFiles(document.getId());
		}
		List<String> fileIds = new ArrayList<String>();
		Set<DocumentFile> files = new LinkedHashSet<DocumentFile>();
		if (!Helper.isEmpty(prpDUwmtypesList)) {

			StringBuilder buf = new StringBuilder(document.getNo());
			buf.append("从承保数据库查询到的承保资料：");

			Set<DocumentFile> documentFiles = new HashSet<DocumentFile>();
			Set<DocumentFile> docFiles;
			DocumentFile documentFile = null;
			int i = 0;
			for (PrpDUwmtypes prpDUwmtypes : prpDUwmtypesList) {
				buf.append("\n\t").append(i++).append(prpDUwmtypes.getId().getType()).append(" - ").append(prpDUwmtypes.getTypEName());
				docFiles = createDocumntFiles(document, prpDUwmtypes);
				buf.append(docFiles);
				buf.append((docFiles == null) + "," + (docFiles != null && !docFiles.isEmpty()));
				if (docFiles != null && !docFiles.isEmpty()) {
					documentFiles.addAll(docFiles);
				}
			}

			System.out.println(buf);
			logger.info(buf.toString());

			String status;
			// String paperType;
			// String paperCode;
			// String newPaperType;
			// String newPaperCode;
			Set<String> existsIds = new HashSet<String>(); // 已存在的id
			for (DocumentFile docFile : documentFiles) {
				// 判断是否有此资料.如果有多个被保人,则会只保存一行数据
				System.out.println(docFile.getFileType().getId());
				logger.info(docFile.getFileType().getId());
				documentFile = getDocumentFile(document, docFile.getFileType().getId(), existsIds);
				System.out.println(documentFile);
				logger.info(documentFile);
				if (documentFile == null) {
					// 没有相同的资料
					documentFile = docFile;
					documentFileDao.save(documentFile);
				} else {
					// 有相同的资料
					status = documentFile.getStatus();
					if (SystemUtils.DOCUMENT_FILE_STATUS_LACK.equals(status)) {
						// 差缺的资料,更新资料信息
						Helper.copyValues(docFile, documentFile, new String[] { "id", "status", "replaced", "shared" });
						documentFileDao.update(documentFile);
					} else if (SystemUtils.DOCUMENT_FILE_STATUS_FILE.equals(status)) {
						// 因为如果同一类型有多个资料，则paperCode，paperType均为null,始终视为同一资料
						// 如果同一类型只有单一资料，就视为修改数据，附件没问题
						// 已归档的文件,判断是否为同一份文件,主要根据paperType和paperCode判断
						// paperType = documentFile.getPaperType();
						// paperCode = documentFile.getPaperCode();

						// newPaperType = docFile.getPaperType();
						// newPaperCode = docFile.getPaperCode();
						// if ((paperType == newPaperType || (paperType != null && paperType.equals(newPaperType)))
						// && (paperCode == newPaperCode || (paperCode != null && paperCode.equals(newPaperCode)))) {
						// 同一份资料
						Helper.copyValues(docFile, documentFile, new String[] { "id", "file", "status", "replaced", "shared" });
						documentFileDao.update(documentFile);
						// } else {
						// // 资料代码不一样,可能是更改了车辆信息,或者投保人/被保人信息,禁用原来的文件
						// documentFileDao.save(docFile);
						//
						// documentFile.setStatus(SystemUtils.DOCUMENT_FILE_STATUS_DISABLED);
						// documentFile.setAnother(docFile);
						// documentFileDao.update(documentFile);
						//
						// documentFile = docFile;
						// }
					} else if (SystemUtils.DOCUMENT_FILE_STATUS_DISABLED.equals(status)) {
						// 原来的资料已禁用
						documentFileDao.save(docFile);
						documentFile.setAnother(docFile);
						documentFileDao.update(documentFile);
						documentFile = docFile;
					}

					existsIds.add(documentFile.getId());
				}
				files.add(documentFile);
				fileIds.add(documentFile.getId());
			}
		}

		// 删除多余的DocumentFile数据
		String hql = "DELETE FROM " + DocumentFile.class.getName() + " WHERE document = ? AND status = ? ";
		Object[] parameters = new Object[2 + fileIds.size()];
		parameters[0] = document;
		parameters[1] = SystemUtils.DOCUMENT_FILE_STATUS_LACK; // 已上传过资料的不删除,因此仅删除差缺的资料
		if (!fileIds.isEmpty()) {
			hql += "AND id NOT IN (?" + StringHelper.copy(", ?", fileIds.size() - 1) + ") ";
			System.arraycopy(fileIds.toArray(), 0, parameters, parameters.length - fileIds.size(), fileIds.size());
		}
		int count = dao.execute(hql, parameters);
		System.out.println("删除" + count + "条数据!");
		logger.info("删除" + count + "条数据!");

		document.setDocumentFiles(files);

		// 调用共享资料的方法
		share(document.getNo());

		changeDocumentFileStatus(document, null);

		return files;
	}

	protected PrpTMainDao prpTMainDao;
	protected PrpCMainDao prpCMainDao;
	protected PrpPMainDao prpPMainDao;
	protected ProductDao productDao;
	protected ProductRiskDao productRiskDao;
	protected PrpTInsuredDao prpTInsuredDao;
	protected PrpCInsuredDao prpCInsuredDao;
	protected PrpPInsuredDao prpPInsuredDao;
	protected PrpTMainsubDao prpTMainsubDao;
	protected PrpCMainsubDao prpCMainsubDao;
	protected PrpPMainsubDao prpPMainsubDao;
	protected PrpTItemcarDao prpTItemcarDao;
	protected PrpCItemcarDao prpCItemcarDao;
	protected PrpPItemcarDao prpPItemcarDao;
	protected PrpDUwmtypesDao prpDUwmtypesDao;
	protected PrpDCoderiskDao prpDCoderiskDao;
	protected UserDao userDao;
	protected FileTypeDao fileTypeDao;
	protected PrpcMaincovernoteDao prpcMaincovernoteDao;
	protected PrppMaincovernoteDao prppMaincovernoteDao;
	protected PrpcCustomerDao prpcCustomerDao;
	protected PrppCustomerDao prppCustomerDao;
	protected PrptcarshiptaxDao prptcarshiptaxDao;
	


	public PrptcarshiptaxDao getPrptcarshiptaxDao() {
		return prptcarshiptaxDao;
	}

	public void setPrptcarshiptaxDao(PrptcarshiptaxDao prptcarshiptaxDao) {
		this.prptcarshiptaxDao = prptcarshiptaxDao;
	}

	public PrppCustomerDao getPrppCustomerDao() {
		return prppCustomerDao;
	}

	public void setPrppCustomerDao(PrppCustomerDao prppCustomerDao) {
		this.prppCustomerDao = prppCustomerDao;
	}

	public PrpcCustomerDao getPrpcCustomerDao() {
		return prpcCustomerDao;
	}

	public void setPrpcCustomerDao(PrpcCustomerDao prpcCustomerDao) {
		this.prpcCustomerDao = prpcCustomerDao;
	}

	public PrpcMaincovernoteDao getPrpcMaincovernoteDao() {
		return prpcMaincovernoteDao;
	}

	public void setPrpcMaincovernoteDao(PrpcMaincovernoteDao prpcMaincovernoteDao) {
		this.prpcMaincovernoteDao = prpcMaincovernoteDao;
	}

	public PrppMaincovernoteDao getPrppMaincovernoteDao() {
		return prppMaincovernoteDao;
	}

	public void setPrppMaincovernoteDao(PrppMaincovernoteDao prppMaincovernoteDao) {
		this.prppMaincovernoteDao = prppMaincovernoteDao;
	}

	public FileTypeDao getFileTypeDao() {
		return fileTypeDao;
	}

	public ProductRiskDao getProductRiskDao() {
		return productRiskDao;
	}

	public void setProductRiskDao(ProductRiskDao productRiskDao) {
		this.productRiskDao = productRiskDao;
	}

	public void setFileTypeDao(FileTypeDao fileTypeDao) {
		this.fileTypeDao = fileTypeDao;
	}

	public PrpDCoderiskDao getPrpDCoderiskDao() {
		return prpDCoderiskDao;
	}

	public void setPrpDCoderiskDao(PrpDCoderiskDao prpDCoderiskDao) {
		this.prpDCoderiskDao = prpDCoderiskDao;
	}

	public PrpDUwmtypesDao getPrpDUwmtypesDao() {
		return prpDUwmtypesDao;
	}

	public void setPrpDUwmtypesDao(PrpDUwmtypesDao prpDUwmtypesDao) {
		this.prpDUwmtypesDao = prpDUwmtypesDao;
	}

	public PrpTItemcarDao getPrpTItemcarDao() {
		return prpTItemcarDao;
	}

	public void setPrpTItemcarDao(PrpTItemcarDao prpTItemcarDao) {
		this.prpTItemcarDao = prpTItemcarDao;
	}

	public PrpCItemcarDao getPrpCItemcarDao() {
		return prpCItemcarDao;
	}

	public void setPrpCItemcarDao(PrpCItemcarDao prpCItemcarDao) {
		this.prpCItemcarDao = prpCItemcarDao;
	}

	public PrpPItemcarDao getPrpPItemcarDao() {
		return prpPItemcarDao;
	}

	public void setPrpPItemcarDao(PrpPItemcarDao prpPItemcarDao) {
		this.prpPItemcarDao = prpPItemcarDao;
	}

	public PrpCMainsubDao getPrpCMainsubDao() {
		return prpCMainsubDao;
	}

	public void setPrpCMainsubDao(PrpCMainsubDao prpCMainsubDao) {
		this.prpCMainsubDao = prpCMainsubDao;
	}

	public PrpPMainsubDao getPrpPMainsubDao() {
		return prpPMainsubDao;
	}

	public void setPrpPMainsubDao(PrpPMainsubDao prpPMainsubDao) {
		this.prpPMainsubDao = prpPMainsubDao;
	}

	public PrpPInsuredDao getPrpPInsuredDao() {
		return prpPInsuredDao;
	}

	public void setPrpPInsuredDao(PrpPInsuredDao prpPInsuredDao) {
		this.prpPInsuredDao = prpPInsuredDao;
	}

	public PrpCInsuredDao getPrpCInsuredDao() {
		return prpCInsuredDao;
	}

	public void setPrpCInsuredDao(PrpCInsuredDao prpCInsuredDao) {
		this.prpCInsuredDao = prpCInsuredDao;
	}

	public PrpCMainDao getPrpCMainDao() {
		return prpCMainDao;
	}

	public void setPrpCMainDao(PrpCMainDao prpCMainDao) {
		this.prpCMainDao = prpCMainDao;
	}

	public PrpPMainDao getPrpPMainDao() {
		return prpPMainDao;
	}

	public void setPrpPMainDao(PrpPMainDao prpPMainDao) {
		this.prpPMainDao = prpPMainDao;
	}

	public PrpTMainsubDao getPrpTMainsubDao() {
		return prpTMainsubDao;
	}

	public void setPrpTMainsubDao(PrpTMainsubDao prpTMainsubDao) {
		this.prpTMainsubDao = prpTMainsubDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public PrpTInsuredDao getPrpTInsuredDao() {
		return prpTInsuredDao;
	}

	public void setPrpTInsuredDao(PrpTInsuredDao prpTInsuredDao) {
		this.prpTInsuredDao = prpTInsuredDao;
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	public PrpTMainDao getPrpTMainDao() {
		return prpTMainDao;
	}

	public void setPrpTMainDao(PrpTMainDao prpTMainDao) {
		this.prpTMainDao = prpTMainDao;
	}

}
