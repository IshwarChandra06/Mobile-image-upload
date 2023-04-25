package com.eikona.tech.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Organization;


@Repository
public interface EmployeeRepository extends DataTablesRepository<Employee, Long> {

	List<Employee> findAllByIsDeletedFalse();
	
	@Query("select emp.empId from com.eikona.tech.entity.Employee as emp where emp.isDeleted=false")
	List<String> getEmpIdAndIsDeletedFalseCustom();

	Employee findByEmpIdAndIsDeletedFalse(String empId);

	List<Employee> findAllByIsDeletedFalseAndOrganization(Organization organization);

}