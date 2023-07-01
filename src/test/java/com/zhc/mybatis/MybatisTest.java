package com.zhc.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhc.dao.StudentMapper;
import com.zhc.model.request.OtherPageRequest;
import com.zhc.model.request.PageRequest;
import com.zhc.model.vo.StudentCourseVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * Mybatis-pageHelper分页插件使用中的坑
 * 1、自定义编写分页中的countSql语句，优化性能；
 * 2、未使用PageHelper却出现了分页效果原因分析；
 * 3、自定义实现分页
 * @author zhouhengchao
 * @since 2023-06-30 18:00:00
 * @version 1.0
 */

@SpringBootTest
public class MybatisTest {

    @Resource
    private StudentMapper studentMapper;

    /**
     * 使用默认count查询总数；
     * 场景分析：SELECT count(0) FROM (SELECT a.id, a.name, a.stu_no AS stuNo, a.age, b.course_name AS courseName,
     * b.course_type AS courseType, b.already_hours AS alreadyHours
     * FROM student a INNER JOIN course b ON a.id = b.student_id AND a.id IN (1, 2, 3) GROUP BY a.id) table_count
     * 发现分页插件自己生成的统计总数sql，多层嵌套，实际中只需要知道总数，可以直接改为count(a.id)总数进行优化；
     * 线上环境通常多个业务表进行关联查询，有时候pageHelper countSql会很耗时，影响接口性能
     */
    @Test
    void testPageHelp(){
        PageHelper.startPage(1, 10);
        List<StudentCourseVo> students = studentMapper.selectStudentList();
        PageInfo<StudentCourseVo> pageInfo = new PageInfo<>(students);
        System.out.println(pageInfo);
    }

    /**
     * 使用自定义count查询总数sql，对sql进行优化，提升性能
     * 1、把原来的SQL语句copy一份；
     * 2、在mapper层，在原来sqlId的结尾加上_COUNT
     * 3、可以对countSql进行优化，统计条数时，直接统计id数，减少回表
     */
    @Test
    void testPageHelp1(){
        PageHelper.startPage(1, 10);
        List<StudentCourseVo> students = studentMapper.selectStudentList1();
        PageInfo<StudentCourseVo> pageInfo = new PageInfo<>(students);
        System.out.println(pageInfo);
    }

    /**
     * 不使用PageHelper分页，但是sql中打印出了count总数语句
     * 原因分析：传入的参数pageNum和pageSize参数与PageHelper.startPage方法的参数相同，会导致自动触发PageHelper分页效果；
     * PageHelper.startPage 方法调用后，后面必须有一个Mapper的查询方法，必须被消费掉。否则会由于ThreadLocal的原因，当该线程被其他方法调用时被分页。
     */
    @Test
    void testPageHelp2(){
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);
        List<StudentCourseVo> students = studentMapper.selectStudentList2(pageRequest);
        System.out.println(students);
    }

    /**
     * Sql查询参数中，传入对象参数名将pageNum和pageSize参数名改为pageNu和pageSiz
     * 执行发现，打印出来的sql不包含count总数语句
     */
    @Test
    void testPageHelp3(){
        OtherPageRequest otherPageRequest = new OtherPageRequest();
        otherPageRequest.setPageNu(1);
        otherPageRequest.setPageSiz(10);
        List<StudentCourseVo> students = studentMapper.selectStudentList3(otherPageRequest);
        System.out.println(students);
    }

    /**
     * 通过自己手动实现分页，不使用自带count功能，提升性能
     * 执行发现，打印出来的sql不包含count总数语句
     */
    @Test
    void testPageHelp4(){
        OtherPageRequest otherPageRequest = new OtherPageRequest();
        otherPageRequest.setPageNu(1);
        otherPageRequest.setPageSiz(10);
        otherPageRequest.setIndex(otherPageRequest.getPageNu() == 1 ? 0 : (otherPageRequest.getPageNu() - 1)
                * otherPageRequest.getPageSiz());
        List<StudentCourseVo> students = studentMapper.selectStudentList4(otherPageRequest);
        PageInfo<StudentCourseVo> pageInfo = new PageInfo<>(students);

        System.out.println(pageInfo);
    }
}
