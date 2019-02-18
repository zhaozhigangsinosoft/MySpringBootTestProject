package cn.test.dao;

import java.util.List;

import cn.test.po.User;

public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);
    
    public User queryById(Integer id);

    public List<User> queryPage(Integer startIndex, Integer endIndex);

    public List<User> queryAll();

	public int countAll();
}