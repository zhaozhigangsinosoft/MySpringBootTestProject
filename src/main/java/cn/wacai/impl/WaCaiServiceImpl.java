package cn.wacai.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

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

import cn.util.FileUtils;
import cn.util.RegTest;
import cn.wacai.service.WaCaiService;
import cn.wacai.vo.WacaiAccountVo;
import cn.wacai.vo.WeixinAccountVo;

@Service
public class WaCaiServiceImpl implements WaCaiService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Override
	public ArrayList<WacaiAccountVo> convertExcel(String filePath
			, HttpServletResponse response) {
		String accountFileName = null;
		try {
			ArrayList<File> fileList = FileUtils.getFiles(filePath,true);
			for (Iterator<File> iterator = fileList.iterator(); 
					iterator.hasNext();) {
				File file = (File) iterator.next();
				String fileName = file.getName();
				if(RegTest.test(fileName, "^微信支付账单.+\\.csv$")) {
					accountFileName = file.getPath();
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		
		ArrayList<WeixinAccountVo> weixinAccountVoList 
				= this.readFile(accountFileName);
		ArrayList<WacaiAccountVo> wacaiAccountVoList 
				= this.convertList(weixinAccountVoList);
		this.exportExcel(wacaiAccountVoList,response);
		return wacaiAccountVoList;
	}

	private void exportExcel(ArrayList<WacaiAccountVo> wacaiAccountVoList
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

	private ArrayList<WacaiAccountVo> convertList(
			ArrayList<WeixinAccountVo> weixinAccountVoList) {
		
		HashMap<String,String> accountMap = new HashMap<>();
		accountMap.put("浦发银行(3337)", "XX浦发银行储蓄卡");
		accountMap.put("招商银行(7038)", "XX招行信用卡");
		accountMap.put("零钱通", "xx微信");
		accountMap.put("零钱", "xx微信");
		
		ArrayList<WacaiAccountVo> wacaiAccountVoList 
				= new ArrayList<WacaiAccountVo>();
		for (Iterator<WeixinAccountVo> iterator = 
				weixinAccountVoList.iterator();iterator.hasNext();) {
			WeixinAccountVo weixinAccountVo = iterator.next();
			WacaiAccountVo wacaiAccountVo = new WacaiAccountVo();
			wacaiAccountVo.setCollectionOrSupport(
					weixinAccountVo.getCollectionOrSupport());
			wacaiAccountVo.setExpenditureCategories("居家");
			wacaiAccountVo.setExpenditureCategory("漏记款");
			if(weixinAccountVo.getCollectionOrSupport().equals("支出")) {
				wacaiAccountVo.setAccount(
						accountMap.get(weixinAccountVo.getPaymentMethod()));
				if("\"亲密付\"".equals(weixinAccountVo.getCommodity())){
					wacaiAccountVo.setMemberAmount("配偶:"+
							weixinAccountVo.getAmount().toString());
				}else {
					wacaiAccountVo.setMemberAmount("自己:"+
							weixinAccountVo.getAmount().toString());
				}
			}else {
				wacaiAccountVo.setAccount("xx微信");
				wacaiAccountVo.setMemberAmount("家庭公用:"+
						weixinAccountVo.getAmount().toString());
			}
			wacaiAccountVo.setCurrency("人民币");
			wacaiAccountVo.setProject("日常");
			wacaiAccountVo.setBusiness("");
			wacaiAccountVo.setReimbursement("非报销");
			wacaiAccountVo.setConsumptionDate(
					weixinAccountVo.getTransactionTime());
			wacaiAccountVo.setConsumptionAmount(weixinAccountVo.getAmount());
			wacaiAccountVo.setRemarks(weixinAccountVo.getTradingParty()+"-"+
					weixinAccountVo.getCommodity()+"-"+
					weixinAccountVo.getCollectionOrSupport()+"-"+
					weixinAccountVo.getAmount().toString()
					);
			wacaiAccountVo.setAccountBook("日常账本");
			
			this.recognitionType(weixinAccountVo, wacaiAccountVo);
			
			wacaiAccountVoList.add(wacaiAccountVo);
		}
		return wacaiAccountVoList;
	}
	
	private void recognitionType(WeixinAccountVo weixinAccountVo,
			WacaiAccountVo wacaiAccountVo) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(weixinAccountVo.getTransactionTime());
		int hour=calendar.get(Calendar.HOUR_OF_DAY);
		if(weixinAccountVo.getCollectionOrSupport().equals("支出")) {
			if(RegTest.test(weixinAccountVo.getTradingParty(), 
					"^.*(出门人|王军|申广涛|太阳).*$")||
					RegTest.test(weixinAccountVo.getCommodity(), 
							"^.*(饭|肉|面|米|鱼|菜|美团).*$")
					) {
				wacaiAccountVo.setExpenditureCategories("餐饮");
				if(hour>=6&&hour<=9) {
					wacaiAccountVo.setExpenditureCategory("早餐");
				}else if(hour>=10&&hour<=14) {
					wacaiAccountVo.setExpenditureCategory("午餐");
				}else if(hour>=15&&hour<=20) {
					wacaiAccountVo.setExpenditureCategory("晚餐");
				}
			}
			if(RegTest.test(weixinAccountVo.getTradingParty(),
					"^.*(超市|冀中小武).*$")) {
				wacaiAccountVo.setExpenditureCategories("购物");
				wacaiAccountVo.setExpenditureCategory("家居百货");
			}
			if(RegTest.test(weixinAccountVo.getTradingParty(),
					"^.*(李志杰).*$")) {
				wacaiAccountVo.setExpenditureCategories("居家");
				wacaiAccountVo.setExpenditureCategory("美发美容");
			}
			if(RegTest.test(weixinAccountVo.getTradingParty(),
					"^.*(李记副食调料|刘进).*$")) {
				wacaiAccountVo.setExpenditureCategories("餐饮");
				wacaiAccountVo.setExpenditureCategory("买菜原料");
			}
			if(RegTest.test(weixinAccountVo.getCommodity(), "^.*(摩摩哒).*$")) {
				wacaiAccountVo.setExpenditureCategories("娱乐");
				wacaiAccountVo.setExpenditureCategory("娱乐其他");
			}
			if(weixinAccountVo.getCommodity().contains("滴滴打车")) {
				wacaiAccountVo.setExpenditureCategories("交通");
				wacaiAccountVo.setExpenditureCategory("打车");
			}
			if(weixinAccountVo.getCommodity().contains("单车")) {
				wacaiAccountVo.setExpenditureCategories("交通");
				wacaiAccountVo.setExpenditureCategory("自行车");
			}
			if(weixinAccountVo.getCommodity().contains("12306")) {
				wacaiAccountVo.setExpenditureCategories("交通");
				wacaiAccountVo.setExpenditureCategory("火车");
			}
			if(weixinAccountVo.getCommodity().contains("中国联通")) {
				wacaiAccountVo.setExpenditureCategories("居家");
				wacaiAccountVo.setExpenditureCategory("电脑宽带");
			}
			if(weixinAccountVo.getCommodity().contains("顺丰")) {
				wacaiAccountVo.setExpenditureCategories("居家");
				wacaiAccountVo.setExpenditureCategory("快递邮政");
			}
			if("\"亲密付\"".equals(weixinAccountVo.getCommodity())) {
				wacaiAccountVo.setExpenditureCategories("人情");
				wacaiAccountVo.setExpenditureCategory("代付款");
			}
		}else {
			wacaiAccountVo.setExpenditureCategories("退款返款");
		}
	}

	private ArrayList<WeixinAccountVo> readFile(String filePath) {
		
		boolean startFlag = false;
		ArrayList<WeixinAccountVo> accountVoList 
				= new ArrayList<WeixinAccountVo>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try (FileReader reader = new FileReader(filePath);
			 BufferedReader br = new BufferedReader(reader) 
			 // 建立一个对象，它把文件内容转成计算机能读懂的语言
		) {
			String line;
			// 网友推荐更加简洁的写法
			while ((line = br.readLine()) != null) {
				// 一次读入一行数据
				if(startFlag) {
					logger.info(line);
					int index = 0;
					WeixinAccountVo weixinAccountVo= new WeixinAccountVo();
					String[] splitLines = line.split(",");
					try {
						weixinAccountVo.setTransactionTime(
								sdf.parse(splitLines[index++]));
					} catch (ParseException e) {
						logger.error(e.getMessage(),e);
					}
					weixinAccountVo.setTransactionType(splitLines[index++]);
					weixinAccountVo.setTradingParty(splitLines[index++]);
					weixinAccountVo.setCommodity(splitLines[index++]);
					weixinAccountVo.setCollectionOrSupport(
							splitLines[index++]);
					weixinAccountVo.setAmount(new BigDecimal(
							splitLines[index++].replaceAll("¥", "")));
					weixinAccountVo.setPaymentMethod(splitLines[index++]);
					weixinAccountVo.setCurrentState(splitLines[index++]);
					weixinAccountVo.setTransactionNumber(splitLines[index++]); 
					weixinAccountVo.setMerchantNumber(splitLines[index++]);
					weixinAccountVo.setRemarks(splitLines[index++]);
					accountVoList.add(weixinAccountVo);
				}
				if(!startFlag&&line.startsWith("交易时间")) {
					startFlag = true;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return accountVoList;
	}
}
