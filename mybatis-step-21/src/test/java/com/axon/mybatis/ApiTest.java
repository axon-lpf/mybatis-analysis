package com.axon.mybatis;

import com.alibaba.fastjson.JSON;
import com.axon.mybatis.dao.IUserDao;
import com.axon.mybatis.enties.UserDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 *  整合springboot 测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Resource
    private IUserDao userDao;

    @Test
    public void test_queryUserInfoById() {
        UserDO user = userDao.queryUserInfoById(1L);
        logger.info("测试结果：{}", JSON.toJSONString(user));
    }

}
