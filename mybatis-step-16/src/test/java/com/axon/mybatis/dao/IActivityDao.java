package com.axon.mybatis.dao;

import com.axon.mybatis.enties.ActivityDO;

public interface IActivityDao {

    ActivityDO queryActivityById(ActivityDO activityDO);


    Integer insert(ActivityDO activity);

}
