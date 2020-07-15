package com.gitee.linzl.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.linzl.mapper.MessageLogMapper;
import com.gitee.linzl.mapper.UserMapper;
import com.gitee.linzl.model.MessageLog;
import com.gitee.linzl.model.User;
import com.gitee.linzl.mq.DirectSender;
import com.gitee.linzl.service.IUserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
	@Autowired
	private TransactionTemplate transaction;
	@Autowired
	private MessageLogMapper logMapper;
	@Autowired
	private DirectSender sender;

	public boolean insert(User user) {
		// 第一步，插入业务数据库

		// 第二步插入消息库
		Boolean msgFlag = transaction.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				MessageLog messageLog = new MessageLog();
				messageLog.setCreatTime(new Date());
				messageLog.setMessageId(user.getId());
				messageLog.setMessage(JSON.toJSONString(user));
				messageLog.setStatus(0);
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, 5);
				messageLog.setNextTryTime(cal.getTime());
				// 消息日志入库
				logMapper.insert(messageLog);
				return true;
			}
		});

		// 第三步发送队列
		// 消息日志必须入库成功才能发送
		if (msgFlag) {
			sender.send(user);
		}
		return true;
	}
}
