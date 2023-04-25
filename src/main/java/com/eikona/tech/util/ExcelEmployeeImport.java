package com.eikona.tech.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.MessageConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Organization;
import com.eikona.tech.repository.EmployeeRepository;

@Component
public class ExcelEmployeeImport {


	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeObjectMap entityObjectMap;

	public Employee excelRowToEmployee(Row currentRow) throws ParseException {

		Employee employeeObj = null;

		Iterator<Cell> cellsInRow = currentRow.iterator();
		int cellIndex = NumberConstants.ZERO;
		employeeObj = new Employee();

		while (cellsInRow.hasNext()) {
			Cell currentCell = cellsInRow.next();
			cellIndex = currentCell.getColumnIndex();
			if (null == employeeObj) {
				break;
			}

			else if (cellIndex == NumberConstants.ZERO) {
				String value = getStringValue(currentCell);
				employeeObj.setEmpId(value);
			} else if (cellIndex == NumberConstants.ONE) {
				String value = getStringValue(currentCell);
				employeeObj.setName(value);
			} 

		}
		return employeeObj;

	}

	

	@SuppressWarnings(ApplicationConstants.DEPRECATION)
	private String getStringValue(Cell currentCell) {
		currentCell.setCellType(CellType.STRING);
		String value = "";
		if (currentCell.getCellType() == CellType.NUMERIC) {
			value = String.valueOf(currentCell.getNumericCellValue());
		} else if (currentCell.getCellType() == CellType.STRING) {
			value = currentCell.getStringCellValue();
		}
		return value;
	}
	
	

	public List<Employee> parseExcelFileEmployeeList(InputStream inputStream, Organization org) throws ParseException {
		List<Employee> employeeList = new ArrayList<Employee>();
		try {

			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(NumberConstants.ZERO);

			Iterator<Row> rows = sheet.iterator();

			int rowNumber = NumberConstants.ZERO;
			Map<String, Employee> employeeMap = entityObjectMap.getEmployeeByOrganization(org);
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber == NumberConstants.ZERO) {
					rowNumber++;
					continue;
				}

				rowNumber++;

				Employee employee = excelRowToEmployee(currentRow);

				Employee emp=employeeMap.get(employee.getEmpId());

				if (null==emp && null != employee.getName() && !employee.getName().isEmpty()
						&& null != employee.getEmpId() && !employee.getEmpId().isEmpty()) {
					employee.setOrganization(org);
					employeeList.add(employee);
					
				}
				
				else if(null!=emp) {
					emp.setName(employee.getName());
					emp.setOrganization(org);
					employeeList.add(emp);
				}
					

				if (rowNumber % NumberConstants.HUNDRED == NumberConstants.ZERO) {
					employeeRepository.saveAll(employeeList);
					employeeList.clear();
				}

			}

			if (!employeeList.isEmpty()) {
				employeeRepository.saveAll(employeeList);
				employeeList.clear();
			}

			workbook.close();

			return employeeList;
		} catch (IOException e) {
			throw new RuntimeException(MessageConstants.FAILED_MESSAGE + e.getMessage());
		}
	}
}
