package cn.test.controller;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.test.po.User;
import cn.test.service.UserService;

@RestController
@RequestMapping("/test")
public class HelloWorldController {
    @Value("${server.name}")
    private String serverName;
    @Value("${params.model}")
    private String model;
    @Resource
    private UserService userService;
    
    @RequestMapping(value = "/printName")
    public String test() {
        return "My name is "+ serverName +" , run model is "+model +".";
    }
    @RequestMapping(value = "/query/{id}")
    public User query(@PathVariable("id") Integer id) {
        return userService.query(id);
    }
    
    @RequestMapping(value = "/queryPage/{pageSize}/{pageNo}")
    public List<User> queryPage(@PathVariable("pageSize") Integer pageSize,
            @PathVariable("pageNo") Integer pageNo) {
        return userService.queryPage(pageSize ,pageNo);
    }
    
    @RequestMapping(value = "/randomInsert")
    public void randomInsert() {
        DecimalFormat df = new DecimalFormat("00000");
        for (int i = 0; i < 900; i++) {
            User user = new User();
            String index = df.format(Math.random()*100000);
            user.setUserName("RUser"+index);
            user.setPassword("PW"+index);
             userService.insert(user);
        }
    }
    
    @RequestMapping(value = "/insert/{name}/{password}")
    public void insert(@PathVariable("name") String name,
                @PathVariable("password") String password) {
        User user = new User();
        user.setUserName(name);
        user.setPassword(password);
        userService.insert(user);
    }
}
