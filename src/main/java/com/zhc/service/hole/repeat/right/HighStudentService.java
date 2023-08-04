package com.zhc.service.hole.repeat.right;


import org.springframework.stereotype.Service;

/**
 * 高学生参加写作文比赛
 */
@Service(value = "highStudent")
public class HighStudentService extends AbstractStudentService{

    /**
     * 写作文
     */
    @Override
    public void writingEssay(){
        System.out.println("编写高中学生作文");
    }


}
