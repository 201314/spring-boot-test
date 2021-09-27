package com.gitee.linzl;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import com.alibaba.fastjson.JSON;
import com.gitee.linzl.commons.filter.gzip.GzipFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;

@SpringBootApplication
@Slf4j
@Controller
public class LogbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogbackApplication.class, args);
	}

	@Autowired
	private RestTemplateBuilder builder;

	// 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
	@Bean
	public RestTemplate restTemplate() {
		builder.setConnectTimeout(Duration.ofMillis(60000));
		builder.setReadTimeout(Duration.ofMillis(60000));
		return builder.build();
	}

	@ResponseBody
	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public String upload(String test, MultipartFile jarFile) {
		// 下面是测试代码
		log.debug("test::【{}】", JSON.toJSONString(test));
		String originalFilename = jarFile.getOriginalFilename();
		log.debug("originalFilename::【{}】", originalFilename);
		String fileName = jarFile.getName();
		log.debug("fileName:【{}】", fileName);
		try {
			// transferTo的路径，在window下必须包含盘符
			jarFile.transferTo(new File("D://test//333.txt"));
			// String string = new String(jarFile.getBytes(), "UTF-8");
			// System.out.println(string);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO 处理文件内容...
		return "OK";
	}

	@Bean
	public MDCInsertingServletFilter mdcFilter() {
		return new MDCInsertingServletFilter();
	}

	@Bean
	public FilterRegistrationBean<MDCInsertingServletFilter> filterRegistrationBean() {
		FilterRegistrationBean<MDCInsertingServletFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(mdcFilter());
		registration.addUrlPatterns("/*");
		registration.setName("mdcFilter");
		return registration;
	}
}
