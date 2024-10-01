package com.axon.mybaits.dao;

import com.axon.mybaits.enties.UserDO;

import java.util.List;

public interface IUserDao {

    /**
     * 根据id 查询用户对象
     *
     * @param id
     * @return
     */
    String queryUserInfoById(Integer id);


    /**
     * 通过用户名称查询
     *
     * @param userName
     * @return
     */
    String queryUserName(String userName);

    /**
     * 查询用户列表信息
     *
     * @param user
     * @return
     */
    List<UserDO> queryUserList(UserDO user);
}
