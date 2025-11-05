package com.devpro.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devpro.spring.model.Section;
import com.devpro.spring.repository.SectionRepository;

@Service
public class SectionServiceImpl implements SectionService{

	@Autowired
	private SectionRepository sectionRepository;
	
	@Override
	public List<Section> getSectionOption() {
		// TODO Auto-generated method stub
		return sectionRepository.findAll();
	}

}
