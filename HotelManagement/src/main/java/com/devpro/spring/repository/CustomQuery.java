package com.devpro.spring.repository;

public interface CustomQuery {

		String CHAMBER_SEARCH = "select c from Chamber c where c.chamberNumber like :text or c.chamberType like :text";
	
		String GUEST_SEARCH = "select g from Guest g where g.guestName like :text or g.birth like :text or g.idCard like :text or  g.passport like :text or g.address like :text or g.nationality like :text or g.phoneNumber like :text or g.isVip like :text";

		String EMPLOYEE_SEARCH = "select g from Employee g where g.employeeName like :text or g.employeeNumber like :text or g.birth like :text or g.address like :text or g.email like :text or g.phoneNumber like :text or g.salary like :text";

		String EMPLOYEE_UPDATE = "update Employee set employeeNumber=:employeeNumber,employeeName=:employeeName,birth=:birth,gender=:gender,address=:address,email=:email,phoneNumber=:phoneNumber,salary=:salary,managerNumber=:managerNumber where employeeId=:employeeId";
		
		String CHAMBER_SEARCH_PRICE_1 = "select c from Chamber c where cast(c.priceDay as int) < 1000000 and c.chamberType = :chamberType and c.isVip = :isVip and c.isEmpty = 'true'";
		
		String CHAMBER_SEARCH_PRICE_2 = "select c from Chamber c where cast(c.priceDay as int) between 1000000 and 3000000 and c.chamberType = :chamberType and c.isVip = :isVip and c.isEmpty = 'true'";
		
		String CHAMBER_SEARCH_PRICE_3 = "select c from Chamber c where cast(c.priceDay as int) > 3000000 and c.chamberType = :chamberType and c.isVip = :isVip and c.isEmpty = 'true'";
		
		String CHAMBER_UPDATE_IS_EMPTY = "update Chamber set isEmpty=:isEmpty where chamberId=:chamberId";
		
		String CHAMBER_UPDATE_INFO = "update Chamber set chamberNumber=:chamberNumber,chamberType=:chamberType,priceDay=:priceDay,isVip=:isVip,chamberArea=:chamberArea,note=:note where chamberId=:chamberId";
		
		String GUEST_SEARCH_CART = "select g from Guest g where g.idCard = :idCard";
		
		String GUEST_CHECK_EXIST_DATABASE = "select count(g) from Guest g where g.idCard = :idCard";
		
		String GUEST_UPDATE_COMPLETE = "update Guest set passport=:passport,address=:address,phoneNumber=:phoneNumber,email=:email,isFamiliar=:isFamiliar,isVip=:isVip where idCard=:idCard";
		
		String GUEST_UPDATE_NORMAL = "update Guest set guestName=:guestName,birth=:birth,idCard=:idCard,passport=:passport,address=:address,nationality=:nationality,phoneNumber=:phoneNumber,email=:email where guestId=:guestId";
		
		String MULTIPLE_GET_CHAMBERS_ORDER_FOOD = "select c.chamberNumber from Rental r join r.chambers c where r.paid = 'false'";
		
		String MULTIPLE_GET_GUEST_INFO = "select g from Rental r join r.chambers c join r.guest g where r.paid = 'false' and c.chamberNumber = :chamberNumber";
		
		String MULTIPLE_GET_RENTAL_ID = "select r.rentalId from Rental r join r.chambers c join r.guest g where r.paid = 'false' and c.chamberNumber = :chamberNumber";

		String MULTIPLE_GET_FOOD_ITEM_INFO = "select f.id,f.name,f.description,f.price,f.image,c.categoryName from FoodItem f join f.category c where f.name like :text or c.categoryName like :text";
		
		String MULTIPLE_GET_ONE_FOOD_ITEM_INFO = "select f.id,f.name,f.description,f.price,f.image,c.categoryName from FoodItem f join f.category c where f.id = :id";
		
		String HOTEL_SERVICE_SEARCH = "select h from HotelService h where h.name =: name";
		
		String MULTIPLE_GET_CHECK_OUT_INFO1 = "select r from Rental r join r.chambers c join r.guest g where c.chamberNumber = :chamberNumber and r.paid = 'false'";
		
		String MULTIPLE_GET_CHECK_OUT_INFO2 = "select g from Rental r join r.chambers c join r.guest g where c.chamberNumber = :chamberNumber and r.paid = 'false'";
		
		String MULTIPLE_GET_CHECK_OUT_INFO3 = "select c from Rental r join r.chambers c join r.guest g where c.chamberNumber = :chamberNumber and r.paid = 'false'";
		
		String MULTIPLE_GET_TOTAL_FOOD = 
		 "select sum(o.total_price) as total from rental r join order_food o on o.rental_id = r.rental_id " +
		"join rental_chamber rc on rc.rental_id =  r.rental_id join chamber c " +
		 "on c.chamber_id = rc.chamber_id where c.chamber_number = :chamberNumber and r.paid = 'false'";
		 
		 String MULTIPLE_GET_TOTAL_SERVICE = 
		"select sum(s.total_price) as total from rental r join service_bill s on r.rental_id = s.rental_id " +
		"join rental_chamber rc on rc.rental_id =  r.rental_id join chamber c "  +
		 "on c.chamber_id = rc.chamber_id where c.chamber_number = :chamberNumber  and r.paid = 'false'";
		 
		 String MULTIPLE_GET_NUMBER_STAY = 
		"select datediff(now(),check_in_date) from rental r join rental_chamber rc on rc.rental_id =  r.rental_id join chamber c " +
		"on c.chamber_id = rc.chamber_id where c.chamber_number = :chamberNumber  and r.paid = 'false'";
}
