package com.axon.mybaits.dao;


import com.axon.mybatis.anntations.Insert;
import com.axon.mybatis.anntations.Select;
import com.axon.mybatis.anntations.Update;
import com.axon.mybatis.enties.UserDO;

import java.util.List;

/**
 * 基于sql注解的隐射
 */
public interface IUserDao {

    @Select("SELECT id, name FROM user where id = #{id}")
    UserDO queryUserInfoById(Long id);

    @Select("SELECT id, name FROM user where id = #{id} and name=#{name}")
    UserDO queryUserInfo(UserDO req);

    @Select("SELECT id,name FROM user")
    List<UserDO> queryUserInfoList();

    @Update("UPDATE user SET name = #{name} WHERE id = #{id}")
    int updateUserInfo(UserDO req);

    @Insert("INSERT INTO user (name) VALUES (#{name})")
    void insertUserInfo(UserDO req);

    @Insert("DELETE FROM user WHERE id = #{id}")
    int deleteUserInfoByUserId(String id);


}
