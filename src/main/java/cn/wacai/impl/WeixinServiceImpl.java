package cn.wacai.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.util.FileUtils;
import cn.util.RegTest;
import cn.wacai.service.WeixinService;
import cn.wacai.vo.WacaiAccountVo;
import cn.wacai.vo.WeixinAccountVo;

@Service
public class WeixinServiceImpl implements WeixinService {
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
				if(RegTest.test(fileName, "^微信支付账单.+\\.csv$")) {
					accountFileName = file.getPath();
					ArrayList<WeixinAccountVo> weixinAccountVoList 
							= this.readFile(accountFileName);
					wacaiAccountVoList.addAll(this.convertList(weixinAccountVoList));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return wacaiAccountVoList;
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
			wacaiAccountVo.setTradingParty(
					weixinAccountVo.getTradingParty());
			wacaiAccountVo.setCommodity(
					weixinAccountVo.getCommodity());
			
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
			
			wacaiAccountVoList.add(wacaiAccountVo);
		}
		return wacaiAccountVoList;
	}
	
	private ArrayList<WeixinAccountVo> readFile(String filePath) {
		
		boolean startFlag = false;
		ArrayList<WeixinAccountVo> accountVoList 
				= new ArrayList<WeixinAccountVo>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
			String line;
			// 网友推荐更加简洁的写法
			while ((line = br.readLine()) != null) {
				// 一次读入一行数据
				if(startFlag) {
					int index = 0;
					WeixinAccountVo weixinAccountVo= new WeixinAccountVo();
					String[] splitLines = line.split(",");
					try {
						weixinAccountVo.setTransactionTime(
								sdf.parse(splitLines[index++]));
					} catch (ParseException e) {
						logger.error("TransactionTime保存失败");
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
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		return accountVoList;
	}
}
