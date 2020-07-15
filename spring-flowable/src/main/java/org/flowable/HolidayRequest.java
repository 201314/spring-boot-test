package org.flowable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

public class HolidayRequest {

	public static void main(String[] args) {
		// ProcessEngineConfiguration一般是通过默认配置文件初始化
		// standalone表示只能自己用，在spring中可用SpringProcessEngineConfiguration
		ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
				.setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")

				.setJdbcUsername("sa")

				.setJdbcPassword("")
				// 使用内存数据库
				.setJdbcDriver("org.h2.Driver")
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

		// allows you to configure and tweak the settings for the process engine
		ProcessEngine processEngine = cfg.buildProcessEngine();

		// deploy a process definition to the Flowable engine
		RepositoryService repositoryService = processEngine.getRepositoryService();
		Deployment deployment = repositoryService.createDeployment()

				.addClasspathResource("holiday-request.bpmn20.xml")

				.deploy();

		// verify that the process definition is known to the engine
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()

				.deploymentId(deployment.getId())

				.singleResult();
		System.out.println("Found process definition : " + processDefinition.getName());

		// To start the process instance, we need to provide some initial process
		// variables.through a form that is presented to the user or through a REST API
		// when a process is triggered by something automatic.
		Scanner scanner = new Scanner(System.in);

		System.out.println("Who are you?");
		String employee = scanner.nextLine().trim();

		System.out.println("How many holidays do you want to request?");
		Integer nrOfHolidays = Integer.valueOf(scanner.nextLine().trim());

		System.out.println("Why do you need them?");
		String description = scanner.nextLine().trim();

		// start a process instance through the RuntimeService
		RuntimeService runtimeService = processEngine.getRuntimeService();

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("employee", employee);
		variables.put("nrOfHolidays", nrOfHolidays);
		variables.put("description", description);
		ProcessInstance processInstance = runtimeService
				// holidayRequest是bpmn20.xml内容中的ID
				.startProcessInstanceByKey("holidayRequest", variables);

		// To get the actual task list
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
		System.out.println("You have " + tasks.size() + " tasks:");
		for (int i = 0; i < tasks.size(); i++) {
			System.out.println((i + 1) + ") " + tasks.get(i).getName());
		}

		// Using the task identifier, we can now get the specific process instance
		// variables and show on the screen the actual request
		System.out.println("Which task would you like to complete?");
		int taskIndex = Integer.valueOf(scanner.nextLine().trim());
		Task task = tasks.get(taskIndex - 1);
		Map<String, Object> processVariables = taskService.getVariables(task.getId());
		System.out.println(processVariables.get("employee") + " wants " + processVariables.get("nrOfHolidays")
				+ " of holidays. Do you approve this?");

		// complete the task
		boolean approved = scanner.nextLine().trim().toLowerCase().equals("y");
		variables = new HashMap<String, Object>();
		variables.put("approved", approved);
		taskService.complete(task.getId(), variables);

		// we want to show the duration of the process instance that we’ve been
		// executing so far
		// only the activities for one particular process instance
		// only the activities that have finished
		HistoryService historyService = processEngine.getHistoryService();
		List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstance.getId()).finished().orderByHistoricActivityInstanceEndTime().asc()
				.list();

		for (HistoricActivityInstance activity : activities) {
			System.out.println(activity.getActivityId() + " took " + activity.getDurationInMillis() + " milliseconds");
		}
	}
}