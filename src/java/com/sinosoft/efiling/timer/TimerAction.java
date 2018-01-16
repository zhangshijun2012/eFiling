package com.sinosoft.efiling.timer;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.sinosoft.efiling.struts2.action.DocumentAction;
import com.sinosoft.efiling.util.SpringUtils;
import com.sinosoft.util.HttpHelper;

/**
 * 定时任务的action,定时任务通过平台调度
 * 
 * @author LuoGang
 * 
 */
public class TimerAction extends DocumentAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7070616677799105303L;
	/** 日志记录对象 */
	public static final Logger logger = LoggerFactory.getLogger(TimerAction.class);
	/** 平台的定时任务参数名为className, 必须为TimerService的一个子类 */
	private String className;

	/**
	 * 执行某个定时任务,TimerService类名通过className参数传入
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public String run() {
		logger.info(className + "定时任务开始调度!" + HttpHelper.getClientIP(getRequest()));
		boolean success = true;
		try {
			TimerService service = null;
			try {
				service = (TimerService) SpringUtils.getBean(className);
			} catch (Exception ex) {
				Class<TimerService> cls = null;
				try {
					cls = (Class<TimerService>) Class.forName(className);
				} catch (ClassNotFoundException e) {
					// 如果className没有找到，则尝试在当前package下查找
					cls = (Class<TimerService>) Class.forName(getClass().getPackage().getName() + "." + className);
				}

				try {
					service = SpringUtils.getBean(cls);
				} catch (Exception e) {
					// 尝试使用className作为参数取得bean
					String name = cls.getSimpleName();
					try {
						String namex = Character.toLowerCase(name.charAt(0)) + name.substring(1);
						service = (TimerService) SpringUtils.getBean(namex);
					} catch (NoSuchBeanDefinitionException exc) {
						service = (TimerService) SpringUtils.getBean(name);
					}
				}
			}
			service.run();
			logger.info(className + "定时任务执行完成!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			success = false;
			logger.error(className + "定时任务执行异常!" + e.getMessage(), e);
		}
		write(Boolean.toString(success));
		return null;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
