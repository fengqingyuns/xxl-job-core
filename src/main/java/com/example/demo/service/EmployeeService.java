package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Employee;
@Service
public interface EmployeeService {

	Employee queryEmployeeById(String id);
	
	void deleteEmployeeById(String id);
	
	void addEmployee(Employee em);
}
