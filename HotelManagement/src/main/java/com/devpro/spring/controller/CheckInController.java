package com.devpro.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.service.ChamberService;

@Controller
public class CheckInController {

	@Autowired
	private ChamberService chamberService;

	@GetMapping("/check-in")
	public String checkIn(Model model, @RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "p", defaultValue = "2") Integer price,
			@RequestParam(name = "t", defaultValue = "single") String type,
			@RequestParam(name = "v", defaultValue = "true") String vip) {

		Pageable pageable = PageRequest.of(page, 12);
		Page<Chamber> pages;
		boolean checkPrice1 = false;
		boolean checkPrice2 = false;
		boolean checkPrice3 = false;
		boolean checkType1 = false;
		boolean checkType2 = false;
		boolean checkType3 = false;
		boolean checkVip1 = false;
		boolean checkVip2 = false;

		switch (price) {
		case 1:
			pages = chamberService.searchChamberWithPrice1(pageable, type, vip);
			checkPrice1 = true;
			break;
		case 2:
			pages = chamberService.searchChamberWithPrice2(pageable, type, vip);
			checkPrice2 = true;
			break;
		default:
			pages = chamberService.searchChamberWithPrice3(pageable, type, vip);
			checkPrice3 = true;
			break;
		}

		switch (vip) {
		case "true":
			checkVip1 = true;
			break;
		default:
			checkVip2 = true;
			break;
		}

		switch (type) {
		case "single":
			checkType1 = true;
			break;
		case "couple":
			checkType2 = true;
			break;
		default:
			checkType3 = true;
			break;
		}

		int current = pages.getNumber() + 1;
		long total = pages.getTotalPages();
		long totalElement = pages.getTotalElements();
		long begin = 1;
		long end = 1;
		if(current>5 && total>6) {
			begin = Math.max(1, current);
		}
		if(total!=0) {
			end = Math.min(begin + 4, total);
		}
		if(current==total-5) {
			end = total;
		}
		boolean extra = false;
		boolean checkLast = false;
		if(total >5 && current<total-5) {
			extra = true;
		}
		if(total >6 && current<total-5) {
			checkLast = true;
		}
		String baseUrl = "/check-in?page=";
		String filterUrl = "&p="+price+"&t="+type+"&v="+vip;


		model.addAttribute("checkPrice1", checkPrice1);
		model.addAttribute("checkPrice2", checkPrice2);
		model.addAttribute("checkPrice3", checkPrice3);
		model.addAttribute("checkType1", checkType1);
		model.addAttribute("checkType2", checkType2);
		model.addAttribute("checkType3", checkType3);
		model.addAttribute("checkVip1", checkVip1);
		model.addAttribute("checkVip2", checkVip2);
		model.addAttribute("currentPrice", price);
		model.addAttribute("currentType", type);
		model.addAttribute("currentVip", vip);

		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", total);
		model.addAttribute("totalElement", totalElement);
		model.addAttribute("chambers", pages);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("filterUrl", filterUrl);
		model.addAttribute("extra", extra);
		model.addAttribute("checkLast", checkLast);
		return "check-in";
	}

}
