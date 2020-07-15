package com.gitee.linzl.ftp.test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gitee.linzl.boot.autoconfigure.ftp.FtpClientPool;
import com.gitee.linzl.ftp.core.FtpClientTemplate;
import com.gitee.linzl.ftp.service.FtpService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FtpTest {
	@Autowired
	private FtpClientPool pool;
	@Autowired
	private FtpService ftp;

	@Test
	public void uploadMany() {
		ExecutorService service = Executors.newFixedThreadPool(9);
		for (int start = 0; start < 19; start++) {
			final int index = start;
			service.submit(() -> {
				FTPClient client = null;
				try {
					client = pool.borrowObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
				FtpClientTemplate ftp = new FtpClientTemplate(client);
				ftp.upload("/test1", new File("D:\\etc\\test\\DownloadComfirmFiles" + index + ".txt"));
				pool.returnObject(client);
			});
		}
		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void uploadAnnotation() {
		ftp.sendFile("h");
	}
}
