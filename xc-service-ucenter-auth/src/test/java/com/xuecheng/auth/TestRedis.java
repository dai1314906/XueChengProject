package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    public void testRedis(){
        //定义key
        String key = "user_token:141395ab-2ae5-4bb4-9a35-b63d5c59e821";
        //定义value
        Map<String,String> value = new HashMap<>();
        value.put("jwt","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTYzMjUxMzI1NiwianRpIjoiMTQxMzk1YWItMmFlNS00YmI0LTlhMzUtYjYzZDVjNTllODIxIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.oNizjcfsF8IHKh1Q2ecGmPECejxrq_b54Hayup9MWivPIJQ0r7ajmtcmd5dFvjDi2PRAldjz7pRwhONojyoG_cCeI0obTHNIUoLp4veSel017_OZXDbdbf8f9A7uk1uwst4C4TExxXTLoSStr9-IamyFqGjpeqTBYgFZ7e1n5Bu4_JcPGc-Ut6HuoBSeGIh4ESO28mRCE-3TeoVo0aN0UhvPtl24iyvrKbAt6iQmvtQLlx53jDK6mPMVHgiuMhkNYn2DBcKGCjgFDIHzenmcjlygoSZzkIs-Elr_QaARWf_Irt3gNwj0kcerKl7EwZDZU6-4NIXg2aFhEC0XO2jQFg");
        value.put("refresh_token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiIxNDEzOTVhYi0yYWU1LTRiYjQtOWEzNS1iNjNkNWM1OWU4MjEiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTYzMjUxMzI1NiwianRpIjoiYzE5Yzk2ZTItODk0ZC00YTlhLWEwOTgtZDgyZWM3NzU4NWUyIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.EpzNcK3rbBPYq4lDaz4lOpxJNciGLJnq9WMyQ-OewIsWlzSUId9vNvfj_p9dvoWXu7ndrHvuQTNYyZqC-CzPp8F5CJHccT55Fj2ZSWAdfK1m6KIywF6YPNO8JkA2F13gv_Mt4AGvf7Bdc1I8Lgi93fwac5Jcw50y_3dljXq4B-g-1zgwpcWm2JOEZDRw_ac84yNM4O93bF3tA5QX9ZWE01wSbNvOwphD9rQYTfb7J4KEHOBiq3eT62aY-F2uRRA3qkIcYgMsn3XcNde8PcYMcNjNb0pT74TYsgIBDnk3lsXbBi1UjWh3TDAvx0e9N9NUd_gVJAQqnu-BobOqPO7cyA");
        String jsonString = JSON.toJSONString(value);
        //校验key是否存在，不存在就返回-2
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        System.out.println(expire);
        //存储数据
        stringRedisTemplate.boundValueOps(key).set(jsonString,30, TimeUnit.SECONDS);
        //获取数据
        String s = stringRedisTemplate.opsForValue().get(key);
        System.out.println(s);
    }

}
