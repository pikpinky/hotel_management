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

import com.devpro.spring.model.Employee;
import com.devpro.spring.service.EmployeeService;
import com.devpro.spring.service.SectionService;


@Controller
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private SectionService sectionService;
	
	@GetMapping("/employee")
	public String loadListEmployees(Model model,
			@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "search-text", defaultValue = "") String text){
		Pageable pageable = PageRequest.of(page, 10);
		Page<Employee> pages = employeeService.searchEmployees(pageable, text);

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
		String baseUrl = "/employee?page=";
		String searchUrl = "&search-text="+text;
		
		model.addAttribute("listSection",sectionService.getSectionOption());
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", total);
		model.addAttribute("totalElement", totalElement);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("employees", pages);
		model.addAttribute("extra", extra);
		model.addAttribute("checkLast", checkLast);
		model.addAttribute("searchUrl", searchUrl);
		model.addAttribute("searchText", text);
		return "employee";
	}
	
	@PostMapping("/update-employee-info")
	public String updateEmployeeInfo(
			@RequestParam(name = "id") Long employeeId,
			@RequestParam(name = "number") String employeeNumber,
			@RequestParam(name = "name") String employeeName,
			@RequestParam(name = "birth") String birth,
			@RequestParam(name = "gender") String gender,
			@RequestParam(name = "address") String address,
			@RequestParam(name = "email") String email,
			@RequestParam(name = "phone") String phoneNumber,
			@RequestParam(name = "salary") String salary,
			@RequestParam(name = "manager") String managerNumber,
			@RequestParam(name = "page") int page,
			@RequestParam(name = "text") String text) {
			if(employeeId == -1) {
				employeeService.addEmployee(employeeNumber, employeeName, birth, gender, address, email, phoneNumber, salary, managerNumber);
			} else {
				employeeService.editEmployeeInfo(employeeNumber, employeeName, birth, gender, address, email, phoneNumber, salary, managerNumber, employeeId);
			}
		return "redirect:/employee?page="+page+"&search-text="+text;
	}
	
	@GetMapping("/find-employee")
	@ResponseBody
	public Employee findOneEmployee(Long id) {
		return employeeService.findEmployee(id);
		
	}
}
