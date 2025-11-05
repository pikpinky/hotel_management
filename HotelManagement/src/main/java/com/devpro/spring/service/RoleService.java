package com.devpro.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devpro.spring.model.AppRole;
import com.devpro.spring.repository.RoleRepository;


@Service
@Transactional
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	public AppRole getRole(Long id) {
		return roleRepository.findById(id).get();
	}
}
