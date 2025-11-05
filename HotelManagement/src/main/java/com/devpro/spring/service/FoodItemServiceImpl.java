package com.devpro.spring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.devpro.spring.dto.FoodItemDto;
import com.devpro.spring.model.FoodItem;
import com.devpro.spring.repository.FoodItemRepository;

@Service
public class FoodItemServiceImpl implements FoodItemService{

	@Autowired
	public FoodItemRepository foodItemRepository;
	
	@Override
	public List<FoodItem> loadToSelectOption() {
		// TODO Auto-generated method stub
		return foodItemRepository.findAll();
	}

	@Override
	public FoodItem getItem(Long id) {
		// TODO Auto-generated method stub
		return foodItemRepository.getOne(id);
	}

	@Override
	public Page<FoodItemDto> getListFoodItem(org.springframework.data.domain.Pageable pageable,String text) {
		// TODO Auto-generated method stub
		List<Object[]> foodItem = foodItemRepository.getListFoodItem("%"+text+"%");
		List<FoodItemDto> result = new ArrayList<FoodItemDto>();
		for(Object[] item:foodItem) {
			result.add(new FoodItemDto(item));
		}
		int start = (int)pageable.getOffset();
		int end = (int) ((pageable.getOffset() + pageable.getPageSize()) > result.size()?
				result.size():pageable.getOffset() + pageable.getPageSize());
		List<FoodItemDto> subList = result.subList(start, end);
		return new PageImpl<FoodItemDto>(subList, pageable, result.size());
		
	}

	@Override
	public FoodItemDto getFoodItem(Long id) {
		// TODO Auto-generated method stub
		Object[] item = foodItemRepository.getOneFoodItem(id);
		FoodItemDto result = new FoodItemDto(item);
		return result;
	}

	@Override
	public void saveFoodItem(FoodItem item) {
		// TODO Auto-generated method stub
		foodItemRepository.save(item);
	}

	@Override
	public void deleteFoodItem(Long id) {
		// TODO Auto-generated method stub
		foodItemRepository.deleteById(id);
	}
}
