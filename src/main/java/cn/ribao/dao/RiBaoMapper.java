package cn.ribao.dao;

import java.util.List;

import cn.ribao.po.RiBao;

public interface RiBaoMapper {
    int insert(RiBao record);

    int insertSelective(RiBao record);

	void deleteAll();

	void insertAll(List<RiBao> riBaoList);
}