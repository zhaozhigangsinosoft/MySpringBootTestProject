package cn.wacai.service;

import java.util.ArrayList;

import cn.wacai.vo.WacaiAccountVo;

public interface WeixinService {
	public ArrayList<WacaiAccountVo> convertExcel(String filePath);
}
