package com.verinon.hrms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.verinon.hrms.bean.PaySlipsDetailsBean;
import com.verinon.hrms.dao.AttendanceDaoImpl;
import com.verinon.hrms.model.Employee;
import com.verinon.hrms.model.EmployeeAttendanceDetails;
import com.verinon.hrms.model.EmployeePaySlipDetails;
import com.verinon.hrms.model.PancardDetails;
import com.verinon.hrms.service.AttendanceService;
import com.verinon.hrms.service.EmployeeService;

public class ProcessAttendanceExcelFile {
	static final Logger log = Logger.getLogger(ProcessAttendanceExcelFile.class);
	@Autowired
	public EmployeeService employeeService;
	
	public	List<EmployeeAttendanceDetails>  readAttendanceDetailsFile(CommonsMultipartFile attendanceDetailsFile,Date attendanceDate) {
		log.info("Start readAttendanceDetailsFile.");	
		List<EmployeeAttendanceDetails>  attendanceDetailsBeanList = new ArrayList<EmployeeAttendanceDetails>();
		String extension = "";
		try {
			File attendanceFile = convertMultiPartFileToFile(attendanceDetailsFile);
			FileInputStream fis = new FileInputStream(attendanceFile);
			// Finds the workbook instance for XLS file
			XSSFWorkbook attendanceDetailsWorkBook = new XSSFWorkbook(fis);
			// Return first sheet from the XLS workbook
			XSSFSheet   attendanceDetailsSheet = attendanceDetailsWorkBook
					.getSheetAt(0);
			System.out.println("attendanceDetailsFile sheet name-"
					+ attendanceDetailsSheet.getSheetName());
			log.info("attendanceDetailsFile sheet name-"
					+ attendanceDetailsSheet.getSheetName());
			// Get iterator to all the rows in current sheet 
			Iterator<Row> rowIterator = attendanceDetailsSheet.iterator();
			
			// Traversing over each row of XLSX file
			Map<String, String> dataHeaderMap = new HashMap<String, String>();
			while (rowIterator.hasNext()) { 
				Row row = rowIterator.next(); 
				// For each row, iterate through each columns
				EmployeeAttendanceDetails employeeAttendanceDetails = new EmployeeAttendanceDetails();
			     Iterator<Cell> cellIterator = row.cellIterator();
			     while (cellIterator.hasNext()) { 
						Cell cell = cellIterator.next(); 
						if (cell.getRowIndex() < 1 ) {
						//	if (cell.getRowIndex() == 5 ) {
							dataHeaderMap.put("" + cell.getRowIndex() + ""
									+ cell.getColumnIndex(),
									cell.getStringCellValue());

						}//end if.
                    //if (cell.getRowIndex() > 5 ) {
						else{
						switch (cell.getCellType()) { 
						case Cell.CELL_TYPE_STRING:
							setAttendanceDetailsBeanData(
									employeeAttendanceDetails,
									dataHeaderMap.get(0 + ""
											+ cell.getColumnIndex()),
									cell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_BOOLEAN:

							setAttendanceDetailsBeanData(
									employeeAttendanceDetails,
									dataHeaderMap.get(0 + ""
											+ cell.getColumnIndex()),
									cell.getBooleanCellValue() + "");
							break;
						case Cell.CELL_TYPE_NUMERIC:
							setAttendanceDetailsBeanData(
									employeeAttendanceDetails,
										dataHeaderMap.get(0 + ""
												+ cell.getColumnIndex()),
												cell.getNumericCellValue() + "");//
							break;
						case Cell.CELL_TYPE_BLANK:
							setAttendanceDetailsBeanData(
									employeeAttendanceDetails,
									dataHeaderMap.get(0 + ""
											+ cell.getColumnIndex()), "");
							break;
							
						   }//switch close.
			             }//else close.
					}// cell iterator close
				
				if (employeeAttendanceDetails.getEmployeeOriginalId() != null) {
					employeeAttendanceDetails.setAttendanceDate(attendanceDate);
					attendanceDetailsBeanList.add(employeeAttendanceDetails);
				log.info(employeeAttendanceDetails.toString());
				}
			} //row iterator close.
			
			attendanceDetailsWorkBook.close();	
		} catch (FileNotFoundException fileException) {
			log.debug("Exception occured."+fileException.getMessage());
		} catch (IOException ioException) {
			log.debug("Exception occured."+ioException.getMessage());
		}
		catch (Exception exception) {
			log.debug("Exception occured."+exception.getMessage());
		}
		
		log.info("End readAttendanceDetailsFile.");
		return attendanceDetailsBeanList;
	}
	
	public EmployeeAttendanceDetails setAttendanceDetailsBeanData(EmployeeAttendanceDetails attendanceDetails, String columnName,
			String dataStr) {
		log.info("Start setAttendanceDetailsBeanData.");
		try{
		if (AttendanceDataConstants.EMPLOYEE_ORIGINAL_ID.equalsIgnoreCase(columnName)) {
			attendanceDetails.setEmployeeOriginalId(dataStr);
		}  else if (AttendanceDataConstants.IN1.equalsIgnoreCase(columnName)) {
			attendanceDetails.setIn1(dataStr);
		}  else if (AttendanceDataConstants.OUT1.equalsIgnoreCase(columnName)) {
			attendanceDetails.setOut1(dataStr);
		} else if (AttendanceDataConstants.IN2.equalsIgnoreCase(columnName)) {
			attendanceDetails.setIn2(dataStr);
		} else if (AttendanceDataConstants.OUT2.equalsIgnoreCase(columnName)) {
			attendanceDetails.setOut2(dataStr);
		} else if (AttendanceDataConstants.TOTAL_HOURS.equalsIgnoreCase(columnName)) {
			attendanceDetails.setTotalHours(dataStr);
		} else if (AttendanceDataConstants.SHIFT.equalsIgnoreCase(columnName)) {
			attendanceDetails.setShift(dataStr);
		} else if (AttendanceDataConstants.STATUS.equalsIgnoreCase(columnName)) {
			attendanceDetails.setStatus(dataStr);
		} 
		}catch (Exception exception) {
			log.debug("Exception occured."+exception.getMessage());
		}
		log.info("End setAttendanceDetailsBeanData.");
		return attendanceDetails;
		
	}
	
	public void saveAttendanceDetails(List<EmployeeAttendanceDetails> employeeAttendanceDetailsList,EmployeeService employeeService,AttendanceService attendanceService){
		log.info("Start saveAttendanceDetails.");
		try {
		for (EmployeeAttendanceDetails  employeeAttendanceDetails : employeeAttendanceDetailsList) {
			String empOriginalId=employeeAttendanceDetails.getEmployeeOriginalId();
			Employee employee = employeeService.getEmployeeByOrginalId(empOriginalId);
			if(employee != null){
			employeeAttendanceDetails.setEmployeeId(employee.getEmployeeId());
		  	//save empAttendanceDetails.
			attendanceService.saveUpdateEmpAttendanceDeatils(employeeAttendanceDetails);
			}
			}//forloop end.
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Exception occured."+e.getMessage());
		}
		log.info("End saveAttendanceDetails.");
	}
	
public File convertMultiPartFileToFile(CommonsMultipartFile attendanceDetailsFile){
	log.info("Start convertMultiPartFileToFile.");
	File convertedFile = new File(attendanceDetailsFile.getOriginalFilename());
	try{
	convertedFile.createNewFile(); 
    FileOutputStream fos = new FileOutputStream(convertedFile); 
     fos.write(attendanceDetailsFile.getBytes());
    fos.close(); 
	}
	catch(Exception exception){
		log.debug("Exception occured."+exception.getMessage());
	}
	log.info("End convertMultiPartFileToFile.");
    return convertedFile;
	}
}