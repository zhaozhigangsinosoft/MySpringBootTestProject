package cn.ribao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.ribao.service.RiBaoService;

@RestController
@RequestMapping("/ribao")
public class RiBaoController {
	
	@Autowired
	RiBaoService riBaoService;
	
	@RequestMapping(value = "/read")
	public String readExcel() {
		return riBaoService.readExcel();
	}
}