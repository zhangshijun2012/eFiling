package com.ecm.webservice;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

/**
 * 用于操作XML的webservice工具类
 * 
 * @author LuoGang
 * 
 */
public class ECMWebServiceHelper {
	protected static final Logger logger = Logger.getLogger(ECMWebServiceHelper.class);

	/**
	 * 读取XML
	 * 
	 * @param url webservice地址
	 * @param batchId 扫描批次号
	 * @return
	 */
	public static String read(String url, String batchId) {
		String xml = null;
		try {
			logger.debug("read webservice:" + url + ";batchId=" + batchId);
			XMLOperation s = new XMLOperationStub(url);
			ReadXml readXml = new ReadXml();
			readXml.setBatchId(batchId);// ("20130614.000022");
			ReadXmlResponse response = s.readXml(readXml);
			xml = response.getOut();
			logger.debug("read success:" + xml);
			// return xml;
		} catch (RemoteException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
			logger.error("read exception", e);
		}
		if (xml == null) xml = "<?xml version=\"1.0\" ?><root><exception code=\"not_found\"></exception></root>";

		return xml;
	}

	/**
	 * 更新XML
	 * 
	 * @param url webservice地址
	 * @param batchId 扫描批次号
	 * @param xml 更改后的XML
	 * 
	 * @return
	 */
	public static boolean update(String url, String batchId, String xml) {
		boolean success = false;
		try {
			// logger.debug("update webservice");
			logger.debug("update webservice:" + url + ";batchId=" + batchId + ";xml=" + xml);
			XMLOperation s = new XMLOperationStub(url);
			UpdateXml updateXml = new UpdateXml();
			// String content = FileHelper.getText("C:\\Users\\LuoGang\\Desktop\\updateXml.txt");
			// xml.setIn0("20130614.000022");
			// xml.setIn1(content);
			updateXml.setBatchId(batchId);
			updateXml.setContent(xml);
			UpdateXmlResponse response = s.updateXml(updateXml);
			success = response.getOut();
			logger.debug("update success:" + success);
		} catch (RemoteException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
			logger.error("update exception", e);
		}
		return success;
	}

	/**
	 * 取消上传
	 * 
	 * @param url webservice地址
	 * @param batchId 扫描批次号 *
	 * @return
	 */
	public static boolean cancel(String url, String batchId) {
		boolean success = false;
		try {
			// logger.debug("cancel webservice");
			logger.debug("cancel webservice:" + url + ";batchId=" + batchId);
			XMLOperation s = new XMLOperationStub(url);
			CancleBatch request = new CancleBatch();
			request.setBatchId(batchId);

			CancleBatchResponse response = s.cancleBatch(request);
			success = response.getOut();
			logger.debug("update success:" + success);
		} catch (RemoteException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
			logger.error("cancel exception", e);
		}
		return success;
	}

	public static void main(String[] args) {
		String xml = read("http://10.132.21.29:8080/XMLOperation/services/XMLOperation",
				"DE2CE730-5574-40DE-B691-B539ED0077C4");
		System.out.println(xml);
	}
}
