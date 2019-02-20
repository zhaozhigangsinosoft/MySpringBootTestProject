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
			return null;
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
					"^.*(出门人|王军|申广涛|太阳|餐饮|板面|小树林水煮鱼|"
					+ "张记酱牛肉|烤全鱼|锅包肉|老胜香|橘和柠|心语|回头一看|梦|周志伟|张金梁|王思铭|"
					+ "为了生活而奋斗|吉野家|金/鑫).*$")||
					RegTest.test(weixinAccountVo.getCommodity(), 
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
					"^.*(铂涛).*$")) {
				wacaiAccountVo.setExpenditureCategories("居家");
				wacaiAccountVo.setExpenditureCategory("住宿房租");
			}
			if(RegTest.test(weixinAccountVo.getTradingParty(),
					"^.*(李记副食调料|刘进|利达鲜切面|李延辉|"
					+ "彩丽市场大刀凉皮|大名府任记香油坊|天津京东网络销售群，王礼状|"
					+ "花自飘零水自流|幸运的人|朱家烘培|任我行|锋哥|梅英|恭喜发财).*$")) {
				wacaiAccountVo.setExpenditureCategories("餐饮");
				wacaiAccountVo.setExpenditureCategory("买菜原料");
			}
			if(RegTest.test(weixinAccountVo.getTradingParty(),
					"^.*(好人，彩丽园店).*$")) {
				wacaiAccountVo.setExpenditureCategories("餐饮");
				wacaiAccountVo.setExpenditureCategory("饮料水果");
			}
			if(RegTest.test(weixinAccountVo.getTradingParty(),
					"^.*(等待绽放胖子干货).*$")) {
				wacaiAccountVo.setExpenditureCategories("餐饮");
				wacaiAccountVo.setExpenditureCategory("零食");
			}
			if(RegTest.test(weixinAccountVo.getCommodity(), "^.*(摩摩哒).*$")) {
				wacaiAccountVo.setExpenditureCategories("娱乐");
				wacaiAccountVo.setExpenditureCategory("娱乐其他");
			}
			if(weixinAccountVo.getCommodity().contains("滴滴")) {
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
			if(RegTest.test(weixinAccountVo.getCommodity(),
					"^.*(顺丰|邮政).*$")||
					RegTest.test(weixinAccountVo.getTradingParty(),
							"^.*(菜鸟驿站).*$")) {
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
