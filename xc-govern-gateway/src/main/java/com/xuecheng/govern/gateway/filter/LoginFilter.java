package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginFilter extends ZuulFilter {
    @Autowired
    AuthService authService;

    //过滤器类型
    @Override
    public String filterType() {
        /*
        * pre：请求在被路由之前 执行
        * routing：在路由请求时调用
        * post：在routing和errror过滤器之后调用
        * error：处理请求时发生错误调用
        */
        return "pre";
    }
    //过滤器序号，越小越优先执行
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //true表示执行此过滤器
        return true;
    }
    //过滤器内容 过虑所有请求，判断头部信息是否有Authorization，如果没有则拒绝访问，否则转发到微服务。
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = requestContext.getRequest();
        //获取response
        HttpServletResponse response = requestContext.getResponse();
        //获取cookie中的身份令牌
        String tokenFromCookie = authService.getTokenFromCookie(request);
        if (StringUtils.isEmpty(tokenFromCookie)){
            //拒绝访问
            access_denied();
            return null;
        }

        //从header中获取jwt
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwtFromHeader)){
            access_denied();
            return null;
        }
        //从redia中获取过期时间
        long expire = authService.getExpire(tokenFromCookie);
        if (expire<0){
            access_denied();
            return null;
        }
        return null;

    }


    //拒绝访问
    private void access_denied(){
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        //拒绝访问
        requestContext.setSendZuulResponse(false);
        //设置响应代码
        requestContext.setResponseStatusCode(200);
        //构建相应信息
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        //转成json
        String jsonString = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(jsonString);
        //设置contentType
        response.setContentType("application/json;charset=utf-8");
    }
}
