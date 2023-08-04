package com.zhc.service.hole.repeat.right;


import org.springframework.stereotype.Service;

/**
 * 小学生参加写作文比赛
 */
@Service(value = "smallStudent")
public class SmallStudentService extends AbstractStudentService{

    /**
     * 写作文
     */
    @Override
    public void writingEssay(){
        System.out.println("编写小学生作文");
    }
}
