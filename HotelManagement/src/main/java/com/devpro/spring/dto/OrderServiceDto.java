package com.devpro.spring.dto;

public class OrderServiceDto {

	private Long rentalId;
	private String orderDate;
	public OrderServiceDto(Long rentalId, String orderDate, String note, String discount, String totalPrice) {
		super();
		this.rentalId = rentalId;
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
	private String note;
	private String discount;
	private String totalPrice;
}
