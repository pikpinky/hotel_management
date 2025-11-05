package com.devpro.spring.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "section")
public class Section implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	@Column(name = "section_id")
	private Long sectionId;
	
	@Column(name = "section_name")
	private String sectionName;
	
	@Column(name = "section_manager_id")
	private String sectionManagerId;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "section")
	private Set<Employee> listEmployee = new HashSet<>();
	
	public Section() {
		super();
	}

	public Section(Long sectionId, String sectionName, String sectionManagerId, Set<Employee> listEmployee) {
		super();
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		this.sectionManagerId = sectionManagerId;
		this.listEmployee = listEmployee;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSectionManagerId() {
		return sectionManagerId;
	}

	public void setSectionManagerId(String sectionManagerId) {
		this.sectionManagerId = sectionManagerId;
	}

	public Set<Employee> getListEmployee() {
		return listEmployee;
	}

	public void setListEmployee(Set<Employee> listEmployee) {
		this.listEmployee = listEmployee;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Section [sectionId=");
		builder.append(sectionId);
		builder.append(", sectionName=");
		builder.append(sectionName);
		builder.append(", sectionManagerId=");
		builder.append(sectionManagerId);
		builder.append(", listEmployee=");
		builder.append(listEmployee);
		builder.append("]");
		return builder.toString();
	}
	
}
