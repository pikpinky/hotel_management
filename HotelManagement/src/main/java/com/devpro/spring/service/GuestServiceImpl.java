package com.devpro.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devpro.spring.model.Guest;
import com.devpro.spring.repository.GuestRepository;

@Service
public class GuestServiceImpl implements GuestService{

	@Autowired
	private GuestRepository guestRepository;
	
	@Override
	public Guest findGuest(Long id) {
		// TODO Auto-generated method stub
		return guestRepository.getOne(id);
	}

	@Override
	public void addGuestInfo(Guest guest) {
		// TODO Auto-generated method stub
		guestRepository.save(guest);
	}

	@Override
	public void editGuestInfo(Guest guest) {
		// TODO Auto-generated method stub
		guestRepository.save(guest);
	}

	@Override
	public Page<Guest> searchGuests(Pageable pageable, String text) {
		// TODO Auto-generated method stub
		return guestRepository.searchGuests(pageable,"%"+text.trim()+"%");
		// "%"+text.trim()+"%" xu ly tu khoa(vd: ___Huong..! -> %Huong..!%)
	}

	@Override
	public List<Guest> searchGuests(String text) {
		// TODO Auto-generated method stub
		return guestRepository.searchGuests("%"+text.trim()+"%");
	}

	@Override
	public Guest searchGuestWithCart(String idCard) {
		// TODO Auto-generated method stub
		return guestRepository.searchGuestWithCart(idCard);
	}

	@Override
	public Integer checkExistGuest(String idCard) {
		// TODO Auto-generated method stub
		return guestRepository.checkExistGuest(idCard);
	}
	
	@Override
	public void updateComplete(String passport, String address, String phoneNumber, String email, String isVip,
			String idCard) {
		
		guestRepository.updateComplete(passport, address, phoneNumber, email, "true", isVip, idCard);
		// mac dinh nhu update check in ma khach da co trong csdl thi familiar = true
	}

	@Override
	public void updateNomal(String guestName, String birth, String idCard, String passport, String address,
			String nationality, String phoneNumber, String email, Long guestId) {
		
		guestRepository.updateNomal(guestName, birth, idCard, passport, address, nationality, phoneNumber, email, guestId);
	}

	@Override
	public Guest getGuestInfoByChamberNumber(String chamberNumber) {
		// TODO Auto-generated method stub
		return guestRepository.getGuestInfoByChamberNumber(chamberNumber);
	}


}
