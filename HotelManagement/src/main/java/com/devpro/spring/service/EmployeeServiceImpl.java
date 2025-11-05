package com.devpro.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devpro.spring.model.Employee;
import com.devpro.spring.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService{

	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public Employee findEmployee(Long id) {
		// TODO Auto-generated method stub
		return employeeRepository.getOne(id);
	}

	@Override
	public void editEmployeeInfo(String employeeNumber, String employeeName, String birth, String gender, String address, String email, String phoneNumber, String salary, String managerNumber, Long employeeId) {
		// TODO Auto-generated method stub
		employeeRepository.updateEmployee(employeeNumber, employeeName, birth, gender, address, email, phoneNumber, salary, managerNumber, employeeId);
	}

	@Override
	public void addEmployee(String employeeNumber, String employeeName, String birth, String gender, String address, String email, String phoneNumber, String salary, String managerNumber) {
		Employee employee = new Employee(employeeNumber, employeeName, birth, gender, address, email, phoneNumber, salary, managerNumber);
		employeeRepository.save(employee);
	}


	@Override
	public Page<Employee> searchEmployees(Pageable pageable, String text) {
		// TODO Auto-generated method stub
		return employeeRepository.searchEmployees(pageable,"%"+text.trim()+"%");
	}


}
