package com.sinosoft.filenet;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.sinosoft.util.json.JSONObject;
import com.sinosoft.util.struts2.action.EntityActionSupport;

/**
 * 用于文件上传下载的Action
 * 
 * @author LuoGang
 * 
 */
public class FileIndexAction extends EntityActionSupport<FileIndex, FileIndexDao, FileIndexService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5528252747335427780L;

	/** 日志记录对象 */
	public static final Logger logger = LoggerFactory.getLogger(FileIndexAction.class);

	public String execute() throws Exception {
		String actionName = ActionContext.getContext().getActionInvocation().getProxy().getActionName();
		getRequest().setAttribute("actionName", actionName); // action.do

		JSONObject parameters = new JSONObject(getRequest().getParameterMap());
		getRequest().setAttribute("queryParameters", parameters.toJSONString()); // 所有参数
		return super.execute();
	}

}
