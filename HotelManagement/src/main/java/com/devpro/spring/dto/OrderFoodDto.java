package com.devpro.spring.dto;

public class OrderFoodDto {
	
	private Long rentalId;
	private String peopleNumber;
	private String orderDate;
	private String note;
	private String discount;
	private String totalPrice;
	public OrderFoodDto(Long rentalId, String peopleNumber, String orderDate, String note, String discount,
			String totalPrice) {
		super();
		this.rentalId = rentalId;
		this.peopleNumber = peopleNumber;
		this.orderDate = orderDate;
		this.note = note;
		this.discount = discount;
		this.totalPrice = totalPrice;
	}
	public Long getRentalId() {
		return rentalId;
	}
	public void setRentalId(Long rentalId) {
		this.rentalId = rentalId;
	}
	public String getPeopleNumber() {
		return peopleNumber;
	}
	public void setPeopleNumber(String peopleNumber) {
		this.peopleNumber = peopleNumber;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
}
