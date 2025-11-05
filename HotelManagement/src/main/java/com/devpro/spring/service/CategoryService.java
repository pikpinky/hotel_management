package com.devpro.spring.service;

import java.util.List;

import com.devpro.spring.model.Category;

public interface CategoryService {

	List<Category> loadListCategories();
	
	Category getOne(Long id);
	
}
