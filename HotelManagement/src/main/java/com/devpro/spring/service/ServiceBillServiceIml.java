package com.devpro.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devpro.spring.model.ServiceBill;
import com.devpro.spring.repository.ServiceBillRepository;

@Service
public class ServiceBillServiceIml implements ServiceBillService{
	
	@Autowired
	private ServiceBillRepository serviceBillRepository;

	@Override
	public void addServiceBill(ServiceBill bill) {
		// TODO Auto-generated method stub
		serviceBillRepository.save(bill);
	}
	
}
