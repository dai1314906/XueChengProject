package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface XcMenuMapper {
    //根据id查询用户的权限
    public List<XcMenu> selectPermissionByUserId(String userid);
}
