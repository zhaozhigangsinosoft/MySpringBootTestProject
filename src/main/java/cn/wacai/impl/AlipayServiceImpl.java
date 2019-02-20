package cn.wacai.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.util.FileUtils;
import cn.util.RegTest;
import cn.wacai.service.AlipayService;
import cn.wacai.vo.WacaiAccountVo;
import cn.wacai.vo.AlipayAccountVo;

@Service
public class AlipayServiceImpl implements AlipayService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public ArrayList<WacaiAccountVo> convertExcel(String filePath) {
		String accountFileName = null;
		ArrayList<WacaiAccountVo> wacaiAccountVoList = new ArrayList<>();
		try {
			ArrayList<File> fileList = FileUtils.getFiles(filePath,true);
			for (Iterator<File> iterator = fileList.iterator(); 
					iterator.hasNext();) {
				File file = (File) iterator.next();
				String fileName = file.getName();
				if(RegTest.test(fileName, "^alipay_record.+\\.csv$")) {
					accountFileName = file.getPath();
					ArrayList<AlipayAccountVo> alipayAccountVoList 
							= this.readFile(accountFileName);
					wacaiAccountVoList.addAll(this.convertList(alipayAccountVoList));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		
		return wacaiAccountVoList;
	}
	
	


	private ArrayList<WacaiAccountVo> convertList(
			ArrayList<AlipayAccountVo> alipayAccountVoList) {
		
		HashMap<String,String> accountMap = new HashMap<>();
		accountMap.put("浦发银行(3337)", "XX浦发银行储蓄卡");
		accountMap.put("招商银行(7038)", "XX招行信用卡");
		accountMap.put("零钱通", "xx微信");
		accountMap.put("零钱", "xx微信");
		
		ArrayList<WacaiAccountVo> wacaiAccountVoList 
				= new ArrayList<WacaiAccountVo>();
		for (Iterator<AlipayAccountVo> iterator = 
				alipayAccountVoList.iterator();iterator.hasNext();) {
			AlipayAccountVo alipayAccountVo = iterator.next();
			if(RegTest.test(alipayAccountVo.getCollectionOrSupport(), 
					"^.*(支出|收入).*$")) {
				WacaiAccountVo wacaiAccountVo = new WacaiAccountVo();
				wacaiAccountVo.setCollectionOrSupport(
						alipayAccountVo.getCollectionOrSupport());
				wacaiAccountVo.setExpenditureCategories("居家");
				wacaiAccountVo.setExpenditureCategory("漏记款");
				wacaiAccountVo.setAccount("XX支付宝");
				wacaiAccountVo.setMemberAmount("自己:"+
						alipayAccountVo.getAmount().toString());
				wacaiAccountVo.setCurrency("人民币");
				wacaiAccountVo.setProject("日常");
				wacaiAccountVo.setBusiness("");
				wacaiAccountVo.setReimbursement("非报销");
				wacaiAccountVo.setConsumptionDate(
						alipayAccountVo.getTransactionCreationTime());
				wacaiAccountVo.setConsumptionAmount(alipayAccountVo.getAmount());
				wacaiAccountVo.setRemarks(alipayAccountVo.getTradingParty()+"-"+
						alipayAccountVo.getTradeName()+"-"+
						alipayAccountVo.getCollectionOrSupport()+"-"+
						alipayAccountVo.getAmount().toString()
						);
				wacaiAccountVo.setAccountBook("日常账本");
				
				this.recognitionType(alipayAccountVo, wacaiAccountVo);
				
				wacaiAccountVoList.add(wacaiAccountVo);
			}
		}
		return wacaiAccountVoList;
	}
	
	private void recognitionType(AlipayAccountVo alipayAccountVo,
			WacaiAccountVo wacaiAccountVo) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(alipayAccountVo.getTransactionCreationTime());
		int hour=calendar.get(Calendar.HOUR_OF_DAY);
		if(alipayAccountVo.getCollectionOrSupport().equals("支出")) {
			if(RegTest.test(alipayAccountVo.getTradingParty(), 
					"^.*(出门人|王军|申广涛|太阳).*$")||
					RegTest.test(alipayAccountVo.getTradeName(), 
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
			if(RegTest.test(alipayAccountVo.getTradingParty(),
					"^.*(超市|冀中小武).*$")) {
				wacaiAccountVo.setExpenditureCategories("购物");
				wacaiAccountVo.setExpenditureCategory("家居百货");
			}
			if(RegTest.test(alipayAccountVo.getTradingParty(),
					"^.*(李志杰).*$")) {
				wacaiAccountVo.setExpenditureCategories("居家");
				wacaiAccountVo.setExpenditureCategory("美发美容");
			}
			if(RegTest.test(alipayAccountVo.getTradingParty(),
					"^.*(李记副食调料|刘进).*$")) {
				wacaiAccountVo.setExpenditureCategories("餐饮");
				wacaiAccountVo.setExpenditureCategory("买菜原料");
			}
			if(RegTest.test(alipayAccountVo.getTradeName(), "^.*(摩摩哒).*$")) {
				wacaiAccountVo.setExpenditureCategories("娱乐");
				wacaiAccountVo.setExpenditureCategory("娱乐其他");
			}
			if(alipayAccountVo.getTradeName().contains("滴滴打车")) {
				wacaiAccountVo.setExpenditureCategories("交通");
				wacaiAccountVo.setExpenditureCategory("打车");
			}
			if(alipayAccountVo.getTradeName().contains("单车")) {
				wacaiAccountVo.setExpenditureCategories("交通");
				wacaiAccountVo.setExpenditureCategory("自行车");
			}
			if(alipayAccountVo.getTradeName().contains("12306")) {
				wacaiAccountVo.setExpenditureCategories("交通");
				wacaiAccountVo.setExpenditureCategory("火车");
			}
			if(alipayAccountVo.getTradeName().contains("中国联通")) {
				wacaiAccountVo.setExpenditureCategories("居家");
				wacaiAccountVo.setExpenditureCategory("电脑宽带");
			}
			if(alipayAccountVo.getTradeName().contains("顺丰")) {
				wacaiAccountVo.setExpenditureCategories("居家");
				wacaiAccountVo.setExpenditureCategory("快递邮政");
			}
			if("\"亲密付\"".equals(alipayAccountVo.getTradeName())) {
				wacaiAccountVo.setExpenditureCategories("人情");
				wacaiAccountVo.setExpenditureCategory("代付款");
			}
		}else {
			wacaiAccountVo.setExpenditureCategories("退款返款");
		}
	}

	private ArrayList<AlipayAccountVo> readFile(String filePath) {
		
		boolean startFlag = false;
		ArrayList<AlipayAccountVo> accountVoList 
				= new ArrayList<AlipayAccountVo>();
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
					AlipayAccountVo alipayAccountVo= new AlipayAccountVo();
					String[] splitLines = line.split(",");
					alipayAccountVo.setTransactionNumber(splitLines[index++]);
					alipayAccountVo.setMerchantOrderNumber(splitLines[index++]);
					try {
						alipayAccountVo.setTransactionCreationTime(
								sdf.parse(splitLines[index++]));
					} catch (ParseException e) {
						logger.error(e.getMessage(),e);
					}
					try {
						alipayAccountVo.setPaymentTime(
								sdf.parse(splitLines[index++]));
					} catch (ParseException e) {
						logger.error(e.getMessage(),e);
					}
					try {
						alipayAccountVo.setLatestRevisionTime(
								sdf.parse(splitLines[index++]));
					} catch (ParseException e) {
						logger.error(e.getMessage(),e);
					}
					alipayAccountVo.setSourceOfTransaction(splitLines[index++]);
					alipayAccountVo.setType(splitLines[index++]);
					alipayAccountVo.setTradingParty(splitLines[index++]);
					alipayAccountVo.setTradeName(splitLines[index++]);
					alipayAccountVo.setAmount(
							new BigDecimal(splitLines[index++]));
					alipayAccountVo.setCollectionOrSupport(splitLines[index++]);
					alipayAccountVo.setTradingStatus(splitLines[index++]);
					alipayAccountVo.setServiceFee(
							new BigDecimal(splitLines[index++]));
					alipayAccountVo.setSuccessfulRefund(
							new BigDecimal(splitLines[index++]));
					alipayAccountVo.setRemarks(splitLines[index++]);
					alipayAccountVo.setFundStatus(splitLines[index++]);
					accountVoList.add(alipayAccountVo);
				}
				if(!startFlag&&line.startsWith("交易号")) {
					startFlag = true;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return accountVoList;
	}
}
