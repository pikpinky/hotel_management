package com.devpro.spring.dto;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.model.Guest;
import com.devpro.spring.model.Rental;

public class CheckOutDto {
	private String type;
	private String price;
	private String vip;

	private String name;
	private String birth;
	private String address;
	private String phone;
	private String card;
	private String passport;
	private String nationality;
	private String email;

	private String checkInDate;
	private String numberStay;
	private String total;
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	private String rentalId;

	public String getRentalId() {
		return rentalId;
	}

	public void setRentalId(String rentalId) {
		this.rentalId = rentalId;
	}

	private String rent;
	private String food;
	private String service;
	private String note;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CheckOutDto() {
		super();
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(String checkInDate) {
		this.checkInDate = checkInDate;
	}

	public String getNumberStay() {
		return numberStay;
	}

	public void setNumberStay(String numberStay) {
		this.numberStay = numberStay;
	}

	public String getRent() {
		return rent;
	}

	public void setRent(String rent) {
		this.rent = rent;
	}

	public String getFood() {
		return food;
	}

	public void setFood(String food) {
		this.food = food;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public CheckOutDto(Rental rental, Chamber chamber, Guest guest, Integer numberStay, Integer totalPriceFood,
			Integer totalPriceService) {
		int rentT;int foodT;int serviceT; 
		if(numberStay==0 || numberStay == null) {
			this.numberStay = "0";
			rentT = (int) (Integer.parseInt(chamber.getPriceDay())*0.5);
			this.rent = String.valueOf(rentT);
			// tra phong trong ngay lay nua so tien
		}else {
			this.numberStay = String.valueOf(numberStay);
			rentT = (int)Integer.parseInt(chamber.getPriceDay())*numberStay;
			this.rent = String.valueOf(rentT);
		}
		
		if(totalPriceFood==null) {
			this.food = "0";
			foodT = 0;
		}else {
			foodT = totalPriceFood;
			this.food = String.valueOf(foodT);
		}
		
		if(totalPriceService==null) {
			this.service = "0";
			serviceT = 0;
		}else {
			serviceT = totalPriceService;
			this.service = String.valueOf(serviceT);
		}
		
		this.type = chamber.getChamberType();
		this.price = chamber.getPriceDay();
		this.vip = chamber.getIsVip();
		this.name = guest.getGuestName();
		this.birth = guest.getBirth();
		this.address = guest.getAddress();
		this.phone = guest.getPhoneNumber();
		this.card = guest.getIdCard();
		this.passport = guest.getPassport();
		this.nationality = guest.getNationality();
		this.email = guest.getEmail();
		this.checkInDate = String.valueOf(rental.getCheckInDate());
		this.rentalId = String.valueOf(rental.getRentalId());
		this.note = rental.getNote();
		this.total = String.valueOf(rentT+foodT+serviceT);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
