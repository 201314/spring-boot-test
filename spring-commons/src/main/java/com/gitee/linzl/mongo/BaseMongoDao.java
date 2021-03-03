package com.gitee.linzl.mongo;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <pre>{@code
 *     @Service
 *     public class UserDao extends BaseMongoDao<UserEntity, Long>{ }
 *
 *     @Autowired
 *     UserDao dao;
 *     dao.insert(UserEntity);
 * }</pre>
 *
 *
 * @author linzhenlie
 * @date 2019/10/10
 */
@ConditionalOnBean(MongoTemplate.class)
public class BaseMongoDao<T, ID> {
    @Autowired
    private MongoTemplate mongoTemplate;
    private Class<T> entityClass;

    public BaseMongoDao() {
        if (this.entityClass == null) {
            ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.entityClass = (Class) actualTypeArguments[0];
        }
    }

    public boolean collectionExists() {
        return mongoTemplate.collectionExists(entityClass);
    }

    public boolean collectionExists(String collectionName) {
        return mongoTemplate.collectionExists(collectionName);
    }

    public void dropCollection() {
        mongoTemplate.dropCollection(entityClass);
    }

    public void dropCollection(String collectionName) {
        mongoTemplate.dropCollection(collectionName);
    }

    public List<T> findAll() {
        return mongoTemplate.findAll(entityClass);
    }

    public T findOne(Query query) {
        return mongoTemplate.findOne(query, entityClass);
    }

    public boolean exists(Query query) {
        return mongoTemplate.exists(query, entityClass);
    }

    public List<T> find(Query query) {
        return mongoTemplate.find(query, entityClass);
    }

    public List<T> find(Query query, int page, int pageSize) {
        return this.find(query, page, pageSize, null);
    }

    public List<T> find(Query query, int page, int pageSize, Sort sort) {
        if (page <= 0 || pageSize <= 0) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        query = query.with(pageable);
        if (Objects.nonNull(sort)) {
            query = query.with(sort);
        }
        return this.find(query);
    }

    public T findById(ID id) {
        return mongoTemplate.findById(id, entityClass);
    }

    public <T> List<T> findDistinct(Query query, String field, Class<T> resultClass) {
        return mongoTemplate.findDistinct(query, field, entityClass, resultClass);
    }

    public T findAndModify(Query query, Update update) {
        return mongoTemplate.findAndModify(query, update, entityClass);
    }

    public T findAndRemove(Query query) {
        return mongoTemplate.findAndRemove(query, entityClass);
    }

    public long count(Query query) {
        return mongoTemplate.count(query, entityClass);
    }

    public <T> T insert(T objectToSave) {
        return mongoTemplate.insert(objectToSave);
    }

    public <T> Collection<T> insert(Collection<? extends T> batchToSave) {
        return mongoTemplate.insert(batchToSave, entityClass);
    }

    public <T> Collection<T> insertAll(Collection<? extends T> objectsToSave) {
        return mongoTemplate.insertAll(objectsToSave);
    }

    public UpdateResult upsert(Query query, Update update) {
        return mongoTemplate.upsert(query, update, entityClass);
    }

    public UpdateResult updateById(ID id, Update update) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.updateFirst(query, update);
    }

    public UpdateResult updateFirst(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, entityClass);
    }

    public UpdateResult updateMulti(Query query, Update update) {
        return mongoTemplate.updateMulti(query, update, entityClass);
    }

    public DeleteResult remove(Query query) {
        return mongoTemplate.remove(query, entityClass);
    }

    public DeleteResult removeById(ID id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.remove(query);
    }

    public List<T> findAllAndRemove(Query query) {
        return mongoTemplate.findAllAndRemove(query, entityClass);
    }
}
