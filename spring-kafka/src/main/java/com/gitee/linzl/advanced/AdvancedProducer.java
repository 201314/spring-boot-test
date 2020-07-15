package com.gitee.linzl.advanced;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by dreamcatchernick on 22/09/2017.
 */
@Component
public class AdvancedProducer {
	@Autowired
	@Qualifier("advancedKafkaTemplate")
	private KafkaTemplate<String, String> kafkaTemplate;

	public void send(String topic, String payload, int messageCount) {
		for (int i = 0; i < messageCount; i++) {
			String message = String.format("Message %d %s", i, payload);
			System.out.println("message===>" + message);
			kafkaTemplate.send(topic, message);
		}
	}
}
