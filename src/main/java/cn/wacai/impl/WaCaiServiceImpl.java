package cn.wacai.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.wacai.service.WaCaiService;
import cn.wacai.vo.WacaiAccountVo;

@Service
public class WaCaiServiceImpl implements WaCaiService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void exportExcel(ArrayList<WacaiAccountVo> wacaiAccountVoList
			, HttpServletResponse response) {
		XSSFWorkbook workbook = new XSSFWorkbook();
        //创建一个Excel表单,参数为sheet的名字
		XSSFSheet sheet1 = workbook.createSheet("支出");
		XSSFSheet sheet2 = workbook.createSheet("收入");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //创建表头
        setTitle1(workbook, sheet1);
        setTitle2(workbook, sheet2);

        //新增数据行，并且设置单元格数据
        int rowNum1 = 1;
        int rowNum2 = 1;
        for (WacaiAccountVo wacaiAccountVo:wacaiAccountVoList) {
        	if(wacaiAccountVo.getCollectionOrSupport().equals("支出")) {
        		XSSFRow row = sheet1.createRow(rowNum1);
        		int index = 0;
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getExpenditureCategories());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getExpenditureCategory());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getAccount());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getCurrency());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getProject());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getBusiness());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getReimbursement());
        		row.createCell(index++).setCellValue(
        				sdf.format(wacaiAccountVo.getConsumptionDate()));
        		
        		XSSFCell cell = row.createCell(index++);
        		XSSFDataFormat df = workbook.createDataFormat();
        		XSSFCellStyle contextstyle =workbook.createCellStyle();
        		contextstyle.setDataFormat(df.getFormat("#,##0.00"));
        		cell.setCellStyle(contextstyle);
        		cell.setCellValue(
        				wacaiAccountVo.getConsumptionAmount().doubleValue());
        		
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getMemberAmount());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getRemarks());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getAccountBook());
        		rowNum1++;
        	}else if(wacaiAccountVo.getCollectionOrSupport().equals("收入")) {
           		XSSFRow row = sheet2.createRow(rowNum2);
        		int index = 0;
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getExpenditureCategories());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getAccount());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getCurrency());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getProject());
        		row.createCell(index++).setCellValue("");
        		row.createCell(index++).setCellValue(
        				sdf.format(wacaiAccountVo.getConsumptionDate()));
        		XSSFCell cell = row.createCell(index++);
        		XSSFDataFormat df = workbook.createDataFormat();
        		XSSFCellStyle contextstyle =workbook.createCellStyle();
        		contextstyle.setDataFormat(df.getFormat("#,##0.00"));
        		cell.setCellStyle(contextstyle);
        		cell.setCellValue(
        				wacaiAccountVo.getConsumptionAmount().doubleValue());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getMemberAmount());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getRemarks());
        		row.createCell(index++).setCellValue(
        				wacaiAccountVo.getAccountBook());
        		rowNum2++;
        	}
        }
        String fileName = "exportWacaiAccount_"+sdf.format(new Date())+".xlsx";
        //清空response  
        response.reset();  
        //设置response的Header  
        response.addHeader("Content-Disposition", "attachment;filename="+
        		fileName);  
        OutputStream os = null;
		try {
			os = new BufferedOutputStream(response.getOutputStream());
		} catch (IOException e1) {
			logger.error(e1.getMessage(),e1);
		}  
        response.setContentType("application/vnd.ms-excel;charset=gb2312"); 
        //将excel写入到输出流中
        try {
			workbook.write(os);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
        try {
			os.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
        try {
			os.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	private void setTitle1(XSSFWorkbook workbook, XSSFSheet sheet){
        XSSFRow row = sheet.createRow(0);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        int index = 0;
        sheet.setColumnWidth(index++, 10*256);
        sheet.setColumnWidth(index++, 10*256);
        sheet.setColumnWidth(index++, 20*256);
        sheet.setColumnWidth(index++, 10*256);
        sheet.setColumnWidth(index++, 10*256);
        sheet.setColumnWidth(index++, 10*256);
        sheet.setColumnWidth(index++, 10*256);
        sheet.setColumnWidth(index++, 20*256);
        sheet.setColumnWidth(index++, 10*256);
        sheet.setColumnWidth(index++, 15*256);
        sheet.setColumnWidth(index++, 50*256);
        sheet.setColumnWidth(index++, 10*256);

        //设置为居中加粗
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(XSSFFont.COLOR_RED);
        style.setFont(font);

        XSSFCell cell;
        index = 0;
        cell = row.createCell(index++);
        cell.setCellValue("支出大类");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("支出小类");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("账户");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("币种");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("项目");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("商家");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("报销");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("消费日期");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("消费金额");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("成员金额");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("备注");
        cell.setCellStyle(style);
        cell = row.createCell(index++);
        cell.setCellValue("账本");
        cell.setCellStyle(style);
    }
	private void setTitle2(XSSFWorkbook workbook, XSSFSheet sheet){
		XSSFRow row = sheet.createRow(0);
		//设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
		int index = 0;
		sheet.setColumnWidth(index++, 10*256);
		sheet.setColumnWidth(index++, 10*256);
		sheet.setColumnWidth(index++, 10*256);
		sheet.setColumnWidth(index++, 10*256);
		sheet.setColumnWidth(index++, 10*256);
		sheet.setColumnWidth(index++, 20*256);
		sheet.setColumnWidth(index++, 10*256);
		sheet.setColumnWidth(index++, 15*256);
		sheet.setColumnWidth(index++, 50*256);
		sheet.setColumnWidth(index++, 10*256);
		
		//设置为居中加粗
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setColor(XSSFFont.COLOR_RED);
		style.setFont(font);
		
		XSSFCell cell;
		index = 0;
		cell = row.createCell(index++);
		cell.setCellValue("收入大类");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("账户");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("币种");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("项目");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("付款方");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("收入日期");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("收入金额");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("成员金额");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("备注");
		cell.setCellStyle(style);
		cell = row.createCell(index++);
		cell.setCellValue("账本");
		cell.setCellStyle(style);
	}
	
}
