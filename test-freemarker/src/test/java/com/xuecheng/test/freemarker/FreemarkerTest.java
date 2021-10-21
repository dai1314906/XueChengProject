package com.xuecheng.test.freemarker;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    //测试静态化，基于ftl模板文件生成html
    @Test
    public void testGenerateHtml() throws IOException, TemplateException {
        //定义配置类
        //使用freemarker下的configuration

        Configuration configuration = new Configuration(Configuration.getVersion());
        //定义模板
        //得到classpath路径
        String classPath = this.getClass().getResource("/").getPath();
        //定义模板路径
        configuration.setDirectoryForTemplateLoading(new File(classPath+"/templates/"));
        //获取模板文件内容
        Template template = configuration.getTemplate("test1.ftl");

        //定义数据模型
        Map map = getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//        System.out.println(content);
        //使用apache的工具包
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream outputStream = new FileOutputStream(new File("D:/test1.html"));
        //输出文件
        IOUtils.copy(inputStream,outputStream);
        inputStream.close();
        outputStream.close();
    }

    @Test
    public void testGenerateHtmlByString() throws IOException, TemplateException{
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());

        //定义模板
        //模板内容（字符串）
        //模板内容，这里测试时使用简单的字符串作为模板
        String templateString=""+
                "<html>\n" +
                "   <head></head>\n" +
                "   <body>\n" +
                "   名称：${name}\n" +
                "   </body>\n" +
                "</html>";
        //模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateString);
        configuration.setTemplateLoader(stringTemplateLoader);
        //在配置中设置模板加载器
        Template template = configuration.getTemplate("template","utf‐8");
        //获取模板的内容
        //定义数据模型
        Map map = getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//        System.out.println(content);
        //使用apache的工具包
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream outputStream = new FileOutputStream(new File("D:/test2.html"));
        //输出文件
        IOUtils.copy(inputStream,outputStream);
        inputStream.close();
        outputStream.close();
    }

    //获取数据模型
    public Map getMap(){
        Map map = new HashMap<>();

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
        map.put("point",2021917233);
        return map;
    }
}
