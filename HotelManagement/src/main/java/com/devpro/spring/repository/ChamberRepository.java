package com.devpro.spring.repository;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devpro.spring.model.Chamber;

public interface ChamberRepository extends JpaRepository<Chamber, Long>{
	
	@Query(CustomQuery.CHAMBER_SEARCH_PRICE_1)
	Page<Chamber> searchChamberWithPrice1(Pageable pageable,
			@Param("chamberType") String type,
			@Param("isVip") String vip);
	
	@Query(CustomQuery.CHAMBER_SEARCH_PRICE_2)
	Page<Chamber> searchChamberWithPrice2(Pageable pageable,
			@Param("chamberType") String type,
			@Param("isVip") String vip);
	
	@Query(CustomQuery.CHAMBER_SEARCH_PRICE_3)
	Page<Chamber> searchChamberWithPrice3(Pageable pageable,
			@Param("chamberType") String type,
			@Param("isVip") String vip);
	
	@Transactional
	@Modifying
	@Query(CustomQuery.CHAMBER_UPDATE_IS_EMPTY)
	void updateChamberIsEmpty(
			@Param("isEmpty") String isEmpty,
			@Param("chamberId") Long chamberId);
	
	@Query(CustomQuery.CHAMBER_SEARCH)
	Page<Chamber> searchChamber(Pageable pageable,@Param("text") String text);
	
	@Transactional
	@Modifying
	@Query(CustomQuery.CHAMBER_UPDATE_INFO)
	void updateChamberInfo(
			@Param("chamberNumber") String chamberNumber,
			@Param("chamberType") String chamberType,
			@Param("priceDay") String priceDay,
			@Param("chamberArea") String chamberArea,
			@Param("note") String note,
			@Param("isVip") String isVip,
			@Param("chamberId") Long chamberId
			);
}
