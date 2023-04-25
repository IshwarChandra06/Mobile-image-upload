package com.eikona.tech.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.DailyAttendanceConstants;
import com.eikona.tech.constants.EmployeeConstants;
import com.eikona.tech.constants.HeaderConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.repository.EmployeeRepository;
@Component
public class ExportEmployee {
	
	@Autowired
	private GeneralSpecificationUtil<Employee> generalSpecification;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;

	public void fileExportBySearchValue(HttpServletResponse response, String name, String empId,
			 String flag, String orgName) throws ParseException, IOException {
		
		List<Employee> employeeList = getListOfEmployee( name, empId, orgName);
		
		excelGenerator(response, employeeList);
		
	}

	private List<Employee> getListOfEmployee(String name, String empId, String orgName) {
		
		Specification<Employee> employeeNameSpec = generalSpecification.stringSpecification(name,EmployeeConstants.NAME);
		Specification<Employee> employeeIdSpec = generalSpecification.stringSpecification(empId,EmployeeConstants.EMPID);
		Specification<Employee> organizationSpec = generalSpecification.foreignKeyStringSpecification(orgName, "organization", EmployeeConstants.NAME);
		
    	List<Employee> employeeList =employeeRepository.findAll((employeeNameSpec).and(employeeIdSpec).and(organizationSpec));
		return employeeList;
	}
	
	private void excelGenerator(HttpServletResponse response, List<Employee> employeeList) throws ParseException, IOException {

		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_SPACE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = DailyAttendanceConstants.DAILY_ATTENDANCE_REPORT + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		int rowCount = NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);

		Font font = workBook.createFont();
		font.setBold(true);

		CellStyle cellStyle = setBorderStyle(workBook, BorderStyle.THICK, font);

		setHeaderForExcel(row, cellStyle);

		font = workBook.createFont();
		font.setBold(false);
		cellStyle = setBorderStyle(workBook, BorderStyle.THIN, font);
		
		//set data for excel
		setExcelDataCellWise(employeeList, sheet, rowCount, cellStyle);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();

	}
	
	private CellStyle setBorderStyle(Workbook workBook, BorderStyle borderStyle, Font font) {
		CellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setFont(font);
		return cellStyle;
	}
	
	private void setExcelDataCellWise(List<Employee> employeeList, Sheet sheet, int rowCount,
			CellStyle cellStyle) {
		for (Employee employee : employeeList) {
			byte[] image = imageProcessingUtil.searchEmployeeImage(employee.getId());
			String date=imageProcessingUtil.getImageDate(employee.getId());
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;


			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getName());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getEmpId());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			if(null!=date)
			 cell.setCellValue(date);
			else
			 cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			if(null!=image)
			 cell.setCellValue("Yes");
			else
			 cell.setCellValue("No");
			cell.setCellStyle(cellStyle);

		}
	}
	
	private void setHeaderForExcel(Row row, CellStyle cellStyle) {
		int columnCount = NumberConstants.ZERO;

		Cell cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.NAME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMPLOYEE_ID);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.UPLOAD_DATE);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.IMAGE_PRESENT);
		cell.setCellStyle(cellStyle);

		

	}

}
