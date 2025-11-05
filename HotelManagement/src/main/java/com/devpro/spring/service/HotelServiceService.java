package com.devpro.spring.service;

import java.util.List;

import com.devpro.spring.model.HotelService;

public interface HotelServiceService {
	
	List<HotelService> loadHotel();
	
	HotelService getService(Long id);
	
	void saveService(HotelService service);
	
	void deleteService(Long id);
}
