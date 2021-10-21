package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class FeignClientInterceptor implements RequestInterceptor {
    //
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes!=null){
            HttpServletRequest request = requestAttributes.getRequest();
            //取出档当前请求的header，找到jwt令牌
            //获取所有headers
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames!=null){
                while (headerNames.hasMoreElements()){
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    //传jwt令牌
                    requestTemplate.header(headerName,headerValue);
                }
            }
        }
        //将jwt令牌向下传递
    }
}
