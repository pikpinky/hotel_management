package com.devpro.spring.api;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devpro.spring.dto.OrderFoodDto;
import com.devpro.spring.dto.OrderServiceDto;
import com.devpro.spring.model.AjaxResponseBody;
import com.devpro.spring.model.OrderFood;
import com.devpro.spring.model.Rental;
import com.devpro.spring.model.ServiceBill;
import com.devpro.spring.service.OrderFoodService;
import com.devpro.spring.service.RentalService;
import com.devpro.spring.service.ServiceBillService;

@RestController
public class OrderApi {

	@Autowired
	private OrderFoodService orderFoodService;

	@Autowired
	private ServiceBillService serviceBillService;

	@Autowired
	private RentalService rentalService;

	@Transactional(rollbackFor = Exception.class) // update data
	@PostMapping("/order/order-food")
	public ResponseEntity<?> addOrderFood(@Valid @RequestBody OrderFoodDto order, Errors error) {

		AjaxResponseBody result = new AjaxResponseBody();
		if (error.hasErrors()) {
			result.setMessage(
					error.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
			return ResponseEntity.badRequest().body(result);
		}

		Rental rental = rentalService.getRentalById(order.getRentalId());
		OrderFood orderFood = new OrderFood(order.getTotalPrice(), order.getPeopleNumber(), order.getOrderDate(),
				order.getDiscount(), order.getNote(), rental);

		orderFoodService.addOrderFood(orderFood);

		result.setMessage("Lưu thành công!");
		return ResponseEntity.ok(result);
	}

	@Transactional(rollbackFor = Exception.class)
	@PostMapping("/order/order-service")
	public ResponseEntity<?> addOrderService(@Valid @RequestBody OrderServiceDto order, Errors error) {

		AjaxResponseBody result = new AjaxResponseBody();
		if (error.hasErrors()) {
			result.setMessage(
					error.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
			return ResponseEntity.badRequest().body(result);
		}

		Rental rental = rentalService.getRentalById(order.getRentalId());
		ServiceBill bill = new ServiceBill(order.getTotalPrice(), order.getOrderDate(), order.getDiscount(),
				order.getNote(), rental);
		serviceBillService.addServiceBill(bill);

		result.setMessage("Lưu thành công!");
		return ResponseEntity.ok(result);

	}

}
