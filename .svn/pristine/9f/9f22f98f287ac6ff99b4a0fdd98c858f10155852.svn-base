package com.sinosoft.efiling.service;

import java.util.Calendar;
import java.util.Date;

import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.Helper;

/**
 * 单证审核类用到的工具类
 * 
 * @author LuoGang
 * 
 */
public class DocumentAuditHelper {
	/**
	 * 得到行驶证的证件到期日.计算规则：
	 * <ol>
	 * <li>小型、微型非营运载客汽车6年以内每2年检验1次；超过6年的，每年检验1次；超过15年的，每年 检验2次。</li>
	 * <li>营运载客汽车5年以内每年检验1次；超过5年的，每6个月检验1次。</li>
	 * <li>载货汽车和大型、中型非营运载客汽车10年以内每年检验1次；超过10年的每6个月检验1次</li>
	 * <li>摩托车4年以内每2年检验1次；超过4年的，每年检验1次。</li>
	 * </ol>
	 * 
	 * @param useNature 使用性质 8开头的表示:非营业 9开头的表示营业
	 * @param vehicleType 交管车辆种类, M开头表示摩托车，H开头表示火车
	 * @param enrolldate 初登日期
	 * @return 根据规则算出来的到期日
	 */
	public static Date getDueDate(String useNature, String vehicleType, Date enrolldate) {
		if (useNature == null) return null;
		enrolldate = DateHelper.clear(enrolldate);
		int year = DateHelper.getYearByDate(enrolldate);
		Date nowDate = DateHelper.clear(new Date());
		int nowYear = DateHelper.getYear();
		int period = nowYear - year;
		Date dueDate = DateHelper.set(enrolldate, Calendar.YEAR, nowYear);
		if (dueDate.after(nowDate)) period--; // 初登日期与当前日期相差在一年以内
		dueDate = null; // 证件有效日期
		// 1) 小型、微型非营运载客汽车6年以内每2年检验1次；超过6年的，每年检验1次；超过15年的，每年 检验2次。
		// 2) 营运载客汽车5年以内每年检验1次；超过5年的，每6个月检验1次。
		// 3) 载货汽车和大型、中型非营运载客汽车10年以内每年检验1次；超过10年的每6个月检验1次
		// 4) 摩托车4年以内每2年检验1次；超过4年的，每年检验1次。；
		if (useNature.startsWith(SystemUtils.CAR_USENATURE_8)
				&& Helper.contains(SystemUtils.CAR_KIND_SMALL_BUS, vehicleType)) {
			// 小型,微型非营运载客汽车 客车以K字母开头
			// 1) 小型、微型非营运载客汽车6年以内每2年检验1次；超过6年的，每年检验1次；超过15年的，每年 检验2次。
			if (period < 6) {
				// 6年以内,每2年检验1次
				period = 2 * (int) Math.ceil((period + 1) / 2D);
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, period);
			} else if (period < 15) {
				// 6年到15年以内,每年检验1次
				period += 1;
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, period);
			} else {
				// 15年以后,每年 检验2次。
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, 15);
				while (nowDate.after(dueDate)) {
					dueDate = DateHelper.add(dueDate, Calendar.MONTH, 6);
				}
			}
		} else if (useNature.startsWith(SystemUtils.CAR_USENATURE_9)
				&& (Helper.contains(SystemUtils.CAR_KIND_SMALL_BUS, vehicleType)
						|| Helper.contains(SystemUtils.CAR_KIND_NEUTER_BUS, vehicleType) || Helper.contains(
						SystemUtils.CAR_KIND_LARGE_BUS, vehicleType))) {
			// 2) 营运载客汽车5年以内每年检验1次；超过5年的，每6个月检验1次。
			if (period < 5) {
				// 5年以内,每年检验1次
				period += 1;
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, period);
			} else {
				// 5年以后,每年 检验2次。
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, 5);
				while (nowDate.after(dueDate)) {
					dueDate = DateHelper.add(dueDate, Calendar.MONTH, 6);
				}
			}
		} else if (vehicleType.startsWith("H")
				|| (SystemUtils.CAR_USENATURE_8.equals(useNature) && (Helper.contains(SystemUtils.CAR_KIND_LARGE_BUS,
						vehicleType) || Helper.contains(SystemUtils.CAR_KIND_NEUTER_BUS, vehicleType)))) {
			// 3) 载货汽车和大型、中型非营运载客汽车10年以内每年检验1次；超过10年的每6个月检验1次
			if (period < 10) {
				// 10年以内,每年检验1次
				period += 1;
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, period);
			} else {
				// 10年以后,每年 检验2次。
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, 10);
				while (nowDate.after(dueDate)) {
					dueDate = DateHelper.add(dueDate, Calendar.MONTH, 6);
				}
			}
		} else if (vehicleType.startsWith("M")) {
			// 4) 摩托车4年以内每2年检验1次；超过4年的，每年检验1次。；
			if (period < 4) {
				// 4年以内,每2年检验1次
				period = 2 * (int) Math.ceil((period + 1) / 2D);
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, period);
			} else {
				// 4年后,每年检验1次
				period += 1;
				dueDate = DateHelper.add(enrolldate, Calendar.YEAR, period);
			}
		}
		return dueDate;
	}

}
