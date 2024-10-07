package com.axon.mybatis.dao;


import com.axon.mybatis.anntations.Insert;
import com.axon.mybatis.anntations.Select;
import com.axon.mybatis.anntations.Update;
import com.axon.mybatis.enties.UserDO;

import java.util.List;

/**
 * 基于sql注解的隐射
 */
public interface IUserDao {

    UserDO queryUserInfoById(Long id);

}
