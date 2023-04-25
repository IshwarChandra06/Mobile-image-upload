package com.eikona.tech.service;


import java.security.Principal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Organization;


public interface EmployeeService {
	/**
	 * Returns all employee List, which are isDeleted false.
	 * @param
	 */
	List<Employee> getAll();
	/**
	 * This function saves the employee in database according to the respective object.  
	 * @param 
	 */
    Employee save(Employee employee);
    /**
	 * This function retrieves the employee from database according to the respective id.  
	 * @param
	 */
    Employee getById(long id);
    
	/**
	 * This function retrieves the employee data from the excel file and set into database.
	 * @param file -MultipartFile
	 * @param org 
	 * @param principal 
	 */
	String storeEmployeeList(MultipartFile file, Organization org);
	/**
	 * This function deletes the employee from database according to the respective id.  
	 * @param
	 */
	void deleteById(long id, Principal principal);
	
	PaginationDto<Employee> searchByField( String name, String empId ,int pageno, String sortField, String sortDir, String orgname);

}
