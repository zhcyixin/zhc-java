package com.zhc.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhc.model.entity.JsonUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 1、日常开发中，可能大家会涉及到Json的一些操作，推荐使用Jackson开发包；
 * 2、推荐理由：最小依赖，Spring mvc默认采用jackson解析；社区活跃、更新速度快；
 * 解析大json速度快、性能不错；灵活的API、易于扩展。
 * 3、今天主要给大家分享JackSon的几个常见使用技巧
 * 首先引入依赖：
 * 两种方式：一种是直接引入spring-boot-starter-web内嵌了jackson依赖，一种是引入jackson依赖
 * 核心对象介绍：ObjectMapper 是 Jackson 库中最常用的一个类，使用它可以进行 Java 对象和 JSON 字符串之间快速转换。
 * 如果你用过 FastJson，那么 Jackson 中的 ObjectMapper就如同 FastJson 中的 JSON 类。
 * @author zhouhengchao
 * @since 2023-12-01 10:35:00
 * @version 1.0
 */
public class JacksonTest {

    @Autowired
    private ResourceLoader resourceLoader;
    /**
     * 1、JSON字符串反序列化为Java对象，序列化为字符串
     * 使用ObjectMapper的readValue方法进行反序列化,ObjectMapper的writeValueAsString方法进行序列化
     */
    @Test
    void testJackson01(){
        String json = "{\"userName\":\"swx\",\"gender\":\"男\",\"birthDay\":\"2000-12-01 11:00:12\",\"mobile\":\"13551278901\",\"userEmail\":\"hello@qq.com\",\"age\":18,\"skillsList\":[\"java\",\"python\",\"php\"]}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonUser jsonUser = null;
        try {
            // 对Json字符串进行反序列化为Java对象
            jsonUser = objectMapper.readValue(json, JsonUser.class);
            System.out.println("反序列化之后结果为:"+jsonUser);

            // 也可以使用Reader对象来装载Json
            Reader reader = new StringReader(json);
            jsonUser = objectMapper.readValue(reader, JsonUser.class);

            // 对Java对象进行序列化为Json字符串
            String resultJson = objectMapper.writeValueAsString(jsonUser);
            System.out.println("序列化之后结果为:"+resultJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 2、JSON数组字符串反序列化为Java对象数组或者List
     * 使用ObjectMapper的readValue方法进行反序列化,
     * 如果json中传入对象不存在的字段需要忽略，可以设置
     */
    @Test
    void testJackson02(){
        String jsonArr = "[{\"userName\":\"swx\",\"userName1\":\"swx\",\"gender\":\"男\",\"birthDay\":\"2000-12-01 11:00:12\",\"mobile\":\"13551278901\",\"userEmail\":\"hello@qq.com\",\"age\":18,\"skillsList\":[\"java\",\"python\",\"php\"]}]";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonUser[] jsonUserArr = null;
        List<JsonUser> jsonUserList = null;
        try {
            // 对Json字符串数组进行反序列化为Java对象数组
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            jsonUserArr = objectMapper.readValue(jsonArr, JsonUser[].class);
            System.out.println("反序列化之后对象数组，结果为:"+jsonUserArr);

            // 对Json字符串数组进行反序列化为Java对象List
            jsonUserList = objectMapper.readValue(jsonArr, new TypeReference<List<JsonUser>>(){});
            System.out.println("反序列化之后对象List，结果为:"+jsonUserList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 3、JSON字符串转map，使用ObjectMapper的readValue方法进行反序列化
     *
     */
    @Test
    void testJackson03(){
        String json = "{\"userName\":\"swx\",\"gender\":\"男\",\"birthDay\":\"2000-12-01 11:00:12\",\"mobile\":\"13551278901\",\"userEmail\":\"hello@qq.com\",\"age\":18,\"skillsList\":[\"java\",\"python\",\"php\"]}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonUserMap = null;
        try {
            // 对Json字符串进行反序列化为Java Map
            jsonUserMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
            System.out.println("反序列化之后对象Map，结果为:"+jsonUserMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 4、Jackson可以返回JsonNode对象
     * 可以基于readTree方法,可以直接通过json的key获取属性值，如果不想定义对象的话
     */
    @Test
    void testJackson04(){
        String json = "{\"userName\":\"swx\",\"gender\":\"男\",\"birthDay\":\"2000-12-01 11:00:12\",\"mobile\":\"13551278901\",\"userEmail\":\"hello@qq.com\",\"age\":18,\"skillsList\":[\"java\",\"python\",\"php\"]}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 对Json字符串进行反序列化为Java Map
            JsonNode jsonNode = objectMapper.readTree(json);
            System.out.println("JsonNode，userName结果为:"+jsonNode.get("userName").asText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
