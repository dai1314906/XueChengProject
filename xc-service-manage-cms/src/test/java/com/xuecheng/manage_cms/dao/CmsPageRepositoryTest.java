package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;


    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }



    //分页查询
    @Test
    public void testFindPage(){
        //分页查询的参数
        int page = 2;//页面数从0开始
        int size = 10;//查询条数
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    @Test
    public void testFindAllByExample(){
        //分页查询的参数
        int page = 0;//页面数从0开始
        int size = 10;//查询条数
        Pageable pageable = PageRequest.of(page,size);
        //of的第一个值关于查询条件对象
        CmsPage cmsPage = new CmsPage();
/*        //查询站点id
        //要查询5a751fab6abb5044e0d19ea1站点的页面
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        //查询模板id
        cmsPage.setTemplateId("5a925be7b00ffc4b3c1578b5");*/

        //以上操作就可以实现两个条件的查询


        //设置页面别名查询
        cmsPage.setPageAliase("轮播");///这个时候无法进行模糊匹配的要进行条件匹配

        //这时候需要第二个参数条件匹配器
        //ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //新定义查询关于模糊匹配ExampleMatcher.GenericPropertyMatchers.contains()（包含关键字）ExampleMatcher.GenericPropertyMatchers.exact();//精准匹配 ExampleMatcher.GenericPropertyMatchers.startsWith();//前缀匹配



        //exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        //定义example 传入cmspage参数和exampleMatcher参数
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        //取出文件并且打印
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }


    @Test
    public void update(){
        //找到要修改的对象
        Optional<CmsPage> optional = cmsPageRepository.findById("5abefd525b05aa293098fca6");
        //判断是否为空
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            //设置修改的值
            cmsPage.setPageAliase("CCCC");
            //修改
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);

        }
    }


}
