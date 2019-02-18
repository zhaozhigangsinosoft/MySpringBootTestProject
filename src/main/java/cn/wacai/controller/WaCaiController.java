package cn.wacai.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.wacai.service.WaCaiService;
import cn.wacai.vo.WacaiAccountVo;

@RestController
@RequestMapping("/wacai")
public class WaCaiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${params.wacai.filepath}")
	private String filePath;
	
	@Autowired
	private WaCaiService waCaiService;
	
	@Autowired
	private HttpServletResponse response;
	
	@RequestMapping(value = "/convert")
	public String convertExcel() {
		@SuppressWarnings("unused")
		ArrayList<WacaiAccountVo> accountVos = null;
		try {
			accountVos = waCaiService.convertExcel(filePath,response);
		} catch (Exception e2) {
			logger.error(e2.getMessage(),e2);
			return "failed";
		}
		return "success";
		
	}
}
