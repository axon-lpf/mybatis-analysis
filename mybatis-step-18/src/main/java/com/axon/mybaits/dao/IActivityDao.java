package com.axon.mybaits.dao;

import com.axon.mybatis.enties.ActivityDO;

public interface IActivityDao {

    ActivityDO queryActivityById(ActivityDO activityDO);


    Integer insert(ActivityDO activity);

}
