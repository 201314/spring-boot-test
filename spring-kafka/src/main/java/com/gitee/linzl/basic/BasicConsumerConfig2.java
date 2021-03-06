package com.gitee.linzl.basic;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dreamcatchernick on 19/09/2017.
 */
@Configuration
@EnableKafka
public class BasicConsumerConfig2 {
	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${kafka.topic.basictopic}")
	private String topic;

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> basicKafkaListenerContainerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, topic);

		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		ConsumerFactory<String, String> basicConsumerFactory = new DefaultKafkaConsumerFactory<>(configProps,
				new StringDeserializer(), new JsonDeserializer<>(String.class));
		factory.setConsumerFactory(basicConsumerFactory);

		return factory;
	}
}
