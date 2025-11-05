package com.devpro.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devpro.spring.model.ServiceBill;

@Repository
public interface ServiceBillRepository extends JpaRepository<ServiceBill, Long>{

}
