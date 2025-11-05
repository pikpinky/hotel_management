package com.devpro.spring.dto;

import com.devpro.spring.model.HotelService;

public class MenuOrderServiceDto {

	private Long id;
	private String name;
	private String price;
	private String time;
	public MenuOrderServiceDto(HotelService service,String quantity) {
		super();
		this.id = service.getId();
		this.name = service.getName();
		if(service.getPrice().equalsIgnoreCase("0")) {
			this.price = "0";
		}else {
			this.price = service.getPrice()+"/"+service.getUnit();
		}
		this.time = quantity;
		this.total = String.valueOf((Integer.parseInt(service.getPrice())*Integer.parseInt(quantity)));
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	private String total;
	
}
