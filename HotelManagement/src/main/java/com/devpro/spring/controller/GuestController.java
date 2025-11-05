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

import com.devpro.spring.model.Guest;
import com.devpro.spring.service.GuestService;;

@Controller
public class GuestController {

	@Autowired
	private GuestService guestService;

	@GetMapping("/guests")
	public String loadListGuests(Model model, 
			@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "search-text",defaultValue = "") String text) {

		Pageable pageable = PageRequest.of(page, 10); // mac dinh 10 ban ghi 1 trang
		Page<Guest> pages = guestService.searchGuests(pageable, text);
		
		int current = pages.getNumber() + 1;
		long total = pages.getTotalPages();
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
		String baseUrl = "/guests?page=";
		String searchUrl = "&search-text="+text;

		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", total);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("guests", pages);
		model.addAttribute("extra", extra);
		model.addAttribute("checkLast", checkLast);
		model.addAttribute("searchUrl", searchUrl);
		model.addAttribute("searchText", text);
		return "guest";
	}

	@PostMapping("/update-guest-info")
	public String updateGuestInfo(@RequestParam(name = "id") Long guestId,
			@RequestParam(name = "name") String guestName, @RequestParam(name = "card") String idCard,
			@RequestParam(name = "birth") String birth, @RequestParam(name = "passport") String passport,
			@RequestParam(name = "address") String address, @RequestParam(name = "nationality") String nationality,
			@RequestParam(name = "phone") String phoneNumber,@RequestParam(name = "email") String email,
			@RequestParam(name = "page") int page,
			@RequestParam(name = "text") String text) {

		guestService.updateNomal(guestName, birth, idCard, passport, address, nationality, phoneNumber, email, guestId);
		return "redirect:/guests?page="+page+"&search-text="+text;
	}

	@GetMapping("/find-guest")
	@ResponseBody
	public Guest findOneGuest(Long id) {
		return guestService.findGuest(id);
	}
}
