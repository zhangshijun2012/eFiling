package com.sinosoft.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sinosoft.util.poi.POIReader;
import com.sinosoft.util.poi.POIWriter;

/**
 * poi工具类,用于简化excel表单的操作
 * 
 * @author LuoGang
 * 
 */
public class POIHelper {
	/**
	 * excel2003以下的文件类型
	 */
	public static final String VERSION_2003 = ".xls";

	/**
	 * excel2007的文件类型
	 */
	public static final String VERSION_2007 = ".xlsx";

	/**
	 * 默认使用2003
	 */
	public static final String VERSION_DEFAULT = VERSION_2003;

	/**
	 * 根据指定的版本得到不同的Workbook实例
	 * 
	 * @param version
	 *            excel版本,".xls"为2003,".xlsx"为2007
	 * @return
	 */
	public static Workbook getWorkbookInstance(String version) {
		return !StringHelper.isEmpty(version) && version.trim().equalsIgnoreCase(VERSION_2007) ? new XSSFWorkbook()
				: new HSSFWorkbook();
	}

	/**
	 * 读取excel文件的内容
	 * 
	 * @param file
	 *            要读取的文件
	 * @return POIReader POIReader对象读取出了excel中的每个sheet的数据.
	 * @see org.anywnyu.util.poi.POIReader
	 */
	public static POIReader read(File file) {
		return read(file, null);
	}

	/**
	 * 读取excel文件的内容
	 * 
	 * @param file
	 *            要读取的文件
	 * @param version
	 *            excel的版本
	 * @return POIReader POIReader对象读取出了excel中的每个sheet的数据.
	 * @see org.anywnyu.util.poi.POIReader
	 */
	public static POIReader read(File file, String version) {
		if (file == null || !file.exists() || !file.isFile()) return null;
		try {
			POIReader reader = new POIReader(version);
			reader.read(file);
			return reader;
		} catch (Exception e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取excel文件的数据
	 * 
	 * @param file
	 *            要读取的文件
	 * @param version
	 *            excel的版本
	 * @return List excel中每个sheet的数据.
	 * @see org.anywnyu.util.poi.POIReader
	 */
	public static List[] readData(File file, String version) {
		POIReader reader = POIHelper.read(file, version);
		return reader == null ? null : reader.getData();
	}

	/**
	 * 读取excel文件的数据
	 * 
	 * @param file
	 *            要读取的文件
	 * @return List excel中每个sheet的数据.
	 * @see org.anywnyu.util.poi.POIReader
	 */
	public static List[] readData(File file) {
		return readData(file, null);
	}

	/**
	 * 读取excel中的第index个sheet的数据.
	 * 
	 * @param file
	 *            要读取的文件
	 * @param index
	 * @param version
	 *            excel的版本
	 * @return List excel中的第index个sheet的数据.
	 * @see org.anywnyu.util.poi.POIReader
	 */
	public static List readData(File file, int index, String version) {
		if (file == null || !file.exists() || !file.isFile()) return null;
		try {
			POIReader reader = new POIReader(version);
			// reader.read(file, index);
			return reader.read(file, index);
		} catch (Exception e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取excel中的第index个sheet的数据.
	 * 
	 * @param file
	 *            要读取的文件
	 * @param index
	 * 
	 * @return List excel中的第index个sheet的数据.
	 * @see org.anywnyu.util.poi.POIReader
	 */
	public static List readData(File file, int index) {
		return readData(file, index, null);
	}

	/**
	 * 向输出流out写入excel数据
	 * 
	 * @param data
	 * @param out
	 * @param version
	 * @return
	 */
	public static boolean write(List[] data, OutputStream out, String version) {
		return POIHelper.write(null, data, out, version);
	}

	/**
	 * 向输出流out写入excel数据
	 * 
	 * @param identifiers
	 *            表头,此数据应该为一个3维数组.第1维表示sheet的index,第2维为行号,第3维为单元格号.
	 * @param data
	 * @param out
	 * @param version
	 * @param widths
	 *            列宽
	 * @return
	 */
	public static boolean write(Object[] identifiers, List[] data, OutputStream out, String version, int[][] widths) {
		if (!Helper.isEmpty(identifiers)) {
			if (!identifiers[0].getClass().isArray()) {
				// identifiers为一维数组
				identifiers = new Object[] { new Object[] { identifiers } };
			} else if (!((Object[]) identifiers[0])[0].getClass().isArray()) {
				// identifiers为二维数组
				identifiers = new Object[] { identifiers };
			}

		}
		POIWriter writer = new POIWriter(identifiers, data, version);
		try {
			writer.setWidths(widths);
			writer.write(out);
			return true;
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 向输出流out写入excel数据
	 * 
	 * @param identifiers
	 *            表头,此数据应该为一个3维数组.第1维表示sheet的index,第2维为行号,第3维为单元格号.
	 * @param data
	 * @param out
	 * @param version
	 * @return
	 */
	public static boolean write(Object[] identifiers, List[] data, OutputStream out, String version) {
		return POIHelper.write(identifiers, data, out, version, null);
	}

	/**
	 * 向输出流out写入excel数据
	 * 
	 * @param identifiers
	 *            表头
	 * @param data
	 * @param out
	 * @return
	 */
	public static boolean write(Object[] identifiers, List[] data, File file) {
		return POIHelper.write(identifiers, data, file, FileHelper.getFileSuffix(file));
	}

	/**
	 * 向文件file写入excel数据
	 * 
	 * @param identifiers
	 *            表头
	 * @param data
	 * @param out
	 * @param version
	 * @return
	 */
	public static boolean write(Object[] identifiers, List[] data, File file, String version) {
		try {
			return POIHelper.write(identifiers, data, new FileOutputStream(file), version);
		} catch (FileNotFoundException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// POIReader reader = POIHelper.read(new File(args[0]), args[1]);
		List[] data = new ArrayList[1];
		data[0] = new ArrayList();
		data[0].add(new Object[] { "数据0", "数据1" });
		data[0].add("数据10");
		data[0].add(new Object[] { "数据20", "数据21" });
		System.out.println(POIHelper.write(new Object[] { new Object[] { new Object[] { "表头1", "表头2" } } }, data,
				new File("d:\\ee.xlsx")));

		// System.out.println(StringHelper.join(POIHelper.readData(new File(args[0]), args[1])));
	}

}
