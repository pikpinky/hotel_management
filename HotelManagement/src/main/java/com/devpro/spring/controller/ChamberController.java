package com.devpro.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devpro.spring.model.Chamber;
import com.devpro.spring.service.ChamberService;

@Controller
public class ChamberController {

	@Autowired
	private ChamberService chamberService;

	@GetMapping("/chamber")
	public String showChamberInfo(Model model, @RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "search-text", defaultValue = "") String text) {

		Pageable pageable = PageRequest.of(page, 10); // mac dinh 10 ban ghi 1 trang
		Page<Chamber> pages = chamberService.searchChamber(pageable, text);

		int current = pages.getNumber() + 1;
		long total = pages.getTotalPages();
		long totalElement = pages.getTotalElements();
		long begin = 1;
		long end = 1;
		if (current > 5 && total > 6) {
			begin = Math.max(1, current);
		}
		if (total != 0) {
			end = Math.min(begin + 4, total);
		}
		if (current == total - 5) {
			end = total;
		}
		boolean extra = false;
		boolean checkLast = false;
		if (total > 5 && current < total - 5) {
			extra = true;
		}
		if (total > 6 && current < total - 5) {
			checkLast = true;
		}
		String baseUrl = "/chamber?page=";
		String searchUrl = "&search-text=" + text;

		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", total);
		model.addAttribute("totalElement", totalElement);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("chambers", pages);
		model.addAttribute("extra", extra);
		model.addAttribute("checkLast", checkLast);
		model.addAttribute("searchUrl", searchUrl);
		model.addAttribute("searchText", text);
		return "chamber";
	}

	@GetMapping("/find-chamber")
	@ResponseBody
	public Chamber findChamber(Long id) {
		return chamberService.findChamber(id);
	}

	@PostMapping("/delete-chamber")
	public String deleteChamber(@RequestParam(name = "id") Long chamberId, @RequestParam(name = "page") int page,
			@RequestParam(name = "text") String text) {

		chamberService.deleteChamber(chamberId);
		return "redirect:/chamber?page=" + page + "&search-text=" + text;
	}

	@PostMapping("/update-chamber")
	public String updateChamber(@RequestParam(name = "id") Long chamberId, @RequestParam(name = "page") int page,
			@RequestParam(name = "text") String text, @RequestParam(name = "number") String number,
			@RequestParam(name = "type") String type, @RequestParam(name = "price") String price,
			@RequestParam(name = "note") String note, @RequestParam(name = "area") String area,
			@RequestParam(name = "vip", required = false,defaultValue = "") String vip) {

		String fvip = "false";
		if (vip.equalsIgnoreCase("on"))
			fvip = "true";
		if (chamberId == -1) {
			chamberService.addChamber(number, type, price, area, note, fvip);
		} else {
			chamberService.updateChamberInfo(number, type, price, area, note, fvip, chamberId);
		}
		return "redirect:/chamber?page=" + page + "&search-text=" + text;
	}
}
