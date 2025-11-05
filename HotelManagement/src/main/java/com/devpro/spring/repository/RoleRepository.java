package com.devpro.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devpro.spring.model.AppRole;

public interface RoleRepository extends JpaRepository<AppRole, Long> {
	
	@Query("select ur.appRole.roleName from UserRole ur where ur.appUser.userId = :userId")
	public List<String> getRoleNames(@Param("userId") Long userId);
}
