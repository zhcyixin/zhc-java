package com.zhc.controller.spring.multipledata;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 相信很多小伙伴在工作中都遇到过多数据源应用场景，一般多数据源需求包含以下三种情况：
 * 1、项目业务需求需要连接其他业务数据库，例如订单数据库、商品数据库；
 * 2、项目采用读写分离技术架构，从库读和主库写场景；
 * 3、项目数据量较大，采用分库设计。
 * 今天主要分享两种多数据源实现方式：
 * 1、基于Spring dynamic-datasource-spring-boot-starter实现；
 * 2、多数据源属性配置，通过属性的依赖注入
 * Spring对多数据源提供很便利的支持，只需要包含以下几步：
 * 1、引入多数据源starter依赖
 *         <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
 *         </dependency>
 * 2、在yaml配置文件或者Apollo配置中心配置对应数据源配置信息；
 * 3、在对应需要引入不同数据源的地方，通过@DS注解引入指定数据源，不使用注解的使用默认数据源，
 * @DS注解可以作用在类上、也可以作用在方法上，遵循就近原则，方法上优先级高于类上
 *
 * @author zhouhengchao
 * @since 2023-09-20 20:02:00
 */
@RestController
@RequestMapping("/v1/zhc/java/spring/multipleDataSource")
@Slf4j
public class MultipleDataSourceController {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/useMaster")
    public void useMaster(){
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList("select * from student");
        if(CollectionUtils.isEmpty(resultList)){
            log.info("暂无数据查询结果。");
        }
        resultList.forEach(item -> {
            log.info("查询数据为:{}",item.toString());
        });
    }

    @GetMapping("/useSlave")
    @DS("slave")
    public void useSlave(){
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList("select * from test_slave");
        if(CollectionUtils.isEmpty(resultList)){
            log.info("暂无数据查询结果。");
        }
        resultList.forEach(item -> {
            log.info("查询数据为:{}",item.toString());
        });
    }

}
