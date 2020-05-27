package com.mindtree.ordermyfood.usermanagement.kafka.servicehandler;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mindtree.ordermyfood.usermanagement.controller.FeignServiceClient;
import com.mindtree.ordermyfood.usermanagement.dto.ItemDto;
import com.mindtree.ordermyfood.usermanagement.dto.RestaurantDto;
import com.mindtree.ordermyfood.usermanagement.dto.OrderDetails;
import com.mindtree.ordermyfood.usermanagement.entity.Item;
import com.mindtree.ordermyfood.usermanagement.exception.DataNotFoundException;
import com.mindtree.ordermyfood.usermanagement.exception.KafkaListnerException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
public class RestaurantValidator{

	private static final String TOPIC = "OMF_Order";
	private static final String REPLY_TOPIC = "responsetest";

	@Autowired 
	private ReplyingKafkaTemplate<String,Integer,RestaurantDto> kafkaTemplate;

	@Autowired
	private FeignServiceClient feignServiceClient;

	@HystrixCommand(fallbackMethod = "fallback")
	public boolean getRestaurantFromKafka(OrderDetails orderDetails) throws KafkaListnerException, DataNotFoundException {
		// create producer record
		ProducerRecord<String, Integer> record = new ProducerRecord<String, Integer>(TOPIC, orderDetails.getRestaurantId());
		// set reply topic in header
		record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, REPLY_TOPIC.getBytes()));
		// post in kafka topic
		RequestReplyFuture<String, Integer, RestaurantDto> sendAndReceive = kafkaTemplate.sendAndReceive(record);
		// confirm if producer produced successfully
		SendResult<String, Integer> sendResult=null;

		try {
			sendResult = sendAndReceive.getSendFuture().get();
		} catch (InterruptedException | ExecutionException exception) {
			throw new KafkaListnerException("Error occured while fetching details of restaurant",exception);
		}

		//print all headers	
		sendResult.getProducerRecord().headers().forEach(header -> { System.out.println("-------------------------------------------");
		System.out.println(header.key() + ":" + header.value().toString());});
		// get consumer record
		ConsumerRecord<String, RestaurantDto> consumerRecord=null;

		try {
			consumerRecord = sendAndReceive.get();
			System.out.println("consumerRecord--------------------------------"+consumerRecord);
		} catch (InterruptedException | ExecutionException exception) {
			System.out.println("error-----------------------------------");
			throw new KafkaListnerException("Error occured while fetching details of restaurant",exception);


		}			
		return validateOrderedItems(orderDetails,consumerRecord.value()); 		

	}

	private boolean validateOrderedItems(OrderDetails orderDetails, RestaurantDto restaurant) throws DataNotFoundException {

		Boolean itemValidateFlag =false;
		List<ItemDto> restaurantItemsList = restaurant.getItems();
		System.out.println("restaurant---------------------------"+restaurant);
		System.out.println("restaurantItemsList-------------------------"+restaurantItemsList);

		if(null == restaurantItemsList && restaurantItemsList.isEmpty()) {
			throw new DataNotFoundException("Items not found for id: "+restaurantItemsList);
		}

		List<ItemDto> unavailableItems =null;
		List<ItemDto> orderItems = orderDetails.getItems();

		/*
		 * unavailableItems = restaurantItemsList.stream() .map(item -> item.getId())
		 * .anyMatch( orderItems.stream() .map(item2 -> item2.getId())
		 * .collect(Collectors.toList()) ::contains);
		 */

		/*
		 * unavailableIdItems = restaurantItemsList.stream() .map(item -> item.getId())
		 * .filter(item -> orderItems.stream() .map(item2 ->
		 * item2.getId()).collect(Collectors.toList()).contains(item)
		 * ).collect(Collectors.toList());
		 */

		unavailableItems = orderItems.stream()
				.filter(t -> 
				orderItems.stream()
				.map(item -> item.getId())
				.filter(item -> 
				!(restaurantItemsList.stream()
						.map(item2 -> item2.getId()).collect(Collectors.toList()).contains(item))
						).collect(Collectors.toList()).contains(t.getId())
						).collect(Collectors.toList());
		/*
		 * unavailableItems = orderItems.stream() .filter(item ->
		 * !restaurantItemsList.contains(item)) .collect(Collectors.toList());
		 * list1.stream() .map(Object1::getProperty)
		 */

		if(unavailableItems != null && !unavailableItems.isEmpty()) {
			throw new DataNotFoundException("Items not found for id: "+unavailableItems.toString());
		}
		itemValidateFlag=true;
		return itemValidateFlag;
	}

	 boolean fallback(OrderDetails orderDetails) throws DataNotFoundException {
		 
		 
		RestaurantDto restaurant = feignServiceClient.getRestaurantDetails(orderDetails.getRestaurantId());		
		List<ItemDto> items = feignServiceClient.getItemDetails(orderDetails.getRestaurantId());
		restaurant.setItems(items);
		return  validateOrderedItems(orderDetails, restaurant);
	}

}
