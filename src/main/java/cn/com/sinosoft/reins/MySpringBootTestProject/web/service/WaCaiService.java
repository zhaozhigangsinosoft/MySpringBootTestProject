package cn.com.sinosoft.reins.MySpringBootTestProject.web.service;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import cn.com.sinosoft.reins.MySpringBootTestProject.web.vo.WacaiAccountVo;

public interface WaCaiService {
	public ArrayList<WacaiAccountVo> convertExcel(String filePath,
			HttpServletResponse response);
}
