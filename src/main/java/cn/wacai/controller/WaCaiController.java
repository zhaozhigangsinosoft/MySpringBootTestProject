package cn.wacai.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.wacai.service.AlipayService;
import cn.wacai.service.WaCaiService;
import cn.wacai.service.WeixinService;
import cn.wacai.vo.WacaiAccountVo;

@RestController
@RequestMapping("/wacai")
public class WaCaiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//配置文件中获取账本文件读取路径
	@Value("${params.wacai.filepath}")
	private String filePath;
	
	@Autowired
	private WaCaiService waCaiService;
	
	@Autowired
	private WeixinService weixinService;
	
	@Autowired
	private AlipayService alipayService;
	
	@Autowired
	private HttpServletResponse response;
	
	@RequestMapping(value = "/convert")
	public String convertExcel() {
		ArrayList<WacaiAccountVo> accountVos = new ArrayList<>();
		try {
			//读取微信账本文件转换为挖财账本对象列表
			accountVos.addAll(weixinService.convertExcel(filePath));
			//读取支付宝账本文件转换为挖财账本对象列表
			accountVos.addAll(alipayService.convertExcel(filePath));
			//根据交易对方和交易商品等信息判断交易类型
			waCaiService.recognitionType(accountVos);
			//将挖财账本对象转换为excel下载
			waCaiService.exportExcel(accountVos, response);
		} catch (Exception e2) {
			//如果转换发生异常则返回失败
			logger.error(e2.getMessage(),e2);
			return "failed";
		}
		return "success";
		
	}
}
