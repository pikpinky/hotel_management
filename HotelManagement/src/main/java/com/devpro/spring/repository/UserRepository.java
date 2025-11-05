package com.devpro.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devpro.spring.model.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {
	
	@Query("select usr from AppUser usr where usr.userName = :userName")
	public AppUser findUserName(@Param("userName") String userName);
}
