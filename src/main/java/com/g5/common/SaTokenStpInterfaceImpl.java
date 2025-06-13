package com.g5.common;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.g5.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SaTokenStpInterfaceImpl implements StpInterface {

    @Autowired
    private IUserService userService; //查询用户信息


    @Override
    public List<String> getPermissionList(Object o, String s) {
        //不需要使用权限点校验，返回空列表
        return null;
    }

    /**
     * 获取用户角色列表
     * @param loginId
     * @param loginType
     * @return
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        //先看看缓存中有没有，没有的话就查询数据库
        Object roles = StpUtil.getTokenSession().get("ROLE_LIST");
        if (roles != null) {
            return (List<String>) roles;
        }

        //查询数据库
        Byte role = userService.getById(Integer.parseInt((String) loginId)).getRole();
        List<String > rolesList =new ArrayList<>();
        rolesList.add(String.valueOf(role));

        //添加到缓存
        StpUtil.getTokenSession().set("RoLE_LIST",rolesList);

        return rolesList;
    }
}
