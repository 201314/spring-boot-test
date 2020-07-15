package com.gitee.linzl.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 获取资源文件路径
 * 
 * @author linzl
 *
 */
public class ReadResourceUtil {

	private static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			System.out.println("Cannot access thread context ClassLoader - falling back to system class loader" + ex);
		}
		if (cl == null) {
			cl = ReadResourceUtil.class.getClassLoader();
		}
		return cl;
	}

	public static URL getURL() throws IOException {
		return getURL("");
	}

	public static URL getURL(String path) throws IOException {
		return getURL(path, getDefaultClassLoader());
	}

	public static URL getURL(String path, Class<?> clazz) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		URL url = clazz.getResource(path);
		if (url == null) {
			throw new FileNotFoundException(
					"class path resource [" + path + "] cannot be resolved to URL because it does not exist");
		}
		return url;
	}

	public static URL getURL(String path, ClassLoader classLoader) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		URL url = classLoader.getResource(path);
		if (url == null) {
			throw new FileNotFoundException(
					"class path resource [" + path + "] cannot be resolved to URL because it does not exist");
		}
		return url;
	}

	public static InputStream getInputStream(String path) throws IOException {
		return getInputStream(path, getDefaultClassLoader());
	}

	public static InputStream getInputStream(String path, Class<?> clazz) throws IOException {
		//if (path.startsWith("/")) {
		//	path = path.substring(1);
		//}
		//InputStream input = clazz.getResourceAsStream(path);
		/*
		 * if (input == null) { throw new FileNotFoundException( "class path resource ["
		 * + path + "] cannot be resolved to URL because it does not exist"); }
		 */
		return new FileInputStream(new File(path));
	}

	public static InputStream getInputStream(String path, ClassLoader classLoader) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		//classLoader.getResource(path);
		//InputStream input = classLoader.getResourceAsStream(path);
		/*
		 * if (input == null) { throw new FileNotFoundException( "class path resource ["
		 * + path + "] cannot be resolved to URL because it does not exist"); }
		 */
		return new FileInputStream(new File(path));
	}

	/**
	 * 获取cls类加载器中 /WebContent/WEB-INF/路径下的资源文件,递归搜索
	 * 
	 * @param name
	 *            资源文件名称
	 * @param cls
	 * @return
	 */
	public static URL getWebInfoResouceURL(String name, Class<?> cls) {
		File targetFile = null;
		String classPath = cls.getResource("/").getFile();
		// 先从外围找
		int index = classPath.indexOf("classes");
		if (index > -1) {
			String outClassPath = classPath.substring(0, index);
			targetFile = findFile(new File(outClassPath), name);
		}

		// 找不到，再深入类文件中看是否有匹配的
		if (targetFile == null && index > -1) {
			// 递归搜索文件
			targetFile = findFile(new File(classPath), name);
		}
		try {
			return targetFile.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static File findFile(File targetFile, String name) {
		File[] files = targetFile.listFiles();

		if (files == null) {
			return null;
		}
		File target = null;
		for (File file : files) {
			if (file.isFile() && file.getName().equalsIgnoreCase(name)) {
				return file;
			} else {
				target = findFile(file, name);
				if (target != null) {
					return target;
				}
			}
		}
		return target;
	}

	public static void main(String[] args) {
		try {
			URL url = ReadResourceUtil.getURL("/com/linzl/cn/property/jdbc.properties");
			System.out.println(url.toURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
