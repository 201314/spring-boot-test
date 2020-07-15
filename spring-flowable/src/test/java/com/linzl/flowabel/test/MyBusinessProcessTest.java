package com.linzl.flowabel.test;

import org.flowable.engine.test.Deployment;
import org.flowable.engine.test.FlowableTestCase;
import org.flowable.task.api.Task;

public class MyBusinessProcessTest extends FlowableTestCase {

	@Deployment
	public void testRuleUsageExample() {
		runtimeService.startProcessInstanceByKey("ruleUsage");
		Task task = taskService.createTaskQuery().singleResult();
		assertEquals("My Task", task.getName());
		taskService.complete(task.getId());
		assertEquals(0, runtimeService.createProcessInstanceQuery().count());
	}
}