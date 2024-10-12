package com.axon.mybatis.dao;


import com.axon.mybatis.enties.UserDO;

import java.util.List;

/**
 * 基于sql注解的隐射
 */
public interface IUserDao {

    UserDO queryUserInfoById(Long id);

    UserDO queryUserInfo(UserDO req);

    List<UserDO> queryUserInfoList();

    int updateUserInfo(UserDO req);

    void insertUserInfo(UserDO req);

    int deleteUserInfoByUserId(String id);


}
