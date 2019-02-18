package cn.test.service;

import java.util.List;

import cn.test.po.User;

public interface UserService {
	public User query(Integer id);
	public void insert(User user);
	public List<User> queryPage(Integer pageSize, Integer pageNo);
}
