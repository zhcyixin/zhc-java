package com.zhc.service.hole.repeat.wrong;


import org.springframework.stereotype.Service;

/**
 * 小学生参加写作文比赛
 */
@Service
public class SmallStudentService {
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
    public void writingEssay(){
        System.out.println("编写小学生作文");
    }

    /**
     * 交卷
     */
    public void submitPaper(){
        System.out.println("提交试卷");
    }
}
