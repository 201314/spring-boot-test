package com.gitee.linzl.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.linzl.mapper.MessageLogMapper;
import com.gitee.linzl.mq.producer.DirectSender;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时处理,发送消息失败的
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年9月19日
 */
@Component
@Slf4j
public class RetryMessageTask {
	@Autowired
	private MessageLogMapper mapper;
	@Autowired
	private DirectSender sender;

//	@Scheduled(cron = "0 0/1 * * * ?")
//	public void resolveMessage() {
//		log.debug("===进入定时任务===");
//		// 查询出失败的消息
//		QueryWrapper<MessageLog> wrapper = new QueryWrapper<>();
//		wrapper.eq("status", 0);
//		wrapper.le("next_try_time", new Date());
//		wrapper.le("try_count", 3);
//
//		Page<MessageLog> page = new Page<>(0, 15);
//		IPage<MessageLog> result = mapper.selectPage(page, wrapper);
//
//		// 发送
//		if (result != null) {
//			result.getRecords().stream().forEach((messageLog) -> {
//				messageLog.setUpdateTime(new Date());
//				if (messageLog.getTryCount() >= 3) {// 最多重试3次
//					// 人工处理
//					messageLog.setStatus(12);
//					mapper.updateById(messageLog);
//					return;
//				}
//
//				messageLog.setTryCount(messageLog.getTryCount() + 1);
//				mapper.updateById(messageLog);
//				User user = JSON.parseObject(messageLog.getMessage(), User.class);
//				sender.send(user);
//			});
//		}
//	}
}
