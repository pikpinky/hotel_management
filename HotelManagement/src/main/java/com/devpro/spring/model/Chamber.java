package com.devpro.spring.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "chamber")
@JsonIgnoreProperties("rentals")
public class Chamber {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chamber_id")
	private Long chamberId;
	
	@Column(name = "chamber_number",unique = true)
	private String chamberNumber;
	
	@Column(name = "chamber_type")
	private String chamberType;
	
	@Column(name = "is_vip")
	private String isVip;
	
	@Column(name = "price_day")
	private String priceDay;
	
	@Column(name = "chamber_area")
	private String chamberArea;
	
	@Column(name = "note")
	private String note;
	
	public Chamber(String chamberNumber, String chamberType, String isVip, String priceDay, String chamberArea,
			String note, String isEmpty) {
		super();
		this.chamberNumber = chamberNumber;
		this.chamberType = chamberType;
		this.isVip = isVip;
		this.priceDay = priceDay;
		this.chamberArea = chamberArea;
		this.note = note;
		this.isEmpty = isEmpty;
	}

	@Column(name = "is_empty")
	private String isEmpty;
	
	@ManyToMany(mappedBy = "chambers")
	private Set<Rental> rentals;

	public Chamber() {
		super();
	}
	
	public Long getChamberId() {
		return chamberId;
	}

	public void setChamberId(Long chamberId) {
		this.chamberId = chamberId;
	}

	public String getChamberNumber() {
		return chamberNumber;
	}

	public void setChamberNumber(String chamberNumber) {
		this.chamberNumber = chamberNumber;
	}

	public String getChamberType() {
		return chamberType;
	}

	public void setChamberType(String chamberType) {
		this.chamberType = chamberType;
	}

	public String getIsVip() {
		return isVip;
	}

	public void setIsVip(String isVip) {
		this.isVip = isVip;
	}

	public String getPriceDay() {
		return priceDay;
	}

	public void setPriceDay(String priceDay) {
		this.priceDay = priceDay;
	}

	public String getChamberArea() {
		return chamberArea;
	}

	public void setChamberArea(String chamberArea) {
		this.chamberArea = chamberArea;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getIsEmpty() {
		return isEmpty;
	}

	public Set<Rental> getRentals() {
		return rentals;
	}

	public void setRentals(Set<Rental> rentals) {
		this.rentals = rentals;
	}

	public void setIsEmpty(String isEmpty) {
		this.isEmpty = isEmpty;
	}

	
}
