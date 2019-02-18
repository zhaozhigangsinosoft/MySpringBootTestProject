package cn.wacai.service;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import cn.wacai.vo.WacaiAccountVo;

public interface WaCaiService {
	public ArrayList<WacaiAccountVo> convertExcel(String filePath,
			HttpServletResponse response);
}
