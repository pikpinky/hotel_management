package com.devpro.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devpro.spring.dto.CheckOutDto;
import com.devpro.spring.model.Chamber;
import com.devpro.spring.model.Guest;
import com.devpro.spring.model.Rental;
import com.devpro.spring.service.RentalService;

@Controller
public class CheckOutController {
	
	@Autowired
	private RentalService rentalService;
	
	@GetMapping("/check-out")
	public String showCheckOut(Model model) {
		model.addAttribute("list_chambers", rentalService.getListChamberOrderFood());
		return "check-out";
	}
	
	@GetMapping("/check-out/get-check-out-info")
	@ResponseBody
	public CheckOutDto getCheckOutInfo(@RequestParam("number") String chamberNumber) {
		Rental rental = rentalService.getRentalCheckOutInfo(chamberNumber);
		Guest guest = rentalService.getGuestCheckOutInfo(chamberNumber);
		Chamber chamber = rentalService.getChamberCheckOutInfo(chamberNumber);
		Integer totalFood = rentalService .getCheckTotalFoodPrice(chamberNumber);
		Integer totalService = rentalService.getCheckTotalServicePrice(chamberNumber);
		Integer numberDaysStay = rentalService.getNumberDaysStay(chamberNumber);
		CheckOutDto info = null;
		try {
			info = new CheckOutDto(rental,chamber,guest, numberDaysStay, totalFood, totalService);
		}catch (NullPointerException e) {
			System.out.println("Null...................");
		}
		return info ;
	}

}
