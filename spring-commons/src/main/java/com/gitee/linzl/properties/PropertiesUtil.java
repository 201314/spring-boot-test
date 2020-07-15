package com.gitee.linzl.properties;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * 常用资源文件工具类
 * 
 * @author linzl 2013年8月15日
 */
public class PropertiesUtil {
	private static Properties properties = new Properties();

	public PropertiesUtil() {
		try {
			// InputStream
			// in=this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
			// 注意以下两种获取包路径下资源文件的路径

			// 获取该类的加载器，和类文件是同级
			InputStream is = this.getClass().getResourceAsStream("/com/linzl/cn/xml/xmlRead.xml");
			// getClassLoader是获取本类的父加载器，和该类的包结构是同级
			is = this.getClass().getClassLoader().getResourceAsStream("com/linzl/cn/xml/xmlRead.xml");

			InputStream in = new FileInputStream("具体路径的资源文件");
			properties.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取资源文件的属性值
	 */
	public static String getAssignName(String property) {
		return properties.getProperty(property, "没有该" + property + "属性值");
	}

	/**
	 * 默认资源文件中驱动名称的Key为driverName
	 */
	public static String getDefaultDriverName() {
		return getAssignName("driverName");
	}

	/**
	 * 默认资源文件中数据库地址的key为url
	 */
	public static String getDefaultUrl() {
		return getAssignName("url");
	}

	/**
	 * 默认资源文件中用户名的key为user
	 */
	public static String getDefaultUser() {
		return getAssignName("user");
	}

	/**
	 * 默认资源文件中用户密码的key为password
	 */
	public static String getDefaultPass() {
		return getAssignName("password");
	}

	/**
	 * 设置资源文件属性key的值为value
	 *
	 * @param key
	 *            属性
	 * @param value
	 *            属性值
	 */
	public static void setKeyValue(String key, String value) {
		properties.setProperty(key, value);
	}

	/**
	 * 保存properties属性的修改
	 *
	 * @param path
	 *            保存位置路径
	 * @throws IOException
	 */
	public static void outputToProp(String path) throws IOException {
		OutputStream fos = new FileOutputStream(path);
		properties.store(fos, "输出到properties文件");
		// properties.storeToXML(fos, "保存为xml格式");
		fos.close();
	}

	public static void main(String[] args) {
		URL in = PropertiesUtil.class.getClassLoader().getResource("jdbc.properties");
		System.out.println(in.getPath());// 获取资源绝对路径
		PropertiesUtil.class.getResource("/jdbc.properties");
	}
}