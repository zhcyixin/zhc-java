package com.zhc.service.hole.repeat.right;


/**
 * @author zhouhengchao
 * @since 2023-08-04 16:41:00
 * 抽象类-学生参加写作文比赛，将相同逻辑定义在父类中
 */
public abstract class AbstractStudentService {
    /**
     * 进入考场
     */
    public void intoExaminationRoom(){
        System.out.println("进入考场");
    }
    /**
     * 安检
     */
    public void securityCheck(){
        System.out.println("进行安检");
    }

    /**
     * 写作文
     */
    public abstract void writingEssay();

    /**
     * 交卷
     */
    public void submitPaper(){
        System.out.println("提交试卷");
    }
}
