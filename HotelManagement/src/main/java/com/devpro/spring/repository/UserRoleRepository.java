package com.devpro.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devpro.spring.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long>{

}
