package com.devpro.spring.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devpro.spring.model.Guest;

public interface GuestRepository extends JpaRepository<Guest, Long>{
	
	@Query(CustomQuery.GUEST_SEARCH)
	Page<Guest> searchGuests(Pageable pageable,@Param("text") String text);
	
	@Query(CustomQuery.GUEST_SEARCH)
	List<Guest> searchGuests(@Param("text") String text);
	
	@Query(CustomQuery.GUEST_SEARCH_CART)
	Guest searchGuestWithCart(
			@Param("idCard") String idCard);
	
	@Query(CustomQuery.GUEST_CHECK_EXIST_DATABASE)
	Integer checkExistGuest(
			@Param("idCard") String idCard
			);
	
	@Transactional
	@Modifying
	@Query(CustomQuery.GUEST_UPDATE_NORMAL)
	void updateNomal(
			@Param("guestName") String guestName,
			@Param("birth") String birth,
			@Param("idCard") String idCard,
			@Param("passport") String passport,
			@Param("address") String address,
			@Param("nationality") String nationality,
			@Param("phoneNumber") String phoneNumber,
			@Param("email") String email,
			@Param("guestId") Long guestId
	);
	
	@Transactional
	@Modifying
	@Query(CustomQuery.GUEST_UPDATE_COMPLETE)
	void updateComplete(
			@Param("passport") String passport,
			@Param("address") String address,
			@Param("phoneNumber") String phoneNumber,
			@Param("email") String email,
			@Param("isFamiliar") String isFamiliar,
			@Param("isVip") String isVip,
			@Param("idCard") String idCard
	);
	
	@Query(CustomQuery.MULTIPLE_GET_GUEST_INFO)
	Guest getGuestInfoByChamberNumber(@Param("chamberNumber") String chamberNumber);
}
