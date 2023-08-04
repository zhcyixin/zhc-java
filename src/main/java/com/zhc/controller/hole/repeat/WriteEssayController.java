package com.zhc.controller.hole.repeat;

import com.google.common.collect.Lists;
import com.zhc.exception.BusinessException;
import com.zhc.service.hole.repeat.right.AbstractStudentService;
import com.zhc.service.hole.repeat.wrong.HighStudentService;
import com.zhc.service.hole.repeat.wrong.MiddleStudentService;
import com.zhc.service.hole.repeat.wrong.SmallStudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * 朋友们大家好，今天继续给大家分享Java开发踩坑系列的场景案例，可能很多小伙伴会抱怨工作中都是写一些业务代码，天天都是CRUD，
 * 用不到设计模式、Java 高级特性、OOP，没什么技术含量也得不好个人成长和能力的提升。其实不是这样的。
 * 相信很多小伙伴工作中都会编写一些重复代码，然而重复代码会造成改一处忘记另一处出现bug，影响代码维护；
 *
 * 今天给大家分享一下使用工厂模式+模板方法，消除if else和重复代码
 * 需求描述：有三类学生，小学生、初中生、高中生参加语文作文比赛，都要经过进入考场、安检、写作文、交卷四个环节，
 * 传统的设计方法：设计三个service类分别完成逻辑
 *
 * @author zhouhengchao
 * @since 2023-08-04 16:18:00
 */
@Slf4j
@RestController
@RequestMapping("/v1/zhc/java/hole/repeat")
public class WriteEssayController {

    @Resource
    private SmallStudentService smallStudentService;

    @Resource
    private MiddleStudentService middleStudentService;

    @Resource
    private HighStudentService highStudentService;

    /**
     * 我们来分析这一段代码，三类学生的进入考场、安检、交卷动作都是重复的代码，不易维护，而且如果再添加一种类型的学生就需要再添加if else分支
     * 违背设计模式的开闭原则，对修改关闭、对扩展开放；
     *
     * 因此我们可以利用方法，将相同逻辑放到抽象类中，不同的逻辑在抽象类通过抽象方法定义，由子类自己实现
     *
     * @param studentType
     * @return
     */
    @GetMapping("/wrong")
    public String wrong(@RequestParam String studentType){
        if("smallStudent".equals(studentType)){
            smallStudentService.intoExaminationRoom();
            smallStudentService.securityCheck();
            smallStudentService.writingEssay();
            smallStudentService.submitPaper();
        } else if("middleStudent".equals(studentType)){
            middleStudentService.intoExaminationRoom();
            middleStudentService.securityCheck();
            middleStudentService.writingEssay();
            middleStudentService.submitPaper();
        } else if("highStudent".equals(studentType)){
            highStudentService.intoExaminationRoom();
            highStudentService.securityCheck();
            highStudentService.writingEssay();
            highStudentService.submitPaper();
        } else{
            throw new BusinessException("请传入正确的学生类型。");
        }
        return "success";
    }

    @Resource
    private ApplicationContext applicationContext;

    /**
     *
     * 注意到在对三个子类的@Service 注解中对 Bean 进行了命名，命名就是传入的studentType值，smallStudent、middleStudent、highStudent
     * 利用 Spring 的 IoC 容器，通过 Bean 的名称直接获取到对应AbstractStudentService，
     * 1、利用工厂模式消除了太多的if else分支；
     * 2、后续如果新增加学生类型只需要添加新的实现类，符合设计模式的开闭原则
     *
     * @param studentType
     * @return
     */
    @GetMapping("/right")
    public String right(@RequestParam String studentType){
        List<String> studentTypeList = Lists.newArrayList("smallStudent","middleStudent","highStudent");
        if(!studentTypeList.contains(studentType)){
            throw new BusinessException("请传入正确的学生类型。");
        }
        AbstractStudentService studentService = (AbstractStudentService) applicationContext.getBean(studentType);
        studentService.intoExaminationRoom();
        studentService.securityCheck();
        studentService.writingEssay();
        studentService.submitPaper();
        return "success";
    }

    /**
     * 总结：
     * 1、重复代码容易造成代码出现bug，并且难以维护，可以通过模板方法+工厂模式消除重复代码；
     * 2、实际中还可以利用自定义注解加拦截器或者反射，来消除重复代码，实现解耦；
     * 3、善于使用对象拷贝工具，如BeanUtils可以减少对象间赋值的大量重复代码。
     */
}
