package cn.wacai.service;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import cn.wacai.vo.WacaiAccountVo;

public interface WaCaiService {
	public void exportExcel(ArrayList<WacaiAccountVo> wacaiAccountVoList
			, HttpServletResponse response);

	public void recognitionType(ArrayList<WacaiAccountVo> accountVos);
}
