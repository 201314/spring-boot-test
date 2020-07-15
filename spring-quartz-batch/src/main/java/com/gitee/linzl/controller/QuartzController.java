package com.gitee.linzl.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.linzl.model.ScheduleJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/quartz")
public class QuartzController {
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	/**
	 * 任务创建与更新(未存在的就创建，已存在的则更新)
	 * 
	 * @param job
	 * @return
	 */
	@PostMapping(value = "/update")
	@ResponseBody
	public Map<String, Object> updateQuartz(@RequestBody ScheduleJob job) {
		try {
			if (null != job) {
				Scheduler scheduler = schedulerFactoryBean.getScheduler();
				// 获取触发器标识
				TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
				// 获取触发器trigger
				CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
				if (null == trigger) {// 不存在任务
					JobDetailFactoryBean jobFactoryBean = new JobDetailFactoryBean();
					// 创建任务
					// jobFactoryBean.setJobClass(Class.forName(job.getJobClass()));
					jobFactoryBean.setBeanName(job.getJobClass());
					jobFactoryBean.setGroup(job.getJobGroup());
					jobFactoryBean.setName(job.getJobName());
					jobFactoryBean.setDurability(true);
					// jobFactoryBean.getJobDataMap().put("scheduleJob", job);
					schedulerFactoryBean.setJobDetails(jobFactoryBean.getObject());

					CronTriggerFactoryBean cronFactoryBean = new CronTriggerFactoryBean();
					cronFactoryBean.setStartDelay(job.getStartDelay());
					cronFactoryBean.setName(job.getJobName());
					cronFactoryBean.setGroup(job.getJobGroup());
					cronFactoryBean.setCronExpression(job.getCronExpression());
					cronFactoryBean.setDescription(job.getDescription());
					cronFactoryBean.setJobDetail(jobFactoryBean.getObject());
					schedulerFactoryBean.setTriggers(cronFactoryBean.getObject());
				} else {
					trigger.getJobKey();
					// Trigger已存在，那么更新相应的定时设置
					// 表达式调度构建器
					CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

					// 按新的cronExpression表达式重新构建trigger
					trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder)
							.build();

					// 按新的trigger重新设置job执行
					scheduler.rescheduleJob(triggerKey, trigger);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 暂停任务
	 * 
	 * @param scheduleJob
	 * @return
	 */
	@PostMapping(value = "/pause")
	@ResponseBody
	public Map<String, Object> pauseQuartz(@RequestBody ScheduleJob scheduleJob) {
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			scheduler.pauseJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("暂停任务失败", e);
			return null;
		}
		return null;
	}

	/**
	 * 恢复任务
	 * 
	 * @param scheduleJob
	 * @return
	 */
	@PostMapping(value = "/resume")
	@ResponseBody
	public Map<String, Object> resumeQuartz(@RequestBody ScheduleJob scheduleJob) {
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			scheduler.resumeJob(jobKey);
		} catch (SchedulerException e) {
			log.error("恢复任务失败", e);
			return null;
		}
		return null;
	}

	/**
	 * 删除任务 ，对应的trigger也将被删除
	 * 
	 * @param scheduleJob
	 * @return
	 */
	@PostMapping(value = "/delete")
	public Map<String, Object> deleteQuartz(@RequestBody ScheduleJob scheduleJob) {
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			// 先暂停任务，再删除任务
			scheduler.pauseJob(jobKey);
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("删除任务失败", e);
			return null;
		}
		return null;
	}

	/**
	 * 立即运行任务
	 * 
	 * @param scheduleJob
	 * @return
	 */
	@PostMapping(value = "/execute")
	public Object executeJob(@RequestBody ScheduleJob scheduleJob) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		try {
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return jobKey;
	}

	/**
	 * 查询运行中的任务
	 * 
	 * @throws SchedulerException
	 */
	@PostMapping(value = "/run")
	public Object runningJobs() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				ScheduleJob job = new ScheduleJob();
				job.setJobName(jobKey.getName());
				job.setJobGroup(jobKey.getGroup());
				job.setDescription("触发器:" + trigger.getKey());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				job.setJobStatus(triggerState.ordinal());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					String cronExpression = cronTrigger.getCronExpression();
					job.setCronExpression(cronExpression);
				}
				jobList.add(job);
			}
		}
		return jobList;
	}

	/**
	 * 查询计划中的任务
	 * 
	 * @throws SchedulerException
	 */
	@PostMapping(value = "/plan")
	public Object planJobs() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
		for (JobExecutionContext executingJob : executingJobs) {
			ScheduleJob job = new ScheduleJob();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();
			job.setJobName(jobKey.getName());
			job.setJobGroup(jobKey.getGroup());
			job.setDescription("触发器:" + trigger.getKey());
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			job.setJobStatus(triggerState.ordinal());
			if (trigger instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				String cronExpression = cronTrigger.getCronExpression();
				job.setCronExpression(cronExpression);
			}
			jobList.add(job);
		}
		return jobList;
	}
}