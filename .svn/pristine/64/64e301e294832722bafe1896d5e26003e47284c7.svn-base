package com.ecm.webservice;

import java.rmi.RemoteException;

import com.sinosoft.util.struts2.action.EntityActionSupport;

/**
 * 用于操作ECM的webservice的action
 * 
 * @author LuoGang
 * 
 */
@SuppressWarnings("rawtypes")
public class ECMWebServiceAction extends EntityActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8242150094321902652L;

	/**
	 * 读取XML
	 * 
	 * @return 得到的XML字符串
	 */
	public String read() {
		try {
			XMLOperation s = new XMLOperationStub(url);
			ReadXml readXml = new ReadXml();
			readXml.setBatchId(batchId);// ("20130614.000022");
			ReadXmlResponse response = s.readXml(readXml);
			getResponse().setContentType("text/xml"); // 设置为xml
			write(xml = response.getOut());
		} catch (RemoteException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * 更新XML
	 * 
	 * @return
	 */
	public String update() {
		boolean success = false;
		try {
			XMLOperation s = new XMLOperationStub(url);
			UpdateXml updateXml = new UpdateXml();
			// String content = FileHelper.getText("C:\\Users\\LuoGang\\Desktop\\updateXml.txt");
			// xml.setIn0("20130614.000022");
			// xml.setIn1(content);
			updateXml.setBatchId(batchId);
			updateXml.setContent(xml);
			UpdateXmlResponse response = s.updateXml(updateXml);
			success = response.getOut();
		} catch (RemoteException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
		}
		// write(String.valueOf(success));
		write("{\"success\":" + String.valueOf(success) + "}");
		return null;
	}

	/**
	 * 取消上传
	 * 
	 * @return
	 */
	public String cancel() {
		boolean success = false;
		try {
			XMLOperation s = new XMLOperationStub(url);
			CancleBatch request = new CancleBatch();
			request.setBatchId(batchId);

			CancleBatchResponse response = s.cancleBatch(request);
			success = response.getOut();
		} catch (RemoteException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
		}
		write("{\"success\":" + String.valueOf(success) + "}");
		return null;
	}

	/** webservice服务端地址 */
	private String url;
	private String batchId;
	private String xml;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

}
