package com.sinosoft.util.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sinosoft.util.FileHelper;
import com.sinosoft.util.StringHelper;

/**
 * 用于读取excel
 * 
 * @author LuoGang
 * 
 */
public class POIReader extends POIObject {

	public POIReader() {
		super();
		// TODO 自动生成构造函数存根
	}

	public POIReader(String version, POIEventListener listener) {
		super(version, listener);
		// TODO 自动生成构造函数存根
	}

	public POIReader(String version) {
		super(version);
		// TODO 自动生成构造函数存根
	}

	/**
	 * 指定的数据类型
	 */
	protected Map<Object, Class> classes;

	/**
	 * 得到key对应的cellClass
	 * 
	 * @param key
	 * @return
	 */
	public Class getCellClass(Object key) {
		return classes == null ? null : classes.get(key);
	}

	/**
	 * 设定key所对应的单元格类型
	 * 
	 * @param key
	 * @param cellClass
	 */
	public void setCellClass(Object key, Class cellClass) {
		if (classes == null) classes = new HashMap<Object, Class>();
		classes.put(key, cellClass);
	}

	/**
	 * 设定第sheet个工作簿第row行col列的单元格类型
	 * 
	 * @param index
	 * @param rownum
	 * @param cellnum
	 * @param cellClass
	 */
	public void setCellClass(Class cellClass, Integer... index) {
		setCellClass(getListener().getKey(index), cellClass);
	}

	/**
	 * 根据指定的版本得到不同的Workbook实例
	 * 
	 * @param file file 要读取的文件
	 * @param version excel版本,".xls"为2003,".xlsx"为2007
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Workbook getWorkbookInstance(File file, String version) throws IOException {
		version = StringHelper.isEmpty(version) ? FileHelper.getFileSuffix(file) : version;
		return !StringHelper.isEmpty(version) && version.trim().equalsIgnoreCase(VERSION_2007) ? new XSSFWorkbook(
				file.getAbsolutePath()) : new HSSFWorkbook(new FileInputStream(file));
	}

	/**
	 * 读取file的数据
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public List[] read(File file) throws IOException {
		return this.read(file, StringHelper.isEmpty(version) ? FileHelper.getFileSuffix(file) : version);
	}

	private boolean readedAll;

	/**
	 * 读取file的数据
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public List[] read(File file, String version) throws IOException {
		// this.setVersion(version);
		workbook = getWorkbookInstance(file, version);
		if (getListener() == null) setListener(new POIReaderListener());
		data = getListener().read(new POIEvent(this));
		readedAll = true;
		return data;
	}

	/**
	 * 读取file的第index个表格的数据
	 * 
	 * @param file
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public List read(File file, int index) throws IOException {
		return this.read(file, index, StringHelper.isEmpty(version) ? FileHelper.getFileSuffix(file) : version);
	}

	/**
	 * 读取file的第index个表格的数据
	 * 
	 * @param file
	 * @param index
	 * @param version 文件版本
	 * @return
	 * @throws IOException
	 */
	public List read(File file, int index, String version) throws IOException {
		if (readedAll && index >= 0) return data[index];
		workbook = getWorkbookInstance(file, version);
		if (getListener() == null) setListener(new POIReaderListener());
		data = getListener().read(new POIEvent(this), index);
		return data[0];
	}

	@Override
	public POIReaderListener getListener() {
		return (POIReaderListener) super.getListener();
	}

	public Map<Object, Class> getClasses() {
		return classes;
	}

	public void setClasses(Map<Object, Class> classes) {
		this.classes = classes;
	}
}
