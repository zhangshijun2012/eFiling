package com.sinosoft.efiling.service;

import java.util.Date;
import java.util.List;

import com.sinosoft.efiling.hibernate.dao.FileBoxDao;
import com.sinosoft.efiling.hibernate.dao.FileDao;
import com.sinosoft.efiling.hibernate.entity.File;
import com.sinosoft.efiling.hibernate.entity.FileBox;
import com.sinosoft.efiling.hibernate.entity.FileBoxVersion;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.efiling.util.UserSessionEntity;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.service.ServiceSupport;

public class FileBoxService extends ServiceSupport<FileBox, FileBoxDao> {
	FileBoxVersionService fileBoxVersionService;
	FileDao fileDao;

	/**
	 * 档案盒号由系统根据分支机构代码及当日日期自动生成，
	 * 规则：分支机构代码4位+险种1位（0车险/1非车险）+当前日期6位(YYMMDD)+盒号流水2位(01-99)；
	 * 
	 * @param branchId 分支机构代码
	 * @param riskType 险种
	 * @return 档案盒号
	 */
	public synchronized String makeFileBoxNo(String branchCode, String riskType) {
		String nowDate = DateHelper.format(new Date()).replace("-", "");
		/*
		 * Random random = new Random();
		 * Integer number = random.nextInt(100);
		 * String flowNumber = "0";
		 * 
		 * if (number < 10) {
		 * flowNumber += number.toString();
		 * } else {
		 * flowNumber = number.toString();
		 * }
		 */
		String fileBoxNo;
		Long flowNumber = NumberHelper.randomNumber(100);
		fileBoxNo = StringHelper.trim(branchCode).substring(0, 4) + StringHelper.trim(riskType) + nowDate
				+ flowNumber.toString();

		return fileBoxNo;
	}

	/**
	 * 产生一个档案盒
	 * 
	 * @param fileBoxVersion 档案盒版本号
	 * @param operator 系统操作人
	 * @param fileBoxNo 新产生的档案盒号
	 * @param company 分公司
	 * @return 档案盒
	 */
	public FileBox createFileBox(FileBoxVersion fileBoxVersion, UserSessionEntity User, String fileBoxNo) {
		FileBox fileBox = new FileBox();
		fileBox.setId(fileBoxNo);
		fileBox.setMaxCapacity(fileBoxVersion.getCapacity());
		fileBox.setUser(User);
		fileBox.setDepartment(User.getDepartment());
		fileBox.setCompany(User.getCurrentCompany());
		fileBox.setInsertTime(new Date());
		fileBox.setUpdateUser(User);
		fileBox.setUpdateDepartment(User.getDepartment());
		fileBox.setUpdateTime(new Date());
		dao.save(fileBox);
		return fileBox;
	}

	/**
	 * 
	 * @param fileBox 档案盒
	 * @param documentNum 对应的所有文件类型
	 * @return
	 */
	public boolean fullBox(FileBox fileBox, int fileCount) {
		int maxCapacity = fileBox.getMaxCapacity(); // 档案盒最大装盒数
		int capacity = fileBox.getCapacity(); // 档案盒现在已经装盒的数量
		return ((maxCapacity - capacity - fileCount) < 0);
	}

	/**
	 * 判断是否有当前部门指定的档案盒版本的档案盒
	 * 
	 * @param fileBoxVersion 档案盒版本
	 * @param riskType 险种类型
	 * @return 档案盒
	 */
	public FileBox haveFileBox(FileBoxVersion fileBoxVersion, String riskType) {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(FileBox.class.getName()).append(" f");
		hql.append(" WHERE f.company.id = ? AND f.riskType = ? AND f.status = ?");
		FileBox fileBox = (FileBox) dao.uniqueResult(hql.toString(), new Object[] {
				fileBoxVersion.getCompany().getId(), riskType, SystemUtils.FILE_BOX_STATUS_VALID });

		return fileBox;
	}

	/**
	 * @param fileBox 档案盒号
	 * @param batchNo 批次号
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public List<File> showBoxDetail(String boxNo, String batchNo) {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(File.class.getName()).append(" f ");
		hql.append(" WHERE f.fileBox.id = ? AND f.batchNo = ?");
		List<File> files = (List<File>) dao.query(hql.toString(), new Object[] { boxNo, batchNo });
		return files;
	}

	/**
	 * 得到当前正在使用的档案盒
	 * 
	 * @param companyId
	 * @param riskType
	 * @return
	 */
	public FileBox getCurrentFileBox(String companyId, String riskType) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM ").append(FileBox.class.getName()).append(" WHERE ");
		hql.append("company.id = ? AND riskType = ? AND status = ?");
		return (FileBox) dao.uniqueResult(hql.toString(), new Object[] { companyId, riskType,
				SystemUtils.FILE_BOX_STATUS_VALID });
	}

	/** 档案盒号中日期格式 */
	public static final String BOX_NO_DATE_PATTERN = "yyMMdd";

	/**
	 * 创建档案盒号前缀.
	 * 档案盒编码规则：分支机构代码4位+险种1位（0车险/1非车险）+当前日期6位(YYMMDD)+盒号流水2位(01-99)；
	 * 
	 * @param companyId
	 * @param riskType
	 * @return
	 */
	public static String createFileBoxIdPrefix(String companyId, String riskType) {
		String boxId = companyId.substring(0, 4) + riskType.charAt(0)
				+ DateHelper.format(new Date(), BOX_NO_DATE_PATTERN);
		return boxId;
	}

	/**
	 * 得到一个可用的档案盒.
	 * 档案盒编码规则：分支机构代码4位+险种1位（0车险/1非车险）+当前日期6位(YYMMDD)+盒号流水2位(01-99)；
	 * 
	 * @param companyId 公司代码
	 * @param riskType 险种类别(0车险/1非车险)
	 * @param count 档案页数
	 * @param user 操作人
	 * @return
	 */
	public synchronized FileBox getValidFileBox(String companyId, String riskType, int count) {
		FileBox fileBox = getCurrentFileBox(companyId, riskType);
		/* 当前正在使用的档案盒容量足够,则直接使用 */
		if (fileBox != null && fileBox.getMaxCapacity() - fileBox.getCapacity() >= count) {
			// fileBox.setCapacity(fileBox.getCapacity() + count);
			// fileBox.setUpdateUser(user.getInstance());
			// fileBox.setUpdateDepartment(user.getCurrentDepartment());
			// fileBox.setUpdateTime(new Date());
			// dao.update(fileBox);
			return fileBox;
		}
		// 规则：分支机构代码4位+险种1位（0车险/1非车险）+当前日期6位(YYMMDD)+盒号流水2位(01-99)；
		String boxId = createFileBoxIdPrefix(companyId, riskType);
		int index = 1;
		if (fileBox != null) {
			// 容量不足,无法使用,则使档案盒无效,重新建立档案盒
			fileBox.setStatus(SystemUtils.FILE_BOX_STATUS_INVALID);
			dao.update(fileBox);
			if (fileBox.getId().startsWith(boxId)) {
				index = NumberHelper.intValue(fileBox.getId().substring(boxId.length())) + 1;
			}
		}
		boxId = boxId + StringHelper.copy("0", 2 - String.valueOf(index).length()) + index;
		FileBoxVersion fileBoxVersion = fileBoxVersionService.getCurrentVersion(companyId);
		fileBox = new FileBox();
		fileBox.setId(boxId);
		fileBox.setMaxCapacity(fileBoxVersion.getCapacity());
		fileBox.setCapacity(0);
		fileBox.setCompany(companyDao.get(companyId));
		fileBox.setRiskType(riskType);

		fileBox.setStatus(SystemUtils.FILE_BOX_STATUS_VALID);
		// fileBox.setUser(user.getInstance());
		// fileBox.setDepartment(user.getCurrentDepartment());
		fileBox.setInsertTime(new Date());
		dao.save(fileBox);
		return fileBox;
	}

	/**
	 * 根据档案和和档案信息得到新的档案盒号,如果档案盒剩余容量足够,则返回自己
	 * 
	 * @param fileBox
	 * @param file
	 * @return
	 */
	public synchronized FileBox getValidFileBox(FileBox fileBox, File file) {
		int count = file.getPageCount();
		if (fileBox.getMaxCapacity() - fileBox.getCapacity() >= count) return fileBox; // 容量足够
		if (fileBox.getCapacity() <= 0) {
			// 容量不足,但是整个档案盒的可装数量就不能满足要装入的文件数量
			return fileBox;
		}
		// 容量不足,无法使用,则使档案盒无效,重新建立档案盒
		fileBox.setStatus(SystemUtils.FILE_BOX_STATUS_INVALID);
		dao.update(fileBox);
		String companyId = fileBox.getCompany().getId();
		String riskType = fileBox.getRiskType();
		// String boxId = companyId.substring(0, 4) + riskType + DateHelper.format(new Date(), BOX_NO_DATE_PATTERN);
		String boxId = createFileBoxIdPrefix(companyId, riskType);
		int index = 1;
		if (fileBox.getId().startsWith(boxId)) index = NumberHelper.intValue(fileBox.getId().substring(boxId.length())) + 1;
		boxId = boxId + StringHelper.copy("0", 2 - String.valueOf(index).length()) + index;
		FileBoxVersion fileBoxVersion = fileBoxVersionService.getCurrentVersion(fileBox.getCompany().getId());
		fileBox = new FileBox();
		fileBox.setId(boxId);
		fileBox.setMaxCapacity(fileBoxVersion.getCapacity());
		fileBox.setCapacity(0);
		fileBox.setCompany(fileBox.getCompany());
		fileBox.setRiskType(riskType);

		fileBox.setStatus(SystemUtils.FILE_BOX_STATUS_VALID);
		fileBox.setUser(file.getUser());
		fileBox.setDepartment(file.getDepartment());
		fileBox.setCompany(file.getCompany());
		fileBox.setInsertTime(file.getInsertTime());
		dao.save(fileBox);

		return fileBox;
	}

	/**
	 * 在档案盒中加入一个文件file
	 * 档案编码规则：档案盒号+3位流水号
	 * 
	 * @param fileBox
	 * @param file
	 * @return fileBox
	 */
	public FileBox save(FileBox fileBox, File file) {
		fileBox = getValidFileBox(fileBox, file);
		// 档案在档案盒中的页码,档案盒中原有的档案数量.从0开始
		file.setPageIndex(fileBox.getCapacity());

		fileBox.setCapacity(fileBox.getCapacity() + file.getPageCount());
		fileBox.setUpdateUser(file.getUser());
		fileBox.setUpdateDepartment(file.getDepartment());
		fileBox.setUpdateTime(file.getInsertTime());
		int fileCount = fileBox.getFileCount() + 1;
		fileBox.setFileCount(fileCount);

		if (fileBox.getUser() == null) { // 新建的档案盒
			fileBox.setUser(file.getUser());
			fileBox.setDepartment(file.getDepartment());
			fileBox.setCompany(file.getCompany());
			fileBox.setInsertTime(file.getInsertTime());
		}
		fileBox.setUpdateUser(file.getUser());
		fileBox.setUpdateDepartment(file.getDepartment());
		fileBox.setUpdateTime(file.getInsertTime());

		dao.update(fileBox);

		String fileNo = fileBox.getId() + StringHelper.copy(0, 3 - String.valueOf(fileCount).length()) + fileCount;
		file.setNo(fileNo);
		file.setFileBox(fileBox);
		fileDao.save(file);
		return fileBox;
	}

	public FileBoxVersionService getFileBoxVersionService() {
		return fileBoxVersionService;
	}

	public void setFileBoxVersionService(FileBoxVersionService fileBoxVersionService) {
		this.fileBoxVersionService = fileBoxVersionService;
	}

	public FileDao getFileDao() {
		return fileDao;
	}

	public void setFileDao(FileDao fileDao) {
		this.fileDao = fileDao;
	}
}
