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
    UserDO queryUserInfoById(Integer id);

    /**
     * 查询用户列表信息
     *
     * @param user
     * @return
     */
    List<UserDO> queryUserList(UserDO user);
}
