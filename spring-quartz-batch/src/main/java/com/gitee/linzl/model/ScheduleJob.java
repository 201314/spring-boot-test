package com.gitee.linzl.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 定时任务实体
 * 
 * @author linzl
 */
@Setter
@Getter
public class ScheduleJob implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 任务id **/
	private String jobId;

	/** 任务名称 **/
	private String jobName;

	/** 任务分组 **/
	private String jobGroup;

	/** 任务执行的类 */
	private String jobClass;

	/** Trigger触发器参数 **/
	/** 开始时间 */
	private Date triggerStartTime;
	/** 停止时间 */
	private Date triggerEndTime;
	/** 优先级 **/
	private int priority;
	/** 延迟启动 **/
	private Long startDelay;

	/** NONE, NORMAL, PAUSED, COMPLETE, ERROR, BLOCKED */
	/** 任务状态 0禁用 1启用 2删除 **/
	private Integer jobStatus;

	/** 任务运行时间表达式 **/
	private String cronExpression;

	/** 任务描述 **/
	private String description;
}