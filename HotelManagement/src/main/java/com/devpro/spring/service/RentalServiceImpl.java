package com.devpro.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.model.Guest;
import com.devpro.spring.model.Rental;
import com.devpro.spring.repository.RentalRepository;

@Service
public class RentalServiceImpl implements RentalService{

	@Autowired
	private RentalRepository rentalRepository;

	
	@Override
	public void addRentalInfo(Rental rental) {
		// TODO Auto-generated method stub
		rentalRepository.save(rental);
	}

	@Override
	public List<String> getListChamberOrderFood() {
		// TODO Auto-generated method stub
		return rentalRepository.getListChamberOrderFood();
	}

	@Override
	public String getRentalIdOrderFood(String chamberNumber) {
		// TODO Auto-generated method stub
		return rentalRepository.getRentalIdOrderFood(chamberNumber);
	}

	@Override
	public Rental getRentalById(Long id) {
		// TODO Auto-generated method stub
		return rentalRepository.getOne(id);
	}

	@Override
	public Integer getCheckTotalFoodPrice(String chamberId) {
		// TODO Auto-generated method stub
		return rentalRepository.getCheckTotalFoodPrice(chamberId);
	}

	@Override
	public Integer getCheckTotalServicePrice(String chamberId) {
		// TODO Auto-generated method stub
		return rentalRepository.getCheckTotalServicePrice(chamberId);
	}

	@Override
	public Integer getNumberDaysStay(String chamberId) {
		// TODO Auto-generated method stub
		return rentalRepository.getNumberDaysStay(chamberId);
	}

	@Override
	public Rental getRentalCheckOutInfo(String chamberNumber) {
		// TODO Auto-generated method stub
		return rentalRepository.getRentalCheckOutInfo(chamberNumber);
	}

	@Override
	public Guest getGuestCheckOutInfo(String chamberNumber) {
		// TODO Auto-generated method stub
		return rentalRepository.getGuestCheckOutInfo(chamberNumber);
	}

	@Override
	public Chamber getChamberCheckOutInfo(String chamberNumber) {
		// TODO Auto-generated method stub
		return rentalRepository.getChamberCheckOutInfo(chamberNumber);
	}

}
