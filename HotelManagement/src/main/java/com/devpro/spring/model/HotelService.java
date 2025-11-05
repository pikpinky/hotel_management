package com.devpro.spring.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service")
public class HotelService implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HotelService() {
		super();
	}

	public HotelService(String name, String price, String unit, String description, String note) {
		super();
		this.name = name;
		this.price = price;
		this.unit = unit;
		this.description = description;
		this.note = note;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "service_id")
	private Long id;
	
	@Column(name = "service_name")
	private String name;
	
	@Column(name = "price")
	private String price;
	
	@Column(name = "unit")
	private String unit;
	
	@Column(name = "service_description")
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HotelService(Long id, String name, String price, String unit, String description, String note) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.unit = unit;
		this.description = description;
		this.note = note;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = "note")
	private String note;
	
}
