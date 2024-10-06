package com.axon.mybatis.dao;

import com.axon.mybatis.enties.ActivityDO;

public interface IActivityDao {

    ActivityDO queryActivityById(Long activityId);

}
