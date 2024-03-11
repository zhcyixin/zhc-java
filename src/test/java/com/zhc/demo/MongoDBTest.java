package com.zhc.demo;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zhc.model.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.util.List;

/**
 * 1、日常开发中，可能大家会使用到MongoDB数据库；
 * 2、工作中一般都是在
 *
 * @author zhouhengchao
 * @since 2024-03-01 10:35:00
 * @version 1.0
 */
@SpringBootTest
@Slf4j
public class MongoDBTest {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 初始化Collection数据
     */
    @Test
    void initCollection(){
        log.info("初始化collection数据-开始");
        Person person1 = new Person(1L,"小王","123456","男",18,"重庆");
        Person person2 = new Person(2L,"小丽","23456","女",20,"吕梁");
        Person person3 = new Person(3L,"小军","34567","男",30,"大同");
        Person person4 = new Person(4L,"小红","45678","女",19,"晋中");
        Person person5 = new Person(5L,"小花","13421","女",28,"太原");
        List<Person> personList = Lists.newArrayList(person1,person2, person3, person4, person5);
        mongoTemplate.insertAll(personList);
        log.info("初始化collection数据-结束");
    }
    /**
     * 插入一条数据
     */
    @Test
    void insertOneData(){
        // 插入一条数据，使用insert ,id如果不指定，会自动生成一个随机值
        Person person = new Person(6L,"小花花","134121","女",28,"孝义");
        mongoTemplate.insert(person);
        // 插入多条数据，使用insertAll，传入集合
        Person person1 = new Person(7L,"小花花1","134121","女",28,"重庆");
        Person person2 = new Person(8L,"小花花2","134121","女",28,"济南");
        Person person3 = new Person(9L,"小花花3","134121","女",28,"苏州");
        List<Person> personList = Lists.newArrayList(person1, person2, person3);
        mongoTemplate.insertAll(personList);
    }

    /**
     * 编辑id为6的数据
     */
    @Test
    void updateData(){
        Person personByIdBefore = mongoTemplate.findById(6L, Person.class);
        log.info("修改前：personById:{}", JSON.toJSONString(personByIdBefore));

        Update update = new Update();
        update.set("userName","小花儿");
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(6L));
        mongoTemplate.updateFirst(query, update, Person.class);

        // 按照id查询
        Person personByIdAfter = mongoTemplate.findById(6L, Person.class);
        log.info("修改后：personById:{}", JSON.toJSONString(personByIdAfter));
    }

    /**
     * 删除指定id的数据
     */
    @Test
    void delData(){
        Query query = new Query(Criteria.where("id").is(5L));
        // 数据库中所有满足条件的集合都会删除,如果只是删除某一条，可以在query中添加limit条件
        mongoTemplate.remove(query);
        // 指定需要删除数据的集合
        mongoTemplate.remove(query, Person.class);
    }


    /**
     * 几种查询操作介绍
     */
    @Test
    void queryData(){
        // 1、直接按照id查询
        Person personById = mongoTemplate.findById(6L, Person.class);
        log.info("personById:{}", JSON.toJSONString(personById));

        // 2、按照多条件And查询，查询28岁的女孩
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("age").is(28));
        query1.addCriteria(Criteria.where("gender").is("女"));
        List<Person> personList1 = mongoTemplate.find(query1, Person.class);
        log.info("查询28岁的女孩:{}", JSON.toJSONString(personList1));

        // 3、多条件or操作，查询28岁或者是女孩
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("age").is(28).orOperator(Criteria.where("gender").is("女")));
        List<Person> personList2 = mongoTemplate.find(query2, Person.class);
        log.info("查询28岁或者女孩:{}", JSON.toJSONString(personList2));

        // 4、分页查询，性别为女孩的
        Query query3 = new Query(Criteria.where("gender").is("女"));
        PageRequest pageable = PageRequest.of(1, 10);
        query3.with(pageable);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        query3.with(sort);
        long total = mongoTemplate.count(query3, Person.class);
        List<Person> personList3 = mongoTemplate.find(query3, Person.class);
        PageImpl<Person> pageResult = new PageImpl<>(personList3, pageable, total);
        log.info("分页查询女孩:{}", JSON.toJSONString(pageResult));
    }
}
