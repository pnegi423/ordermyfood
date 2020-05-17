package com.mindtree.ordermyfood.usermanagement.dto;
  
  import java.util.List;
  
  public class OrderDetails{
	  
  private boolean anonymousFlag;
  private double totalCost;
  private String email;
  private long phoneNumber;
  
  private int customerId; 
  private String deliveryPersonName;
  private String deliveryPersonContact;
  
  private int restaurantId; 
  private boolean payFlag;
  
  private List<Integer> itemsId;

public boolean isAnonymousFlag() {
	return anonymousFlag;
}

public void setAnonymousFlag(boolean anonymousFlag) {
	this.anonymousFlag = anonymousFlag;
}

public double getTotalCost() {
	return totalCost;
}

public void setTotalCost(double totalCost) {
	this.totalCost = totalCost;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

public long getPhoneNumber() {
	return phoneNumber;
}

public void setPhoneNumber(long phoneNumber) {
	this.phoneNumber = phoneNumber;
}

public int getCustomerId() {
	return customerId;
}

public void setCustomerId(int customerId) {
	this.customerId = customerId;
}

public String getDeliveryPersonName() {
	return deliveryPersonName;
}

public void setDeliveryPersonName(String deliveryPersonName) {
	this.deliveryPersonName = deliveryPersonName;
}

public String getDeliveryPersonContact() {
	return deliveryPersonContact;
}

public void setDeliveryPersonContact(String deliveryPersonContact) {
	this.deliveryPersonContact = deliveryPersonContact;
}

public int getRestaurantId() {
	return restaurantId;
}

public void setRestaurantId(int restaurantId) {
	this.restaurantId = restaurantId;
}

public boolean isPayFlag() {
	return payFlag;
}

public void setPayFlag(boolean payFlag) {
	this.payFlag = payFlag;
}

public List<Integer> getItemsId() {
	return itemsId;
}

public void setItemsId(List<Integer> itemsId) {
	this.itemsId = itemsId;
}

@Override
public String toString() {
	return "OrderDetails [anonymousFlag=" + anonymousFlag + ", totalCost=" + totalCost + ", email=" + email
			+ ", phoneNumber=" + phoneNumber + ", customerId=" + customerId + ", deliveryPersonName="
			+ deliveryPersonName + ", deliveryPersonContact=" + deliveryPersonContact + ", restaurantId=" + restaurantId
			+ ", payFlag=" + payFlag + ", itemsId=" + itemsId + "]";
}

   
  
  }
 