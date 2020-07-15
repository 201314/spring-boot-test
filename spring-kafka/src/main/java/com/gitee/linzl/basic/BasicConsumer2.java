package com.gitee.linzl.basic;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Created by dreamcatchernick on 19/09/2017.
 */
@Component
public class BasicConsumer2 {

	@KafkaListener(topics = "sleuth", containerFactory = "basicKafkaListenerContainerFactory")
	public void receive(String basic) {
		System.out.println(basic);
	}
}
