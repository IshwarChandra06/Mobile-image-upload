package com.eikona.tech.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Organization;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.EmployeeRepository;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.EmployeeService;
import com.eikona.tech.service.OrganizationService;
import com.eikona.tech.util.ExportEmployee;
import com.eikona.tech.util.ImageProcessingUtil;

@Controller
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	
	@Autowired
	private ExportEmployee exportEmployee;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganizationService organizationService;
	
	
	@GetMapping({"/employee-list"})
	public String employeeList(Model model) {
		
		return "employee/employee_list";
	}
	

	// Import Employee
	@GetMapping("/import/employee-list")
	public String importEmployeeList() {
		return "multipartfile/uploadEmployeeList";
	}

	@PostMapping("/upload/employee-list/excel")
	public String uploadEmployeeList(@RequestParam("uploadfile") MultipartFile file, Model model, Principal principal) {

		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		Organization org =  organizationService.getById(user.getOrganization().getId());
		String message = employeeService.storeEmployeeList(file, org);
		model.addAttribute("message", message);
		return "multipartfile/uploadEmployeeList";
	}

	// Import Image
	@GetMapping("/employee/new")
	public String newEmployee(Model model, Principal principal) {

		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		model.addAttribute("listOrganization", organizationService.getById(user.getOrganization().getId()));
		Employee employee = new Employee();
		model.addAttribute("employee", employee);
		model.addAttribute("title", "New Employee");
		return "employee/employee_new";
	}
	
	@PostMapping("/employee/add")
	@PreAuthorize("hasAnyAuthority('employee_create','employee_update')")
	public String saveEmployee(@RequestParam("files") MultipartFile file, @ModelAttribute("employee") Employee employee,
			Model model, @Valid Employee emp, Errors errors, String title,BindingResult bindingResult, Principal principal) {
		if (errors.hasErrors()) {
			User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
			model.addAttribute("listOrganization", organizationService.getById(user.getOrganization().getId()));
			model.addAttribute("title", title);
			return "employee/employee_new";
		} else {
			
			if (null == employee.getId()) {
				Employee findEmployee= employeeRepository.findByEmpIdAndIsDeletedFalse(employee.getEmpId());
				if(null!=findEmployee){
					model.addAttribute("title", title);
				    bindingResult.rejectValue("empId", "error.empId", "Employee id is already exist!");
				    return "employee/employee_new";
				}
				employee=employeeService.save(employee);
				if (null != file && !file.getOriginalFilename().isEmpty()) {
					imageProcessingUtil.saveEmployeeImageWhileEnrolling(file,employee);
				}

			} else {
				Employee employeeObj = employeeService.getById(employee.getId());
				employee.setCreatedBy(employeeObj.getCreatedBy());
				employee.setCreatedDate(employeeObj.getCreatedDate());
				employee=employeeService.save(employee);
				if (null != file && !file.getOriginalFilename().isEmpty()) {
					imageProcessingUtil.saveEmployeeImageWhileEnrolling(file,employee);
				}
				
			}

			return "redirect:/employee";
		}
	}

	@GetMapping("/employee/edit/{id}")
	public String editEmployee(@PathVariable(value = "id") long id, Model model, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		model.addAttribute("listOrganization", organizationService.getById(user.getOrganization().getId()));

		Employee employee = employeeService.getById(id);
		model.addAttribute("employee", employee);
		model.addAttribute("title", "Update Employee");
		return "employee/employee_new";
	}

	@GetMapping("/employee/delete/{id}")
	public String deleteEmployee(@PathVariable(value = "id") long id, Principal principal) {
		this.employeeService.deleteById(id, principal);
		return "redirect:/employee";
	}
	
	@RequestMapping(value = "/api/search/employee", method = RequestMethod.GET)
	public @ResponseBody PaginationDto<Employee> searchEmployee(Long id, String name,String empId,String company,String department,String designation, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgname = (null == user.getOrganization()?null:user.getOrganization().getName());
		
		PaginationDto<Employee> dtoList = employeeService.searchByField(name, empId , pageno, sortField, sortDir, orgname);
		return dtoList;
	
	}
	
	
	@RequestMapping(value="/api/employee/export-to-excel",method = RequestMethod.GET)
	public void exportToFile(HttpServletResponse response,String name,String empId, String flag, Principal principal) {
		
		User userObj = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == userObj.getOrganization()? null : userObj.getOrganization().getName());
		
		 response.setContentType("application/octet-stream");
			DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String currentDateTime = dateFormat.format(new Date());
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Employee_master_data" + currentDateTime + "."+flag;
			response.setHeader(headerKey, headerValue);
		try {
			exportEmployee.fileExportBySearchValue(response, name, empId, flag, orgName);
		} catch (Exception  e) {
			e.printStackTrace();
		}
	}
	@GetMapping("/excel-template-download")
	public void downloadEmployeeListExcelTemplate(HttpServletResponse response) throws IOException {
		
//		String filename = "src/main/resources/static/excel/Employee_import_template.xlsx";
        String filename = "excel/Employee_import_template.xlsx";
        try {
        	
        	String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Employee_import_template.xlsx";
			response.setHeader(headerKey, headerValue);
			FileInputStream inputStream = new FileInputStream(new File(filename));
			Workbook workBook = new XSSFWorkbook(inputStream);
			FileOutputStream fileOut = new FileOutputStream(filename);
			workBook.write(fileOut);
			ServletOutputStream outputStream = response.getOutputStream();
			workBook.write(outputStream);
			workBook.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
