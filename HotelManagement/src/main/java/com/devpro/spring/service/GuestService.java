package com.devpro.spring.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.devpro.spring.model.Guest;

public interface GuestService {
	
	Guest findGuest(Long id);
	
	Page<Guest> searchGuests(Pageable pageable,String text);
	
	List<Guest> searchGuests(String text);
	
	void addGuestInfo(Guest guest);
	
	void editGuestInfo(Guest guest);
	
	Guest searchGuestWithCart(String idCard);
	
	Integer checkExistGuest(String idCard);
	
	void updateComplete(String passport,String address,String phoneNumber,
			String email,String isVip,String idCard);
	
	void updateNomal(String guestName,String birth,String idCard,String passport,
			String address,String nationality,String phoneNumber,String email,Long guestId);
	
	Guest getGuestInfoByChamberNumber(String chamberNumber);

}
