package cn.com.sinosoft.reins.MySpringBootTestProject.web.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.sinosoft.reins.MySpringBootTestProject.web.service.WaCaiService;
import cn.com.sinosoft.reins.MySpringBootTestProject.web.vo.WacaiAccountVo;

@RestController
@RequestMapping("/wacai")
public class WaCaiController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${params.wacai.filepath}")
	private String filePath;
	
	@Autowired
	private WaCaiService waCaiService;
	
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;
	
	@RequestMapping(value = "/convert")
	public String convertExcel() {
		ArrayList<WacaiAccountVo> accountVos;
		try {
			accountVos = waCaiService.convertExcel(filePath,response);
		} catch (Exception e2) {
			logger.error(e2.getMessage(),e2);
			return "failed";
		}
		return "success";
		
	}
}
