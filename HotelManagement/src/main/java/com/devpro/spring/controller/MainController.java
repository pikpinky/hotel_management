package com.devpro.spring.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.devpro.spring.utils.WebUtils;

@Controller
public class MainController {
	
	@RequestMapping(value = {"/", "home"}, method = RequestMethod.GET)
	public String homePage(Model model) {
		return "home";
	}
	
	@GetMapping("/login")
	public String login(Model model) {
		return "login";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String accessDenied(Model model, Principal principal) {
		if(principal != null) {
			User loginUser = (User) ((Authentication)principal).getPrincipal();
			String userInfor = WebUtils.toString(loginUser);
			model.addAttribute("userInfor", userInfor);
			String message = "Xin chào " + principal.getName() + ". Bạn không có quyền truy cập vào trang web này";
			model.addAttribute("message", message);
		}
		return "403Page";
	}
}
