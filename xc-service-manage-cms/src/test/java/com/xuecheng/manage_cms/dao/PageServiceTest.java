package com.xuecheng.manage_cms.dao;

import com.xuecheng.manage_cms.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {

    @Autowired
    PageService pageService;


    @Test
    public void testGetPageHtml(){
        String pageHtml = pageService.getPageHtml("611d1fae70e4d57e3d6569e6");//611cce833b1f980f0c8e5cd0
        System.out.println(pageHtml);
    }

}
