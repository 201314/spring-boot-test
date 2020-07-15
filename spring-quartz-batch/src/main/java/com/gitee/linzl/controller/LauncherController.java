package com.gitee.linzl.controller;

import java.util.Date;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.linzl.commons.tools.SpringContextUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 外部通过web请求直接启动batch job任务，无定时调度
 * 
 * 执行器
 * 
 * @author linzl
 *
 *         2017年2月24日
 */
@Slf4j
@RestController
@RequestMapping("job")
public class LauncherController {
	public static final String JOBNAME = "jobName";
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private JobOperator jobOperator;

	/**
	 * 启动任务
	 * 
	 * @param jobName
	 *            任务bean名称
	 * @param request
	 */
	@GetMapping(value = "lanuch")
	public void launch(@RequestParam String jobName, HttpServletRequest request) {
		try {
			Job job = (Job) SpringContextUtil.getBean(jobName);
			log.debug("job:【{}】,jobLauncher:【{}】", job, jobLauncher);
			jobLauncher.run(job, builderJobParemeters(request));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止任务
	 * 
	 * @param jobName
	 *            任务bean名称
	 * @param request
	 */
	@GetMapping(value = "stop")
	public void stop(@RequestParam String jobName, HttpServletRequest request) {
		Set<Long> runningExecutions;
		try {
			runningExecutions = jobOperator.getRunningExecutions(jobName);
			for (Long jobExcecuteID : runningExecutions) {
				// 何时完成停止，由batch决定
				// 如果Job正在reader 中，只有当read完成规定次数才会停止
				jobOperator.stop(jobExcecuteID);
			}
		} catch (NoSuchJobException | NoSuchJobExecutionException | JobExecutionNotRunningException e) {
			e.printStackTrace();
		}
	}

	private JobParameters builderJobParemeters(HttpServletRequest request) {
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addDate("date", new Date());
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (!JOBNAME.equals(paramName)) {
				builder.addString(paramName, request.getParameter(paramName));
			}
		}
		return builder.toJobParameters();
	}
}
