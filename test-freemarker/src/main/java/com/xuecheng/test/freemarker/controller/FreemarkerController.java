package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RequestMapping("/freemarker")
//@RestController //相当于@ResponseBody ＋ @Controller
@Controller//
public class FreemarkerController {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/course")
    public String course(Map<String,Object> map){
        //使用restTemplate请求轮播图的模型数据

        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31200/course/courseview/4028e581617f945f01617f9dabc40000", Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "course";
    }

    @RequestMapping("/banner")
    public String index_banner(Map<String,Object> map){
        //使用restTemplate请求轮播图的模型数据

        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "index_banner";
    }
    //测试1
    @RequestMapping("/test1")
    public String test1(Map<String,Object> map){
        //map为freemarker模板所使用的数据
        map.put("name","DL");

        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());

        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setAge(19);
        stu2.setMoney(2000.86f);
        stu2.setBirthday(new Date());


        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);

        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        map.put("stus",stus);

        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        map.put("stu1",stu1);
        map.put("stuMap",stuMap);
        //返回freemarker模板的位置，基于resources/templates路径的
        return "test1";
    }

}
