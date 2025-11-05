package com.devpro.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devpro.spring.model.HotelService;

@Repository
public interface HotelServiceRepository extends JpaRepository<HotelService, Long>{
	
}
