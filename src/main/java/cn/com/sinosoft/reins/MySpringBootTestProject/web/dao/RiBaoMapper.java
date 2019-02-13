package cn.com.sinosoft.reins.MySpringBootTestProject.web.dao;

import java.util.List;

import cn.com.sinosoft.reins.MySpringBootTestProject.web.po.RiBao;

public interface RiBaoMapper {
    int insert(RiBao record);

    int insertSelective(RiBao record);

	void deleteAll();

	void insertAll(List<RiBao> riBaoList);
}