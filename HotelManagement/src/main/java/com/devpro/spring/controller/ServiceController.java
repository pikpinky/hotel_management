package com.devpro.spring.controller;

import java.util.List;

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

import com.devpro.spring.dto.FoodItemDto;
import com.devpro.spring.model.Category;
import com.devpro.spring.model.FoodItem;
import com.devpro.spring.model.HotelService;
import com.devpro.spring.service.CategoryService;
import com.devpro.spring.service.FoodItemService;
import com.devpro.spring.service.HotelServiceService;

@Controller
public class ServiceController {

	@Autowired
	private FoodItemService foodItemService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private HotelServiceService hotelServiceService;
	
	@GetMapping("/service")
	public String showService(Model model,
			@RequestParam(name = "page",defaultValue = "0") int page,
			@RequestParam(name = "search-text",defaultValue = "") String text) {
		
		Pageable pageable = PageRequest.of(page, 10); // mac dinh 10 ban ghi 1 trang
		Page<FoodItemDto> pages = foodItemService.getListFoodItem(pageable, text);
		List<HotelService> services = hotelServiceService.loadHotel();
		
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
		String baseUrl = "/service?page=";
		String searchUrl = "&search-text="+text;

		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", total);
		model.addAttribute("totalElement", totalElement);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("foods", pages);
		model.addAttribute("extra", extra);
		model.addAttribute("checkLast", checkLast);
		model.addAttribute("searchUrl", searchUrl);
		model.addAttribute("searchText", text);
		
		model.addAttribute("categories", categoryService.loadListCategories());
		model.addAttribute("services", services);
		return "service";
	}
	
	@GetMapping("/service/find-food")
	@ResponseBody
	public FoodItem findFood(Long id) {
		return foodItemService.getItem(id);
	}
	
	@GetMapping("/service/find-service")
	@ResponseBody
	public HotelService findService(Long id) {
		return hotelServiceService.getService(id);
	}
	
	@PostMapping("/service/update-food")
	public String addFood(@RequestParam(name = "id",defaultValue = "0") Long id,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "price") String price,
			@RequestParam(name = "description") String description,
			@RequestParam(name = "category") Long categoryId) {
		
		FoodItem item = null;
		Category category = categoryService.getOne(categoryId);
		if(id==0) {
			item = new FoodItem(name, description, price, "", category);
		}else {
			item = new FoodItem(id, name, description, price, "", category);
		}
		foodItemService.saveFoodItem(item);
		return "redirect:/service";
	}
	
	@PostMapping("/service/delete-food")
	public String deleteFood(@RequestParam(name = "id")Long id) {
		foodItemService.deleteFoodItem(id);
		return "redirect:/service";
	}
	
	@PostMapping("/service/update-service")
	public String addService(@RequestParam(name = "id-S",defaultValue = "0") Long id,
			@RequestParam(name = "name-S") String name,
			@RequestParam(name = "price-S") String price,
			@RequestParam(name = "note-S") String note,
			@RequestParam(name = "description-S") String description,
			@RequestParam(name = "unit-S") String unit) {
		
		HotelService service = null;
		if(id==0) {
			service = new HotelService(name, price, unit, description, note);
		}else {
			service = new HotelService(id, name, price, unit, description, note);
		}
		hotelServiceService.saveService(service);
		return "redirect:/service";
	}
	
	@PostMapping("/service/delete-service")
	public String deleteService(@RequestParam(name = "idService")Long id) {
		hotelServiceService.deleteService(id);
		return "redirect:/service";
	}
	
}
