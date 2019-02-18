package cn.test.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

import cn.test.dao.UserMapper;
import cn.test.po.User;
import cn.test.service.UserService;
import cn.util.PageBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private UserMapper userMapper; 
	
	public User query(Integer id) {
		return userMapper.queryById(id);
	}

	/**
	 * 插入用户方法
	 */
	@Override
	public void insert(User user) {
		userMapper.insert(user);
	}

	@Override
	public List<User> queryPage(Integer pageSize, Integer pageNo) {
		logger.info("分页查询方法，"+pageNo+"第页，每页"+pageSize+"条");
		PageHelper.startPage(pageNo, pageSize);
		PageHelper.orderBy("id desc");
		List<User> allItems = userMapper.queryAll();
		int count = userMapper.countAll();
		PageBean<User> pageData = new PageBean<>(pageNo, pageSize, count);
	    pageData.setItems(allItems);
	    return pageData.getItems();
	}

}
