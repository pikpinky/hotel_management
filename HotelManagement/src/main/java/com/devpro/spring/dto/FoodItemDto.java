package com.devpro.spring.dto;

public class FoodItemDto {
	public FoodItemDto(long id, String name, String description, String price, String image, String category) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.image = image;
		this.category = category;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public FoodItemDto(Object[] item) {
		this.id = (long)item[0];
		this.name = String.valueOf(item[1]);
		this.description = String.valueOf(item[2]);
		this.price = String.valueOf(item[3]);
		this.image = String.valueOf(item[4]);
		this.category = String.valueOf(item[5]);
	}
	@Override
	public String toString() {
		return "FoodItemDto [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price
				+ ", image=" + image + ", category=" + category + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	private Long id;
	private String name;
	private String description;
	private String price;
	private String image;
	private String category;
}
