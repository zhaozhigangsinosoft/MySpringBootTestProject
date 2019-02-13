package cn.com.sinosoft.reins.MySpringBootTestProject.web.dao;

import java.util.List;

import cn.com.sinosoft.reins.MySpringBootTestProject.web.po.User;

public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);
    
    public User queryById(Integer id);

    public List<User> queryPage(Integer startIndex, Integer endIndex);

    public List<User> queryAll();

	public int countAll();
}