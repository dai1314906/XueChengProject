package com.xuecheng.manage_course.dao;

import org.hibernate.mapping.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testRibbon(){
        //确当获取的服务名称
        String serviceId  = "XC-SERVICE-MANAGE-CMS";
        //ribbon客户端从eureka中获取服务列表,根据服务名字获取服务列表
        for (int i = 0; i < 10; i++) {
            ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://"+serviceId+"/cms/page/get/5a754adf6abb500ad05688d9", Map.class);
            Map body = forEntity.getBody();
            System.out.println(body);
        }

    }
}
