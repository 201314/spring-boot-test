package com.gitee.linzl.ftp.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import lombok.extern.slf4j.Slf4j;

/**
 * FTP上传下载文件,不做加解密处理
 * 
 * PORT中文称为主动模式，工作原理：
 * 
 * FTP客户端连接到FTP服务器的21端口，发送用户名和密码登录，登录成功后要列表列表或者读取数据时，客户端随机开放一个端口（1024以上），
 * 发送PORT命令到FTP服务器，告诉服务器客户端采用主动模式并开放端口;
 * FTP服务器收到PORT主动模式命令和端口号后，通过服务器的20端口和客户端开放的端口连接
 * 
 * PASV是Passive的缩写，中文成为被动模式，工作原理：FTP客户端连接到FTP服务器的21端口，发送用户名和密码登录，登录成功后要list列表或者读取数据时，发送PASV命令到FTP服务器，
 * 服务器在本地随机开放一个端口（1024以上），然后把开放的端口告诉客户端， 客户端再连接到服务器开放的端口进行数据传输
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年8月27日
 */
@Slf4j
public class FtpClientTemplate implements Closeable {
	public enum RegexEnum {
		START_REGEX, // 以什么为开头

		END_REGEX, // 以什么为结束

		MATCH_REGEX, // 以正则去匹配

		EQUAL_REGEX, // 完全相等

		INGORE_REGEX;// 忽略匹配规则
	}

	private FTPClient ftpClient = null;
	private DecimalFormat format = new DecimalFormat("0.00%");

	public FtpClientTemplate(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	/**
	 * 改变目录路径
	 * 
	 * @param directory
	 * @return
	 */
	public boolean changeWorkingDirectory(String directory) {
		boolean flag = true;
		try {
			flag = ftpClient.changeWorkingDirectory(directory);
		} catch (IOException e) {
			log.error("切换ftp目录失败:", e);
		}
		return flag;
	}

	public boolean mkdir(String ftpDir, File file) {
		return mkdir(new File(ftpDir, file.getPath()));
	}

	/**
	 * 递归创建目录
	 * 
	 * @param file
	 * @return
	 */
	public boolean mkdir(File file) {
		try {
			if (existFile(file.getPath())) {
				changeWorkingDirectory(file.getPath());
			} else {
				mkdir(file.getParentFile());// 递归创建目录
				ftpClient.makeDirectory(file.getPath());
			}
		} catch (IOException e) {
			log.error("新建ftp目录失败:", e);
		}
		return true;
	}

	/**
	 * 创建目录
	 * 
	 * @param dir
	 * @return
	 */
	public boolean mkdirParent(File file) {
		final File parent = file.getParentFile();
		if (parent == null) {
			return false;
		}
		return mkdir(parent);
	}

	/**
	 * 判断ftp服务器文件是否存在
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean existFile(String path) throws IOException {
		boolean flag = false;
		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		if (Objects.nonNull(ftpFileArr) && ftpFileArr.length > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 获取目录下的文件名
	 * 
	 * @param ftpDir    获取哪个目录
	 * @param recursion 是否递归获取子目录
	 * @return
	 */
	public List<String> listFiles(String ftpDir, boolean recursion) {
		List<String> fileLists = new ArrayList<>();
		try {
			FTPFile[] files = ftpClient.listFiles(ftpDir);
			for (FTPFile ftpFile : files) {
				if (ftpFile.isFile()) {
					fileLists.add(ftpFile.getName());
				} else if (recursion && ftpFile.isDirectory()) {
					List<String> cLists = listFiles(ftpDir + "/" + ftpFile.getName(), recursion);
					fileLists.addAll(cLists);
				}
			}
		} catch (IOException e) {
			log.error("ftp列表失败:", e);
		}
		return fileLists;
	}

	/**
	 * 获得FTP 服务器下的文件列表,包含目录名称
	 * 
	 * @param ftpDir ftp目录
	 * @return
	 */
	public String[] listFileNames(String ftpDir) {
		String files[] = null;
		try {
			this.ftpClient.changeWorkingDirectory(ftpDir);
			files = this.ftpClient.listNames();
		} catch (IOException e) {
			log.error("列出文件异常!", e);
		}
		return files;
	}

	/**
	 * 修改文件名
	 * 
	 * @param srcFname    源文件名
	 * @param targetFname 修改后的文件名
	 * @return
	 */
	public boolean reName(String srcFname, String targetFname) {
		boolean flag = false;
		try {
			flag = this.ftpClient.rename(srcFname, targetFname);
		} catch (IOException e) {
			log.error("修改ftp文件名失败:", e);
		}
		return flag;
	}

	/**
	 * 删除文件,先切换到对应目录
	 * 
	 * @param filename 要删除的文件名称
	 * @return
	 */
	public boolean deleteFile(String filename) {
		boolean flag = false;
		try {
			return ftpClient.deleteFile(filename);
		} catch (Exception e) {
			log.error("删除ftp文件失败:", e);
		}
		return flag;
	}

	/**
	 * 设置传输文件的类型[文本文件或者二进制文件]
	 *
	 * @param fileType--FTP.BINARY_FILE_TYPE,FTP.ASCII_FILE_TYPE
	 */
	public void setFileType(int fileType) {
		try {
			this.ftpClient.setFileType(fileType);
		} catch (Exception e) {
			log.error("设置传输文件的类型异常！", e);
			throw new RuntimeException("设置传输文件的类型异常！", e);
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param ftpDir 上传到指定的ftp目录
	 * @param file   待上传文件或文件夹
	 * @return
	 */
	public boolean upload(String ftpDir, File file) {
		try {
			String currentFtpDir = ftpDir;
			log.debug("对象地址:【{}】", ftpClient);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			if (file.isDirectory()) {// 创建当前目录
				currentFtpDir = currentFtpDir + "/" + file.getName();
				mkdir(new File(currentFtpDir));
				File[] files = file.listFiles();
				for (File source : files) {
					if (source.isDirectory()) {
						upload(currentFtpDir, source);
					} else {
						upload(currentFtpDir, source, source.getName());
					}
				}
			} else if (file.isFile()) {
				upload(currentFtpDir, file, file.getName());
			}
		} catch (Exception e) {
			log.error("上传ftp目录失败:", e);
		}
		return false;
	}

	public boolean upload(String ftpDir, File file, String fileName) throws Exception {
		if (Objects.isNull(file) || !file.isFile()) {
			throw new Exception("只能上传文件");
		}
		return upload(ftpDir, new FileInputStream(file), fileName);
	}

	/**
	 * 上传文件
	 * 
	 * @param fileName 上传到ftp的文件名
	 * @param input    输入文件流
	 * @return
	 */
	public boolean upload(String ftpDir, FileInputStream input, String fileName) {
		boolean flag = false;
		changeWorkingDirectory(ftpDir);
		// TODO 可能已经上传完毕，要使用真实的文件名判断一下
		// FTPFile[] file2 = ftpClient.listFiles(ftpDir + "/" + fileName);
		String tmpFileName = FilenameUtils.getBaseName(fileName) + ".tmp";
		try (BufferedInputStream buf = new BufferedInputStream(input);) {
			FTPFile[] file = ftpClient.listFiles(ftpDir + "/" + tmpFileName);
			long beginSize = 0;
			FileChannel fc = input.getChannel();
			long endSize = fc.size();
			if (Objects.nonNull(file) && file.length > 0) {
				beginSize = file[0].getSize();
				if (log.isDebugEnabled()) {
					log.debug("已存在上传文件:【{}】,大小:【{}】", file[0].getName(), file[0].getSize());
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("读取文件:【{}】,开始位置:【{}】,文件大小:【{}】,", fileName, beginSize, endSize);
			}
			ftpClient.setRestartOffset(beginSize);
			OutputStream out = ftpClient.storeFileStream(tmpFileName);

			byte[] bytes = new byte[1024];
			long ingSize = 0;
			long waitSize = endSize - beginSize;
			int readLength = 0;

			double nowPercent = beginSize / (double) endSize;
			double finishPercent = 0;
			buf.skip(beginSize);

			if (log.isDebugEnabled()) {
				log.debug(">>>>>上传进度:【{}】", format.format(nowPercent));
			}
			while ((readLength = buf.read(bytes)) != -1) {
				out.write(bytes, 0, readLength);
				ingSize += readLength;
				if (log.isDebugEnabled()) {
					log.debug("共读取到:【{}】,等待读取:【{}】,", ingSize, waitSize);
				}
				if (ingSize >= waitSize) {
					log.debug(">>>>>上传进度:100%");
				} else {
					// 每次比上一次增加1%(0.01),则打印
					double currentPercent = (ingSize + beginSize) / (double) endSize;

					BigDecimal bd1 = new BigDecimal(Double.toString(currentPercent));
					BigDecimal bd2 = new BigDecimal(Double.toString(finishPercent));
					double subValue = bd1.subtract(bd2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

					if (subValue > 0.05) {
						if (log.isDebugEnabled()) {
							log.debug(">>>>>上传进度:【{}】", format.format(currentPercent));
						}
						finishPercent = currentPercent;
					}
				}
			}
			if (Objects.nonNull(input)) {
				out.flush();
				out.close();
				// 使用storeFileStream 、 retrieveFileStream 必须调用此方法
				// 使用storeFile 、 retrieveFile 默认调用了此方法
				ftpClient.completePendingCommand();
				reName(tmpFileName, fileName);
			}
			flag = true;
		} catch (Exception e) {
			log.error("上传异常信息:", e);
		}
		return flag;
	}

	/**
	 * 下载ftp目录下的全部直接文件
	 *
	 * @param ftpDir   FTP服务器文件目录
	 * @param localDir 下载后的存储路径
	 * @return
	 */
	public List<String> downloadAll(String ftpDir, File localDir) {
		return download(ftpDir, localDir, null, RegexEnum.INGORE_REGEX, false);
	}

	/**
	 * 下载ftp目录下文件名以startWith开头的直接文件
	 *
	 * @param ftpDir    FTP服务器文件目录
	 * @param localDir  下载后的存储路径
	 * @param startWith 以某个字符串开头的文件
	 * @return
	 */
	public List<String> downloadStartWith(String ftpDir, File localDir, String startWith) {
		return download(ftpDir, localDir, startWith, RegexEnum.START_REGEX, false);
	}

	/**
	 * 下载ftp目录下文件名以endWith结尾的直接文件
	 *
	 * @param ftpDir   FTP服务器文件目录
	 * @param localDir 下载后的存储路径
	 * @param endWith  以某个字符串结尾的文件
	 * @return
	 */
	public List<String> downloadEndWith(String ftpDir, File localDir, String endWith) {
		return download(ftpDir, localDir, endWith, RegexEnum.END_REGEX, false);
	}

	/**
	 * 下载ftp目录下文件名为equalWith的直接文件
	 *
	 * @param ftpDir    FTP服务器文件目录
	 * @param localDir  下载后的存储路径
	 * @param equalWith 查找文件名为 equalWith的文件
	 * @return
	 */
	public List<String> downloadEqualWith(String ftpDir, File localDir, String equalWith) {
		return download(ftpDir, localDir, equalWith, RegexEnum.EQUAL_REGEX, false);
	}

	/**
	 * 下载ftp目录下文件名符合正则表达式的直接文件
	 *
	 * @param ftpDir   FTP服务器文件目录
	 * @param localDir 下载后的存储路径
	 * @param regex    合正则表达式
	 * @return
	 */
	public List<String> downloadMatchWith(String ftpDir, File localDir, String regex) {
		return download(ftpDir, localDir, regex, RegexEnum.MATCH_REGEX, false);
	}

	/**
	 * * 下载ftp文件到本地目录
	 * 
	 * @param ftpDir    FTP服务器文件目录
	 * @param localDir  下载后的存储路径
	 * @param regex     表达式
	 * @param type      采用的表达式匹配类型
	 * @param recursion 是否递归下载
	 * @return
	 */
	public List<String> download(String ftpDir, File localDir, String regex, RegexEnum type, boolean recursion) {
		List<String> downList = null;
		FTPFile[] ftpFiles = null;
		try {
			// ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			// ftpClient.enterLocalPassiveMode();
			boolean changeFtpDir = ftpClient.changeWorkingDirectory(ftpDir);
			if (!changeFtpDir) {
				return downList;
			}

			ftpFiles = ftpClient.listFiles();
			if (Objects.isNull(ftpFiles) || ftpFiles.length <= 0) {
				return downList;
			}

			downList = new ArrayList<>();
			for (FTPFile file : ftpFiles) {
				if (!recursion && file.isDirectory()) {
					continue;
				}

				if (file.isDirectory()) {
					File dir = new File(localDir, file.getName());
					FileUtils.forceMkdir(dir);
					download(ftpDir + "/" + file.getName(), dir, regex, type, recursion);
				} else if (file.isFile()) {
					String fileName = file.getName();// 文件名
					boolean isMatch = false;
					if (type == RegexEnum.START_REGEX) {
						isMatch = fileName.startsWith(regex);
					} else if (type == RegexEnum.END_REGEX) {
						isMatch = fileName.endsWith(regex);
					} else if (type == RegexEnum.MATCH_REGEX) {
						Matcher match = Pattern.compile(regex).matcher(fileName);
						isMatch = match.find();
					} else if (type == RegexEnum.EQUAL_REGEX) {
						isMatch = fileName.equals(regex);
					} else if (type == RegexEnum.INGORE_REGEX || type == null) {
						isMatch = true;
					}

					if (!isMatch) {
						continue;
					}

					try {
						FileUtils.forceMkdir(localDir);
					} catch (Exception e) {
						log.error("创建目录异常:【{}】", e.getMessage());
					}
					File localFile = new File(localDir, fileName);

					long endSize = file.getSize();
					long beginSize = localFile.length();
					long waitSize = endSize - beginSize;
					if (localFile.exists() && waitSize <= 0) {// 文件已存在
						continue;
					}
					ftpClient.changeWorkingDirectory(ftpDir);
					try (OutputStream out = new FileOutputStream(localFile, true);
							BufferedOutputStream buffer = new BufferedOutputStream(out);) {
						log.debug("读取文件:【{}】,开始位置:【{}】,文件大小:【{}】,", fileName, beginSize, endSize);
						ftpClient.setRestartOffset(beginSize);
						InputStream input = ftpClient.retrieveFileStream(fileName);
						byte[] bytes = new byte[1024];
						long ingSize = 0;
						int readLength = 0;

						double nowPercent = beginSize / (double) endSize;
						double finishPercent = 0;
						log.debug(">>>>>下载进度:【{}】", format.format(nowPercent));
						while ((readLength = input.read(bytes)) != -1) {
							out.write(bytes, 0, readLength);
							ingSize += readLength;
							if (ingSize >= waitSize) {
								log.debug(">>>>>下载进度:100%");
							} else {
								// 每次比上一次增加1%(0.01),则打印
								double currentPercent = (ingSize + beginSize) / (double) endSize;

								BigDecimal bd1 = new BigDecimal(Double.toString(currentPercent));
								BigDecimal bd2 = new BigDecimal(Double.toString(finishPercent));
								double subValue = bd1.subtract(bd2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

								if (subValue > 0.01) {
									log.debug(">>>>>下载进度:【{}】", format.format(currentPercent));
									finishPercent = currentPercent;
								}
							}
						}
						if (Objects.nonNull(input)) {
							input.close();
							// 使用storeFileStream 、 retrieveFileStream 必须调用此方法
							// 使用storeFile 、 retrieveFile 默认调用了此方法
							ftpClient.completePendingCommand();
						}
						downList.add(localFile.getAbsolutePath());
					}
				}
			}
		} catch (IOException se) {
			log.info("获取文件异常：【{}】", se.getMessage());
		}
		return downList;
	}

	/**
	 * 退出并关闭FTP连接
	 */
	public void close() {
		// if (Objects.nonNull(this.ftpClient) && this.ftpClient.isConnected()) {
		// try {
		// this.ftpClient.logout();// 退出FTP服务器,退出登录
		// this.ftpClient.disconnect();// 关闭FTP服务器的连接
		// } catch (Exception e) {
		// throw new RuntimeException("连接FTP服务失败！", e);
		// }
		// log.debug("成功关闭ftp");
		// }
	}
}