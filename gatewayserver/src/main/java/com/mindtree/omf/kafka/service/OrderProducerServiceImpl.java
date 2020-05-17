package com.mindtree.omf.kafka.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.mindtree.omf.kafka.order.dto.OrderDetails;
import com.mindtree.omf.kafka.order.dto.OrderResponseDto;
import com.mindtree.omf.kafka.order.dto.OrderDetails;

@Service
public class OrderProducerServiceImpl implements OrderProducerService{

	private static final String TOPIC = "OMF_Order";
	private static final String REPLY_TOPIC = "responsetest";
	

	  @Autowired 
	  private ReplyingKafkaTemplate<String,OrderDetails,OrderResponseDto> kafkaTemplate;
	 

	
	@Override
	public OrderResponseDto placeOrder(OrderDetails orderDetails) throws InterruptedException, ExecutionException, TimeoutException {

		  // create producer record
		  ProducerRecord<String, OrderDetails> record = new ProducerRecord<String, OrderDetails>(TOPIC, orderDetails);
		  // set reply topic in header
		  record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, REPLY_TOPIC.getBytes()));
		  // post in kafka topic
		  RequestReplyFuture<String, OrderDetails, OrderResponseDto> sendAndReceive = kafkaTemplate.sendAndReceive(record);
		  // confirm if producer produced successfully
		  SendResult<String, OrderDetails> sendResult = sendAndReceive.getSendFuture().get();
		  //print all headers
		  sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));
		  // get consumer record
		  ConsumerRecord<String, OrderResponseDto> consumerRecord = sendAndReceive.get();
		
		  return consumerRecord.value();
		/*
		 * ObjectMapper mapper = new ObjectMapper(); try { String json =
		 * mapper.writeValueAsString(orderDetails);
		 * //System.out.println("ResultingJSONstring = " + json);
		 * this.kafkaTemplate.send(TOPIC,json); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
				
	}
}
