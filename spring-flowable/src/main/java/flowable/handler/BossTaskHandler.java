package flowable.handler;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * 员工经理任务分配
 */
public class BossTaskHandler implements TaskListener {
	private static final long serialVersionUID = 253814966377774751L;

	@Override
	public void notify(DelegateTask delegateTask) {
		delegateTask.setAssignee("老板");
	}
}
