package com.devpro.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devpro.spring.model.HotelService;
import com.devpro.spring.repository.HotelServiceRepository;

@Service
public class HotelServiceServiceImpl implements HotelServiceService{
	
	@Autowired
	private HotelServiceRepository hotelServiceRepository;

	@Override
	public List<HotelService> loadHotel() {
		// TODO Auto-generated method stub
		return hotelServiceRepository.findAll();
	}

	@Override
	public HotelService getService(Long id) {
		// TODO Auto-generated method stub
		return hotelServiceRepository.getOne(id);
	}

	@Override
	public void saveService(HotelService service) {
		// TODO Auto-generated method stub
		hotelServiceRepository.save(service);
	}

	@Override
	public void deleteService(Long id) {
		// TODO Auto-generated method stub
		hotelServiceRepository.deleteById(id);
	}

}
