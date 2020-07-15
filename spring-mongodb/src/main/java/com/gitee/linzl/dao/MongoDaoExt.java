package com.gitee.linzl.dao;

import com.gitee.linzl.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author linzhenlie
 * @date 2019/10/10
 */
@Service
public class MongoDaoExt extends BaseMongoDao<UserEntity, Long> {

}
