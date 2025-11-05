package com.devpro.spring.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_food")
public class OrderFood {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "order_food_id")
    private Long id;
	
	public OrderFood(String totalPrice, String peopleNumber, String orderDate, String discount, String note,
			Rental rental) {
		super();
		this.totalPrice = totalPrice;
		this.peopleNumber = peopleNumber;
		this.orderDate = orderDate;
		this.discount = discount;
		this.note = note;
		this.rental = rental;
	}

	@Column(name = "total_price")
	private String totalPrice;
	
	@Column(name = "people_number")
	private String peopleNumber;
	
	@Column(name = "order_date")
	private String orderDate;
	
	@Column(name = "discount")
	private String discount;
	
	@Column(name = "note")
	private String note;
	
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "rental_id",nullable = false)
	private Rental rental;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
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

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Rental getRental() {
		return rental;
	}

	public void setRental(Rental rental) {
		this.rental = rental;
	}
}
