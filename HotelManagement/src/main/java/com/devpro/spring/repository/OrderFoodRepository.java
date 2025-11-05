package com.devpro.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devpro.spring.model.OrderFood;

@Repository
public interface OrderFoodRepository extends JpaRepository<OrderFood, Long>{

}
