package com.mindtree.omf.kafka.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.mindtree.omf.kafka.order.dto.OrderDetails;
import com.mindtree.omf.kafka.order.dto.OrderResponseDto;

public interface OrderProducerService {

	OrderResponseDto placeOrder(OrderDetails orderDetails) throws InterruptedException, ExecutionException, TimeoutException;

}
