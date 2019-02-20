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
import cn.wacai.service.AlipayService;
import cn.wacai.vo.AlipayAccountVo;
import cn.wacai.vo.WacaiAccountVo;

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
				wacaiAccountVo.setTradingParty(
						alipayAccountVo.getTradingParty());
				wacaiAccountVo.setCommodity(
						alipayAccountVo.getTradeName());
				
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
				wacaiAccountVoList.add(wacaiAccountVo);
			}
		}
		return wacaiAccountVoList;
	}

	private ArrayList<AlipayAccountVo> readFile(String filePath) {
		
		boolean startFlag = false;
		ArrayList<AlipayAccountVo> accountVoList 
				= new ArrayList<AlipayAccountVo>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"GBK"));
			String line;
			// 网友推荐更加简洁的写法
			while ((line = br.readLine()) != null) {
				// 一次读入一行数据
				line = line.replaceAll(" ", "").replaceAll("	", "");
				if(line.startsWith("----------------------------------------")) {
					startFlag = false;
				}
				if(startFlag) {
					int index = 0;
					AlipayAccountVo alipayAccountVo= new AlipayAccountVo();
					String[] splitLines = line.split(",");
					alipayAccountVo.setTransactionNumber(splitLines[index++]);
					alipayAccountVo.setMerchantOrderNumber(splitLines[index++]);
					try {
						alipayAccountVo.setTransactionCreationTime(
								sdf.parse(splitLines[index++]));
					} catch (ParseException e) {
						logger.error("TransactionCreationTime保存失败");
						logger.error(e.getMessage(),e);
					}
					try {
						alipayAccountVo.setPaymentTime(
								sdf.parse(splitLines[index++]));
					} catch (ParseException e) {
						logger.warn("PaymentTime保存失败");
					}
					try {
						alipayAccountVo.setLatestRevisionTime(
								sdf.parse(splitLines[index++]));
					} catch (ParseException e) {
						logger.warn("LatestRevisionTime保存失败");
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
					accountVoList.add(alipayAccountVo);
				}
				if(!startFlag&&line.startsWith("交易号")) {
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
