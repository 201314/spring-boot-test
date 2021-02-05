package com.gitee.linzl;

import com.alibaba.fastjson.JSON;
import com.gitee.linzl.dao.MongoDaoExt;
import com.gitee.linzl.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author linzhenlie
 * @date 2019/10/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BaseMongoDaoTest {
    @Autowired
    private MongoDaoExt dao;

    @Test
    public void collectionExists() {
        System.out.println("collectionExists:" + dao.collectionExists());
    }

    @Test
    public void collectionNameExists() {
        System.out.println("collectionNameExists:" + dao.collectionExists("person"));
    }

    @Test
    public void dropCollection() {
        dao.dropCollection();
    }

    @Test
    public void dropCollectionName() {
        dao.dropCollection("person");
    }

    @Test
    public void findAll() {
        System.out.println("findAll():" + JSON.toJSONString(dao.findAll()));
    }

    @Test
    public void findOne() {
        System.out.println("findOne():" + JSON.toJSONString(dao.findOne(Query.query(Criteria.where("userName").is("小明")))));
    }

    @Test
    public void exists() {
        System.out.println("exists():" + JSON.toJSONString(dao.exists(Query.query(Criteria.where("userName").is("小明")))));
    }

    @Test
    public void find() {
        System.out.println("find():" + JSON.toJSONString(dao.find(new Query())));
    }

    @Test
    public void find2() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date todayStart = calendar.getTime();
        calendar.add(Calendar.DATE, 1);

        Date endStart = calendar.getTime();
        Query query2 = new Query();

        Criteria criteria = Criteria.where("birthday").gte(todayStart).lte(endStart);
        query2.addCriteria(criteria);
        List<UserEntity> userList = dao.find(query2);
    }

    @Test
    public void page() {
        /*Sort sort = new Sort(Sort.Direction.DESC, "creatTime");
        int page = 1;
        int pageSize = 10;

        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is("小明"));

        long total = dao.count(query);
        List<UserEntity> items = dao.find(query, page, pageSize, sort);
        System.out.println("total:" + total);
        System.out.println("items:" + items);*/
    }

    @Test
    public void findById() {
        System.out.println("findById():" + dao.findById(2L));
    }

    @Test
    public void count() {
        System.out.println("count():" + dao.count((new Query())));
    }

    @Test
    public void insert() {
        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setUserName("小明10");
        user.setPassWord("fffooo123-10");
        user.setBirthday(new Date());
        dao.insert(user);
    }

    @Test
    public void insertList() {
        List<UserEntity> list = new ArrayList<>();
        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setUserName("小明");
        user.setPassWord("fffooo123");
        user.setBirthday(new Date());
        list.add(user);

        user = new UserEntity();
        user.setId(22l);
        user.setUserName("小黄");
        user.setPassWord("ofo");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, -1);
        user.setBirthday(cal.getTime());
        list.add(user);

        dao.insertAll(list);
    }


    @Test
    public void updateById() {
        UserEntity user = new UserEntity();
        user.setId(2l);
        user.setUserName("天空");
        user.setPassWord("fffxxxx");

        Update update = new Update();
        update.set("userName", user.getUserName());
        update.set("passWord", user.getPassWord());
        dao.updateById(user.getId(), update);
        // 更新查询返回结果集的第一条
        // dao.updateFirst(query, update);
        // 更新查询返回结果集的所有
        // dao.updateMulti(query,update);
    }

    @Test
    public void delete() {
        dao.removeById(1l);
    }

}
