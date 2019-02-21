package cn.wacai.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import cn.util.RegTest;
import cn.wacai.service.WaCaiService;
import cn.wacai.vo.WacaiAccountVo;

/**
 * 挖财账本文件处理服务接口实现类
 * @author ZhaoZhigang
 *
 */
@Service
public class WaCaiServiceImpl implements WaCaiService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 将挖财账本文件列表转换为excel文件并存到response中进行下载
	 * @param wacaiAccountVoList
	 * @param response
	 */
	@Override
	public void exportExcel(ArrayList<WacaiAccountVo> wacaiAccountVoList
			, HttpServletResponse response) {
		XSSFWorkbook workbook = new XSSFWorkbook();
        //创建支出sheet页
		XSSFSheet sheetSupport = workbook.createSheet("支出");
		//创建收入sheet页
		XSSFSheet sheetCollection = workbook.createSheet("收入");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //创建支出页表头
        this.setTitleSupport(workbook, sheetSupport);
        //创建收入页表头
        this.setTitleCollection(workbook, sheetCollection);

        //定义支出页行索引
        int rowNumSupport = 1;
        //定义收入页行索引
        int rowNumCollection = 1;
        for (WacaiAccountVo wacaiAccountVo:wacaiAccountVoList) {
        	if(wacaiAccountVo.getCollectionOrSupport().equals("支出")) {
        		//如果对象收支属性为支出，则按支出页格式设置表格数据
        		XSSFRow row = sheetSupport.createRow(rowNumSupport);
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
        		//设置交易金额单元格格式
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
        		rowNumSupport++;
        	}else if(wacaiAccountVo.getCollectionOrSupport().equals("收入")) {
        		//如果对象收支属性为支出，则按支出页格式设置表格数据
        		XSSFRow row = sheetCollection.createRow(rowNumCollection);
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
        		//设置交易金额单元格格式
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
        		rowNumCollection++;
        	}
        }
        //以当前时间命名导出的账本文件
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
	
	/**
	 * 设置支出页表头
	 * @param workbook
	 * @param sheet
	 */
	private void setTitleSupport(XSSFWorkbook workbook, XSSFSheet sheet){
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

        //设置为居中加粗，红色
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
	
	/**
	 * 设置收入页表头
	 * @param workbook
	 * @param sheet
	 */
	private void setTitleCollection(XSSFWorkbook workbook, XSSFSheet sheet){
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
		
		//设置为居中加粗，红色
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

	/**
	 * 据交易对方和交易商品等信息判断交易类型
	 * @param accountVos
	 */
	@Override
	public void recognitionType(ArrayList<WacaiAccountVo> accountVos) {
		for (Iterator<WacaiAccountVo> iterator = accountVos.iterator();
				iterator.hasNext();) {
			WacaiAccountVo wacaiAccountVo = (WacaiAccountVo) iterator.next();
			//根据交易时间生成日历对象
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(wacaiAccountVo.getConsumptionDate());
			//定义交易时间的小时，用于后面的逻辑判断
			int hour=calendar.get(Calendar.HOUR_OF_DAY);
			if(wacaiAccountVo.getCollectionOrSupport().equals("支出")) {
				//当对象为包含以下字符时，为一日三餐，需要根据交易时间判断早中晚
				if(RegTest.match(wacaiAccountVo.getTradingParty(), 
						"^.*(出门人|王军|申广涛|太阳|餐饮|板面|小树林水煮鱼|"
								+ "张记酱牛肉|烤全鱼|锅包肉|老胜香|橘和柠|心语|回头一看|"
								+ "梦|周志伟|张金梁|王思铭|"
								+ "为了生活而奋斗|吉野家|金/鑫).*$")||
						RegTest.match(wacaiAccountVo.getCommodity(), 
								"^.*(饭|肉|面|米|鱼|菜|美团).*$")
						) {
					wacaiAccountVo.setExpenditureCategories("餐饮");
					if(hour>=6&&hour<=10) {
						wacaiAccountVo.setExpenditureCategory("早餐");
					}else if(hour>=11&&hour<=15) {
						wacaiAccountVo.setExpenditureCategory("午餐");
					}else if(hour>=16&&hour<=23) {
						wacaiAccountVo.setExpenditureCategory("晚餐");
					}
				}
				
				//根据交易对方判断交易类型
				if(RegTest.match(wacaiAccountVo.getTradingParty(),
						"^.*(超市|冀中小武|家具).*$")) {
					wacaiAccountVo.setExpenditureCategories("购物");
					wacaiAccountVo.setExpenditureCategory("家居百货");
				}
				if(RegTest.match(wacaiAccountVo.getTradingParty(),
						"^.*(李志杰).*$")) {
					wacaiAccountVo.setExpenditureCategories("居家");
					wacaiAccountVo.setExpenditureCategory("美发美容");
				}
				if(RegTest.match(wacaiAccountVo.getTradingParty(),
						"^.*(铂涛).*$")) {
					wacaiAccountVo.setExpenditureCategories("居家");
					wacaiAccountVo.setExpenditureCategory("住宿房租");
				}
				if(RegTest.match(wacaiAccountVo.getTradingParty(),
						"^.*(李记副食调料|刘进|利达鲜切面|李延辉|"
								+ "彩丽市场大刀凉皮|大名府任记香油坊|王礼状|"
								+ "花自飘零水自流|幸运的人|朱家烘培|任我行|锋哥|梅英|恭喜发财).*$")) {
					wacaiAccountVo.setExpenditureCategories("餐饮");
					wacaiAccountVo.setExpenditureCategory("买菜原料");
				}
				if(RegTest.match(wacaiAccountVo.getTradingParty(),
						"^.*(好人，彩丽园店|果生鲜).*$")) {
					wacaiAccountVo.setExpenditureCategories("餐饮");
					wacaiAccountVo.setExpenditureCategory("饮料水果");
				}
				if(RegTest.match(wacaiAccountVo.getTradingParty(),
						"^.*(等待绽放胖子干货).*$")) {
					wacaiAccountVo.setExpenditureCategories("餐饮");
					wacaiAccountVo.setExpenditureCategory("零食");
				}
				if(RegTest.match(wacaiAccountVo.getTradingParty(),
						"^.*(公交).*$")) {
					wacaiAccountVo.setExpenditureCategories("交通");
					wacaiAccountVo.setExpenditureCategory("公交");
				}
				
				//根据商品名称判断交易类型
				if(RegTest.match(wacaiAccountVo.getCommodity(),
						"^.*(摩摩哒).*$")) {
					wacaiAccountVo.setExpenditureCategories("娱乐");
					wacaiAccountVo.setExpenditureCategory("娱乐其他");
				}
				if(wacaiAccountVo.getCommodity().contains("滴滴")) {
					wacaiAccountVo.setExpenditureCategories("交通");
					wacaiAccountVo.setExpenditureCategory("打车");
				}
				if(RegTest.match(wacaiAccountVo.getCommodity(),
						"^.*(地铁).*$")) {
					wacaiAccountVo.setExpenditureCategories("交通");
					wacaiAccountVo.setExpenditureCategory("地铁");
				}
				if(RegTest.match(wacaiAccountVo.getTradingParty(),
						"^.*(水果).*$")) {
					wacaiAccountVo.setExpenditureCategories("餐饮");
					wacaiAccountVo.setExpenditureCategory("饮料水果");
				}
				if(RegTest.match(wacaiAccountVo.getCommodity(),
						"^.*(鲜花).*$")) {
					wacaiAccountVo.setExpenditureCategories("人情");
					wacaiAccountVo.setExpenditureCategory("物品");
				}
				if(RegTest.match(wacaiAccountVo.getCommodity(),
						"^.*(哈啰|单车).*$")) {
					wacaiAccountVo.setExpenditureCategories("交通");
					wacaiAccountVo.setExpenditureCategory("自行车");
				}
				if(wacaiAccountVo.getCommodity().contains("12306")) {
					wacaiAccountVo.setExpenditureCategories("交通");
					wacaiAccountVo.setExpenditureCategory("火车");
				}
				if(wacaiAccountVo.getCommodity().contains("中国联通")) {
					wacaiAccountVo.setExpenditureCategories("居家");
					wacaiAccountVo.setExpenditureCategory("电脑宽带");
				}
				if(RegTest.match(wacaiAccountVo.getCommodity(),
						"^.*(顺丰|邮政).*$")||
						RegTest.match(wacaiAccountVo.getTradingParty(),
								"^.*(菜鸟驿站).*$")) {
					wacaiAccountVo.setExpenditureCategories("居家");
					wacaiAccountVo.setExpenditureCategory("快递邮政");
				}
				if("\"亲密付\"".equals(wacaiAccountVo.getCommodity())) {
					wacaiAccountVo.setExpenditureCategories("人情");
					wacaiAccountVo.setExpenditureCategory("代付款");
				}
			}else {
				wacaiAccountVo.setExpenditureCategories("退款返款");
			}
		}
	}
	
}
