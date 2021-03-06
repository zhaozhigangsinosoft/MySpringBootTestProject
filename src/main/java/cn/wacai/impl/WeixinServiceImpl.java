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

/**
 * 微信账本转换服务接口实现类
 * @author ZhaoZhigang
 *
 */
@Service
public class WeixinServiceImpl implements WeixinService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将路径中的微信账本文件转换为挖财账本对象列表
     * @param filePath
     * @return ArrayList<WacaiAccountVo>
     */
    @Override
    public ArrayList<WacaiAccountVo> convertExcel(String filePath) {
        String accountFileName = null;
        ArrayList<WacaiAccountVo> wacaiAccountVoList = new ArrayList<>();
        try {
            //迭代获取路径下所有文件
            ArrayList<File> fileList = FileUtils.getFiles(filePath,true);
            for (Iterator<File> iterator = fileList.iterator(); 
                    iterator.hasNext();) {
                File file = (File) iterator.next();
                String fileName = file.getName();
                //遍历所有文件名，如果是微信账本的规则，则进行解析处理
                if(RegTest.match(fileName, "^微信支付账单.+\\.csv$")) {
                    accountFileName = file.getPath();
                    ArrayList<WeixinAccountVo> weixinAccountVoList 
                            = this.readFile(accountFileName);
                    wacaiAccountVoList.addAll(
                            this.convertList(weixinAccountVoList));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        
        return wacaiAccountVoList;
    }

    /**
     * 将微信账本对象列表转换为挖财账本对象列表，并做初步对象赋值
     * @param weixinAccountVoList
     * @return ArrayList<WacaiAccountVo>
     */
    private ArrayList<WacaiAccountVo> convertList(
            ArrayList<WeixinAccountVo> weixinAccountVoList) {
        
        HashMap<String,String> accountMap = new HashMap<>();
        accountMap.put("浦发银行(3337)", "XX浦发银行储蓄卡");
        accountMap.put("招商银行(7038)", "XX招行信用卡");
        accountMap.put("中信银行(1794)", "中信银行储蓄卡");
        accountMap.put("零钱通", "xx微信");
        accountMap.put("零钱", "xx微信");
        
        ArrayList<WacaiAccountVo> wacaiAccountVoList 
                = new ArrayList<WacaiAccountVo>();
        for (Iterator<WeixinAccountVo> iterator = 
                weixinAccountVoList.iterator();iterator.hasNext();) {
            WeixinAccountVo weixinAccountVo = iterator.next();
            //仅对交易类型为收入或支出的数据进行处理
            if(RegTest.match(weixinAccountVo.getCollectionOrSupport(), 
                    "^.*(支出|收入).*$")) {
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
                    wacaiAccountVo.setExpenditureCategories("退款返款");
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
                //处理完毕后将对象添加到返回列表中
                wacaiAccountVoList.add(wacaiAccountVo);
            }
        }
        return wacaiAccountVoList;
    }
    
    /**
     * 从csv文件中读取微信账本，转换为微信账本对象列表
     * @param filePath
     * @return ArrayList<WeixinAccountVo>
     */
    private ArrayList<WeixinAccountVo> readFile(String filePath) {
        //定义一个标识，需要解析此行数据时设置为true,否则为false
        boolean startFlag = false;
        ArrayList<WeixinAccountVo> accountVoList 
                = new ArrayList<WeixinAccountVo>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
            String line;
            // 逐行遍历文件内容，一次读入一行数据
            while ((line = br.readLine()) != null) {
                //标识为true，正式开始解析账单数据
                if(startFlag) {
                    int index = 0;
                    WeixinAccountVo weixinAccountVo= new WeixinAccountVo();
                    //line = line.replaceAll("\t", ",");
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
                //“交易时间”开头的下一行，账本数据文件正式开始，
                //将标识设置为true,下次循环开始解析
                if(!startFlag&&line.startsWith("交易时间")) {
                    startFlag = true;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            //关闭bufferReader
            try {
                br.close();
            } catch (IOException e) {
            }
        }
        return accountVoList;
    }
}
