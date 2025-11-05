package com.devpro.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devpro.spring.model.OrderFood;
import com.devpro.spring.repository.OrderFoodRepository;

@Service
public class OrderFoodServiceImpl implements OrderFoodService{
	
	@Autowired
	private OrderFoodRepository orderFoodRepository;

	@Override
	public void addOrderFood(OrderFood orderFood) {
		// TODO Auto-generated method stub
		orderFoodRepository.save(orderFood);
	}

}
