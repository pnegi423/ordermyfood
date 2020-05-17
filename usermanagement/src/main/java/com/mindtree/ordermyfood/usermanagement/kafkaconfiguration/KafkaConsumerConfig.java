package com.mindtree.ordermyfood.usermanagement.kafkaconfiguration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.mindtree.ordermyfood.usermanagement.dto.OrderDetails;
import com.mindtree.ordermyfood.usermanagement.dto.OrderResponseDto;


@EnableKafka
@Configuration
public class KafkaConsumerConfig {
 
	String bootstrapServers = "localhost:9092";
	
	String groupId = "group_id";
	
	@Bean
	public ReplyingKafkaTemplate<String, OrderDetails, OrderResponseDto> replyKafkaTemplate(ProducerFactory<String, OrderDetails> pf, KafkaMessageListenerContainer<String, OrderResponseDto> container) {
	  return new ReplyingKafkaTemplate<>(pf, container);
	}
	
	@Bean
	public ProducerFactory<String,OrderDetails> producerFactory() {
	  return new DefaultKafkaProducerFactory<>(producerConfigs());
	}
	
	@Bean
	public Map<String, Object> producerConfigs() {
	  Map<String, Object> props = new HashMap<>();
	  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
	            bootstrapServers);
	  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
	            StringSerializer.class);
	  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	  props.put("request.timeout.ms", 20000);
	  return props;
	}
	
	@Bean
	public KafkaMessageListenerContainer<String, OrderResponseDto> replyContainer(ConsumerFactory<String, OrderResponseDto> cf) {
	  ContainerProperties containerProperties = new ContainerProperties("responsetest");
	  return new KafkaMessageListenerContainer<>(cf, containerProperties);
	}
	
	
	// Default Consumer Factory
	@Bean
	public ConsumerFactory<String, OrderResponseDto> consumerFactory() {
		JsonDeserializer<OrderResponseDto> deserializer = new JsonDeserializer<>(OrderResponseDto.class);
	    deserializer.setRemoveTypeHeaders(false);
	    deserializer.addTrustedPackages("*");
	    deserializer.setUseTypeMapperForKey(true);
	  return new DefaultKafkaConsumerFactory<>(consumerConfigs(),new StringDeserializer(),deserializer);
	}
	// Concurrent Listner container factory
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OrderResponseDto>> kafkaListenerContainerFactory(
			ReplyingKafkaTemplate<String, OrderDetails, OrderResponseDto> template) {
	  ConcurrentKafkaListenerContainerFactory<String, OrderResponseDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
	  factory.setConsumerFactory(consumerFactory());
	  // NOTE - set up of reply template
	  factory.setReplyTemplate(kafkaTemplate());
	  factory.setErrorHandler(new SeekToCurrentErrorHandler(new DeadLetterPublishingRecoverer(template),1));
	  return factory;
	}
	// Standard KafkaTemplate
	@Bean
	public KafkaTemplate<String, OrderDetails> kafkaTemplate() {
	  return new KafkaTemplate<>(producerFactory());
	}
	
	// Standard KafkaProducer settings - specifying brokerand serializer 
	@Bean
	public Map<String, Object> consumerConfigs() {
	  Map<String, Object> props = new HashMap<>();
	  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
	            bootstrapServers);
	  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
	            StringSerializer.class);
	  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	  props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
	  props.put("request.timeout.ms", 20000);
	  return props;
	}
	/*
	 * @Bean public ConsumerFactory<String, String> consumerFactory() { Map<String,
	 * Object> props = new HashMap<>(); props.put(
	 * ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress); props.put(
	 * ConsumerConfig.GROUP_ID_CONFIG, groupId); props.put(
	 * ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	 * props.put( ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
	 * StringDeserializer.class); return new DefaultKafkaConsumerFactory<>(props); }
	 * 
	 * @Bean public ConcurrentKafkaListenerContainerFactory<String, String>
	 * kafkaListenerContainerFactory() {
	 * 
	 * ConcurrentKafkaListenerContainerFactory<String, String> factory = new
	 * ConcurrentKafkaListenerContainerFactory<>();
	 * factory.setConsumerFactory(consumerFactory()); return factory; }
	 */
}
