package com.devpro.spring.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "guest")
@JsonIgnoreProperties("rentals") // anotation giup bo di thuoc tinh ko muon binding ra json
public class Guest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "guest_id")
	private long guestId;
	
	@Column(name = "guest_name")
	private String guestName;
	
	public Guest(Long guestId, String guestName, String birth, String idCard, String passport, String address,
			String nationality, String phoneNumber, String isVip) {
		super();
		this.guestId = guestId;
		this.guestName = guestName;
		this.birth = birth;
		this.idCard = idCard;
		this.passport = passport;
		this.address = address;
		this.nationality = nationality;
		this.phoneNumber = phoneNumber;
		this.isVip = isVip;
	}

	@Column(name = "birth")
	private String birth;
	
	@Column(name = "id_card")
	private String idCard;
	
	@Column(name = "passport")
	private String passport;
	
	@Column(name = "address")
	private String address;

	@Column(name = "nationality")
	private String nationality;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "email")
	private String email;
	
	public String getEmail() {
		return email;
	}

	public Guest(String guestName, String birth, String idCard, String passport, String address, String nationality,
			String phoneNumber, String email, String isFamiliar, String isVip) {
		super();
		this.guestName = guestName;
		this.birth = birth;
		this.idCard = idCard;
		this.passport = passport;
		this.address = address;
		this.nationality = nationality;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.isFamiliar = isFamiliar;
		this.isVip = isVip;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIsFamiliar() {
		return isFamiliar;
	}

	public void setIsFamiliar(String isFamiliar) {
		this.isFamiliar = isFamiliar;
	}

	@Column(name = "is_familiar")
	private String isFamiliar;

	@Column(name = "is_vip")
	private String isVip;
	
	public Guest() {
		super();
	}

	@OneToMany(mappedBy = "guest",fetch = FetchType.LAZY)
	private Set<Rental> rentals;

	public long getGuestId() {
		return guestId;
	}

	public void setGuestId(long guestId) {
		this.guestId = guestId;
	}

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getIsVip() {
		return isVip;
	}

	public void setIsVip(String isVip) {
		this.isVip = isVip;
	}

	public Set<Rental> getRentals() {
		return rentals;
	}

	public void setRentals(Set<Rental> rentals) {
		this.rentals = rentals;
	}
	
}
