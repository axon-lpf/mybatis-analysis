package com.axon.mybatis.dao;


import com.axon.mybatis.enties.UserDO;

import java.util.List;

public interface IUserDao {

    /**
     * 根据id 查询用户对象
     *
     * @param id
     * @return
     */
    UserDO queryUserInfoById(Long id);


    /**
     * 多个参数获取
     */
    UserDO queryUserInfo(UserDO user);

    /**
     * 查询用户列表信息
     *
     * @param user
     * @return
     */
    List<UserDO> queryUserList(UserDO user);


    List<UserDO> queryUserInfoList();

    int updateUserInfo(UserDO req);

    void insertUserInfo(UserDO req);

    int deleteUserInfoByUserId(String userId);


}
