package com.eikona.tech.service.impl;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.EmployeeConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Organization;
import com.eikona.tech.repository.EmployeeRepository;
import com.eikona.tech.service.EmployeeService;
import com.eikona.tech.util.ExcelEmployeeImport;
import com.eikona.tech.util.GeneralSpecificationUtil;
import com.eikona.tech.util.ImageProcessingUtil;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ExcelEmployeeImport excelEmployeeImport;
	
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	
	@Autowired
	private GeneralSpecificationUtil<Employee> generalSpecificationEmployee;
	
	@Override
	public List<Employee> getAll() {
		return employeeRepository.findAllByIsDeletedFalse();
	}

	@Override
	public Employee save(Employee employee) {
		employee.setDeleted(false);
		return this.employeeRepository.save(employee);
	}

	@Override
	public Employee getById(long id) {
		Optional<Employee> optional = employeeRepository.findById(id);
		Employee employee = null;
		if (optional.isPresent()) {
			employee = optional.get();
		} else {
			throw new RuntimeException(EmployeeConstants.EMPLOYEE_NOT_FOUND+ id);
		}
		return employee;
	}

	@Override
	public void deleteById(long id,Principal principal) {
		Optional<Employee> optional = employeeRepository.findById(id);
		Employee employee = null;
		if (optional.isPresent()) {
			employee = optional.get();
			employee.setDeleted(true);
		} else {
			throw new RuntimeException(EmployeeConstants.EMPLOYEE_NOT_FOUND + id);
		}
		this.employeeRepository.save(employee);

	}

	@Override
	public String storeEmployeeList(MultipartFile file, Organization org) {
		try {
			List<Employee> employeeList = excelEmployeeImport.parseExcelFileEmployeeList(file.getInputStream(), org);
			employeeRepository.saveAll(employeeList);
			return "File uploaded successfully!";
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return "Fail! -> uploaded filename: " + file.getOriginalFilename();
		}
	}
	
	@Override
	public PaginationDto<Employee> searchByField(String name, String empId, int pageno, String sortField, String sortDir, String orgname) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Employee> page = getEmployeePage(name, empId, pageno, sortField,
				sortDir, orgname);
        List<Employee> employeeList =  page.getContent();
        List<Employee> employeeWithImgList = new ArrayList<Employee>();
        for (Employee employee : employeeList) {
			byte[] image = imageProcessingUtil.searchEmployeeImage(employee.getId());
			employee.setCropImage(image);
			employeeWithImgList.add(employee);
		}
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Employee> dtoList = new PaginationDto<Employee>(employeeWithImgList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Employee> getEmployeePage(String name, String empId,int pageno, String sortField, String sortDir, String orgname) {
		
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		Specification<Employee> isDeletedFalse = generalSpecificationEmployee.isDeletedSpecification();
		Specification<Employee> nameSpc = generalSpecificationEmployee.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Employee> empIdSpc = generalSpecificationEmployee.stringSpecification(empId, EmployeeConstants.EMPID);
		Specification<Employee> orgSpc = generalSpecificationEmployee.foreignKeyStringSpecification(orgname, "organization", EmployeeConstants.NAME);
		
    	Page<Employee> page = employeeRepository.findAll(nameSpc.and(empIdSpc).and(orgSpc).and(isDeletedFalse), pageable);
		return page;
	}

}