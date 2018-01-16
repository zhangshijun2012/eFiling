package com.ecm.webservice;

import java.rmi.RemoteException;

import com.sinosoft.util.FileHelper;

public class Test {
	/**
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <root>
	 * <exception code="not_found">异常信息</exception>
	 * </root>
	 * 
	 * @return
	 */
	public static String readXML() {
		try {
			XMLOperation s = new XMLOperationStub();
			ReadXml xml = new ReadXml();
			xml.setIn0("20130614.000022");
			ReadXmlResponse response = s.readXml(xml);
			return response.getOut();
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static boolean updateXML() {
		try {
			XMLOperation s = new XMLOperationStub();
			UpdateXml xml = new UpdateXml();
			String content = FileHelper.getText("C:\\Users\\LuoGang\\Desktop\\updateXml.txt");
			xml.setIn0("20130614.000022");
			xml.setIn1(content);
			UpdateXmlResponse response = s.updateXml(xml);
			return response.getOut();
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		String xml = String.valueOf(updateXML());
		System.out.println("----------------------------");
		System.out.println(xml);
	}
}
