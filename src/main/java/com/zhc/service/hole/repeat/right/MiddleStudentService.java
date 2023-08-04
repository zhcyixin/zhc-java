package com.zhc.service.hole.repeat.right;


import org.springframework.stereotype.Service;

/**
 * 中学生参加写作文比赛
 */
@Service(value = "middleStudent")
public class MiddleStudentService extends AbstractStudentService{

    /**
     * 写作文
     */
    @Override
    public void writingEssay(){
        System.out.println("编写中学生作文");
    }
}
