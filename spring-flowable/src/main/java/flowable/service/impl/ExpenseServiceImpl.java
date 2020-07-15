package flowable.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import flowable.mapper.ExpenseMapper;
import flowable.model.Expense;
import flowable.model.TaskVo;
import flowable.service.IExpenseService;
import flowable.state.ExpenseState;
import flowable.support.HttpKit;

/**
 * <p>
 * 报销表 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2017-12-04
 */
@Service
public class ExpenseServiceImpl extends ServiceImpl<ExpenseMapper, Expense> implements IExpenseService {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private ProcessEngine processEngine;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(Expense expense) {
		// 保存业务数据
		// expense.setUserid(ShiroKit.getUser().getId());
		expense.setState(ExpenseState.SUBMITING.getCode());

		// 启动流程
		HashMap<String, Object> map = new HashMap<>();
		// map.put("taskUser", ShiroKit.getUser().getName());
		map.put("money", expense.getMoney());
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Expense", map);
		expense.setProcessId(processInstance.getId());
		super.save(expense);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer expenseId) {
		Expense expense = this.getById(expenseId);
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
				.processInstanceId(expense.getProcessId()).list();
		for (ProcessInstance processInstance : list) {
			if (processInstance.getId().equals(expense.getProcessId())) {
				runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "取消报销");
			}
		}
		super.removeById(expenseId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void pass(String taskId) {
		// 使用任务ID，查询任务对象，获取流程流程实例ID
		Task task = taskService.createTaskQuery()//
				.taskId(taskId).singleResult();

		// 通过审核
		HashMap<String, Object> map = new HashMap<>();
		map.put("outcome", "通过");
		taskService.complete(taskId, map);

		// 判断流程是否结束,结束之后修改状态
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(task.getProcessInstanceId())// 使用流程实例ID查询
				.singleResult();
		Wrapper<Expense> wrapper = new QueryWrapper<Expense>().eq("processId", task.getProcessInstanceId());
		Expense expense = this.getOne(wrapper);

		// 审核通过修改为通过状态
		if (pi == null) {
			expense.setState(ExpenseState.PASS.getCode());
			expense.updateById();
		} else {
			// 审核通过如果还有记录则为经理或boss审核中
			expense.setState(ExpenseState.CHECKING.getCode());
			expense.updateById();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void unPass(String taskId) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("outcome", "驳回");
		taskService.complete(taskId, map);
	}

	@Override
	public List<TaskVo> getProcessList() {
		String name = "admin";// ShiroKit.getUser().getName();
		List<Task> list = taskService.createTaskQuery().taskAssignee(name).orderByTaskCreateTime().desc().list();
		ArrayList<TaskVo> taskVos = new ArrayList<>();
		for (Task task : list) {
			Object money = runtimeService.getVariable(task.getExecutionId(), "money");
			String taskUser = (String) taskService.getVariable(task.getId(), "taskUser");
			boolean flag = false;
			if (name.equals(taskUser)) {
				flag = false;
			} else {
				flag = true;
			}
			taskVos.add(new TaskVo(task.getId(), task.getName(), task.getCreateTime(), taskUser, money, flag));
		}
		return taskVos;
	}

	@Override
	public void printProcessImage(Integer expenseId) throws IOException {
		Expense expense = this.getById(expenseId);
		String processId = expense.getProcessId();
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();

		// 流程走完的不显示图
		if (pi == null) {
			return;
		}

		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();

		// 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		String InstanceId = task.getProcessInstanceId();
		List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(InstanceId).list();

		// 得到正在执行的Activity的Id
		List<String> activityIds = new ArrayList<>();
		List<String> flows = new ArrayList<>();
		for (Execution exe : executions) {
			List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
			activityIds.addAll(ids);
		}

		// 获取流程图
		BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
		ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
		ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
		InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows,
				engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(),
				engconf.getClassLoader(), 1.0);
		OutputStream out = null;
		byte[] buf = new byte[1024];
		int legth = 0;
		try {
			out = HttpKit.getResponse().getOutputStream();
			while ((legth = in.read(buf)) != -1) {
				out.write(buf, 0, legth);
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}
