package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    XcUserRepository xcUserRepository;

    @Autowired
    XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    XcMenuMapper xcMenuMapper;

    //根据账号查询xcUser信息
    public XcUser findXcUserByUsername(String username){
        return xcUserRepository.findByUsername(username);
    }

    //根据账号查询用户信息
    public XcUserExt getUserExt(String username){
        //根据用户账号查询xcUser信息
        XcUser xcUser = this.findXcUserByUsername(username);

        if (xcUser == null){
            return null;
        }
        //根据用户id查询用户所属公司id
        String userId = xcUser.getId();
        //查询用户的所有权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        //得到用户所属公司
        String companyId = null;

        if (xcCompanyUser == null){
            companyId = xcCompanyUser.getCompanyId();

        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);

        //设置权限
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;

    }

}
