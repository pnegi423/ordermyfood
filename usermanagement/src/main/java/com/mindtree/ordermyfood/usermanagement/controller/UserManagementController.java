package com.mindtree.ordermyfood.usermanagement.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mindtree.ordermyfood.usermanagement.dto.ItemResponseDto;
import com.mindtree.ordermyfood.usermanagement.dto.OrderDetails;
import com.mindtree.ordermyfood.usermanagement.dto.OrderResponseDto;
import com.mindtree.ordermyfood.usermanagement.dto.UserDetailsDto;
import com.mindtree.ordermyfood.usermanagement.dto.UserResponseDto;
import com.mindtree.ordermyfood.usermanagement.exception.KafkaListnerException;
import com.mindtree.ordermyfood.usermanagement.exception.UserMangementException;
import com.mindtree.ordermyfood.usermanagement.service.UserManagementService;

@RestController
@Validated
@RequestMapping("api/v1")
public class UserManagementController {

	@Autowired
	UserManagementService userManagementService;

	@GetMapping("/customer/price")
	public @ResponseBody ResponseEntity<ItemResponseDto> getPricingDetails(@RequestParam(value="item") @NotNull List<Integer> itemList) throws UserMangementException {

		return new ResponseEntity<ItemResponseDto>(userManagementService.getPricingDetails(itemList), HttpStatus.OK);
	}

	@PostMapping("/customer/user")
	@ResponseBody
	public ResponseEntity<UserResponseDto> userDetails(@RequestBody @Valid UserDetailsDto userDetails ) throws UserMangementException {

		return new ResponseEntity<UserResponseDto>(userManagementService.userDetails(userDetails), HttpStatus.OK);
	}

	@GetMapping("/login/{id}")
	@ResponseBody
	public ResponseEntity<UserResponseDto> userLogin(@PathVariable("id") @Positive @NotNull int id ) throws UserMangementException {

		return new ResponseEntity<UserResponseDto>(userManagementService.userLogin(id),HttpStatus.OK);

	}
	
	/*
	 * @PostMapping("/customer/order")
	 * 
	 * @ResponseBody public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody
	 * OrderDetails orderDetails ) {
	 * 
	 * return new ResponseEntity<OrderResponseDto>(userManagementService.placeOrder(
	 * orderDetails), HttpStatus.OK); }
	 */



	@KafkaListener(topics = "OMF_Order",groupId = "group_id")
	@SendTo
	public OrderResponseDto listen(OrderDetails orderDetails) throws JsonMappingException, JsonProcessingException, InterruptedException, KafkaListnerException{
		
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++in listner++++++++++++++++++++++++++++++++++");
		try {
		return userManagementService.placeOrder(orderDetails);	
		}catch(UserMangementException exception) {
			throw new KafkaListnerException("Exception occured in kafka listner");
		}
		
		/*
		 * //logger.info(String.format("$$ -> Consumed Message -> %s",message));
		 * System.out.println("+++++++++++++++++++++++++++++++++++++++++++"+orderDetails
		 * .toString());
		 */
	}
}
