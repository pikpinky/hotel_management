package com.devpro.spring.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "rental")
public class Rental implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rental_id")
	private Long rentalId;
	
	@Column(name = "discount")
	private String discount;
	
	@Column(name = "check_in_date")
	private Date checkInDate;
	
	@Column(name = "check_out_date")
	private Date checkOutDate;
	
	@Column(name = "paid")
	private String paid;
	
	@Column(name = "note")
	private String note;
	
	@ManyToOne
	@JoinColumn(name = "guest_id",nullable = false)
	private Guest guest;
	
    @ManyToMany
    @JoinTable(name = "rental_chamber",
        joinColumns = @JoinColumn(name = "rental_id"),
        inverseJoinColumns = @JoinColumn(name = "chamber_id"))
    private Set<Chamber> chambers;
	
	public Long getRentalId() {
		return rentalId;
	}

	public void setRentalId(Long rentalId) {
		this.rentalId = rentalId;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}
	
	public Date getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}

	public Set<Chamber> getChambers() {
		return chambers;
	}

	public void setChambers(Set<Chamber> chambers) {
		this.chambers = chambers;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="payment_id", nullable=false)
    private Payment payment;
	
	@OneToMany(mappedBy = "rental")
	private Set<OrderFood> orderFoods;

	public Set<OrderFood> getOrderFoods() {
		return orderFoods;
	}

	public void setOrderFoods(Set<OrderFood> orderFoods) {
		this.orderFoods = orderFoods;
	}
	
	@OneToMany(mappedBy = "rental")
	private Set<ServiceBill> serviceBills;

	public Set<ServiceBill> getServiceBills() {
		return serviceBills;
	}

	public void setServiceBills(Set<ServiceBill> serviceBills) {
		this.serviceBills = serviceBills;
	}
	
}
