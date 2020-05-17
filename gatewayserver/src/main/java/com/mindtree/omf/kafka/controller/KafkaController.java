package com.mindtree.omf.kafka.controller;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindtree.omf.kafka.exception.KafkaProducerException;
import com.mindtree.omf.kafka.order.dto.OrderDetails;
import com.mindtree.omf.kafka.order.dto.OrderResponseDto;
import com.mindtree.omf.kafka.service.OrderProducerService;

@RestController
@RequestMapping(value = "/kafka/publish")
public class KafkaController {

	@Autowired
	OrderProducerService producer;

	@KafkaListener(topics="OMF_Order.DLT")
	public void dltListner(@Payload OrderResponseDto order, @Headers MessageHeaders headers) throws KafkaProducerException{
		String orderException = new String((byte[])headers.get("kafka_dlt-exception-stacktrace"), StandardCharsets.UTF_8);
	      if(orderException!= null) {	    	  
	    	  throw new KafkaProducerException("Exception occured in kafka producer "+orderException);
	      }
	}

	@PostMapping(value = "/customer/order")
	public OrderResponseDto sendMessageToKafkaTopic(@RequestBody OrderDetails orderDetails) throws InterruptedException, ExecutionException, TimeoutException{

		return producer.placeOrder(orderDetails);	

	}


}
