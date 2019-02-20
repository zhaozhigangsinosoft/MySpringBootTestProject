package cn.wacai.service;

import java.util.ArrayList;

import cn.wacai.vo.WacaiAccountVo;

public interface AlipayService {
	public ArrayList<WacaiAccountVo> convertExcel(String filePath);
}
