package com.eikona.tech.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.tech.entity.Employee;
import com.eikona.tech.repository.EmployeeRepository;

@Component
public class EmployeeObjectMap {
	
	
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	
	
	public Map<String, Employee> getEmployeeByEmpId(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalse();
		Map<String, Employee> employeeMap = new HashMap<String, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getEmpId(), employee);
		}
		return employeeMap;
	}
	
	
	
}
