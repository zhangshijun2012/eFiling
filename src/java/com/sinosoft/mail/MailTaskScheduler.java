package com.sinosoft.mail;

import java.util.Date;

/**
 * 邮件任务调度
 * 
 * @author 罗刚
 * 
 */
public class MailTaskScheduler {// implements SchedulerInterface {
	/** 任务执行对象 */
	private MailTask task;

	public void execute() {
		if (task == null) task = new MailTask();
		try {
			System.out.println(MailTask.format(new Date()) + "执行任务:" + this.getClass());
			task.run();
			System.out.println(MailTask.format(new Date()) + "任务完成:" + this.getClass());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(MailTask.format(new Date()) + "任务异常:" + e);
		}
	}

}
