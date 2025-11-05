package com.devpro.spring.dto;

import com.devpro.spring.model.Guest;

public class GuestOrderFoodDto {
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public GuestOrderFoodDto(String name, String phone, String vip) {
		super();
		this.name = name;
		this.phone = phone;
		this.vip = vip;
	}
	public String getRentalId() {
		return rentalId;
	}
	public void setRentalId(String rentalId) {
		this.rentalId = rentalId;
	}
	public GuestOrderFoodDto(Guest guest,String rentalId) {
		super();
		this.name = guest.getGuestName();
		this.phone = guest.getPhoneNumber();
		this.vip = guest.getIsVip();
		this.rentalId = rentalId;
	}
	
	private String name;
	private String phone;
	private String vip;
	private String rentalId;
}
