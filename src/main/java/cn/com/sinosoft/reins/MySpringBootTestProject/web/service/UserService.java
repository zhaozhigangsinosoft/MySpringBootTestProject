package cn.com.sinosoft.reins.MySpringBootTestProject.web.service;

import java.util.List;

import cn.com.sinosoft.reins.MySpringBootTestProject.web.po.User;

public interface UserService {
	public User query(Integer id);
	public void insert(User user);
	public List<User> queryPage(Integer pageSize, Integer pageNo);
}
