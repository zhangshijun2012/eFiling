package com.sinosoft.efiling.struts2.action;

import com.sinosoft.util.HttpHelper;
import com.sinosoft.util.struts2.action.EntityActionSupport;

/**
 * 跨域读取http内容的action
 * 
 * @author LuoGang
 * 
 */
@SuppressWarnings("rawtypes")
public class HttpReaderAction extends EntityActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6438375334953850755L;

	public String get() {
		super.getQueryParameters();
		String html = HttpHelper.get(url, parameters, charset);
		write(html);
		return null;
	}

	public String post() {
		String html = HttpHelper.post(url, parameters, charset);
		write(html);
		return null;
	}

	private String url;
	private String parameters;
	private String charset;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

}
