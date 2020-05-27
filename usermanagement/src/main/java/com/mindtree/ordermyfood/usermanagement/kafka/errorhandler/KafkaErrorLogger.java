package com.mindtree.ordermyfood.usermanagement.kafka.errorhandler;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.mindtree.ordermyfood.usermanagement.dto.OrderResponseDto;

@Component
public class KafkaErrorLogger {

	private static Logger logger = LoggerFactory.getLogger(KafkaErrorLogger.class);
	
	@KafkaListener(topics="OMF_Order.DLT",
			containerFactory = "listenerFactoryInt")
	public void dltListner(@Payload Integer order, @Headers MessageHeaders headers) {
		String orderException = new String((byte[])headers.get("kafka_dlt-exception-stacktrace"), StandardCharsets.UTF_8);
		//System.out.println("}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}"+orderException+"{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{");
		logger.info("Error occured in kafka listner with details:"+orderException);
	}
}
