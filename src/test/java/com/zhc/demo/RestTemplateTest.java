package com.zhc.demo;

import com.zhc.model.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * RestTemplate测试类-主要介绍RestTemplate各种使用场景
 *
 * @author zhouhengchao
 * @since 2023-10-20 10:00:00
 */
@Slf4j
@SpringBootTest
public class RestTemplateTest {

    @Resource
    private RestTemplate restTemplate;

    /**
     * 调用一个Get请求，并在url中传入id参数，
     * 返回指定自定义类型Student的对象信息，可以通过getForEntity、getForObject方法
     */
    @Test
    void testGetRequest1(){
        String url = "/v1/zhc/java/spring/rest/findStudent?id={id}";
        // 通过getForEntity调用get请求，返回对象包在ResponseEntity，可以获取到响应头及响应状态信息
        // 请求参数中是可变参数也可以传多个参数
        ResponseEntity<Student> resultEntity = restTemplate.getForEntity(url, Student.class, 1);

        // 也还可以通过map类型方式传参
        Map<String,String> param = new HashMap<>();
        param.put("id","1");
        ResponseEntity<Student> resultEntity1 = restTemplate.getForEntity(url, Student.class, param);
        log.info("response resultEntity1 student:{}",resultEntity1.getBody());

        // 直接通过getForObject调用get请求，返回指定类型对象
        Student templateForObject = restTemplate.getForObject(url, Student.class, 1);
        log.info("response templateForObject:{}",templateForObject);
        Student student = resultEntity.getBody();
        log.info("response headers:{}",resultEntity.getHeaders());
        log.info("response status:{}",resultEntity.getStatusCode());
        log.info("response status value:{}",resultEntity.getStatusCodeValue());
        log.info("response body:{}",student);
        log.info("response student name :{}",student.getName());
    }

    /**
     * 调用一个Get请求，基于url中传入参数，
     * 返回指定自定义类型Student的对象信息，可以通过getForEntity、getForObject方法
     * 通过LinkedMultiValueMap和UriComponentsBuilder 方式
     */
    @Test
    void testGetRequest2(){
        String url = "http://localhost:8081/v1/zhc/java/spring/rest/findStudent";
        // 通过getForEntity调用get请求，返回对象包在ResponseEntity，可以获取到响应头及响应状态信息
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("id", "1");
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = uriComponentsBuilder.queryParams(multiValueMap).build().encode().toUri();
        ResponseEntity<Student> resultEntity = restTemplate.getForEntity(uri, Student.class);
        Student student = resultEntity.getBody();

        log.info("response headers:{}",resultEntity.getHeaders());
        log.info("response status:{}",resultEntity.getStatusCode());
        log.info("response status value:{}",resultEntity.getStatusCodeValue());
        log.info("response body:{}",student);
        log.info("response student name :{}",student.getName());
    }

    /**
     * 调用一个Get请求，基于url中传入参数，
     * 返回指定自定义类型Student的对象信息，可以通过exchange方法
     * 需要传入Header参数时
     */
    @Test
    void testGetRequest3(){
        String url = "http://localhost:8081/v1/zhc/java/spring/rest/findStudent?id={id}";
        // 通过getForEntity调用get请求，返回对象包在ResponseEntity，可以获取到响应头及响应状态信息
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("token", "myToken");
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("id","1");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

        ResponseEntity<Student> resultEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity,Student.class);
        Student student = resultEntity.getBody();

        log.info("response headers:{}",resultEntity.getHeaders());
        log.info("response status:{}",resultEntity.getStatusCode());
        log.info("response status value:{}",resultEntity.getStatusCodeValue());
        log.info("response body:{}",student);
        log.info("response student name :{}",student.getName());
    }

    /**
     * 发起post请求，使用postForObject、postForEntity方法
     */
    @Test
    void testPostRequest1(){
        String url = "http://localhost:8081/v1/zhc/java/spring/rest/postFindStudent";
        // 通过getForEntity调用get请求，返回对象包在ResponseEntity，可以获取到响应头及响应状态信息
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("token", "myToken");
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("id","1");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

        Student studentRequest = new Student(1,"hello","12345",18);

        ResponseEntity<Student> resultEntity = restTemplate.postForEntity(url,studentRequest ,Student.class);
        Student student = resultEntity.getBody();

        log.info("response headers:{}",resultEntity.getHeaders());
        log.info("response status:{}",resultEntity.getStatusCode());
        log.info("response status value:{}",resultEntity.getStatusCodeValue());
        log.info("response body:{}",student);
        log.info("response student name :{}",student.getName());
    }

    /**
     * 通过exchange方法调用post请求
     * 不添加斜杠/，根路径不生效，可以通过调式源代码分析
     */
    @Test
    void testPostRequest2(){
        String url = "v1/zhc/java/spring/rest/postFindStudent";
        // 通过getForEntity调用get请求，返回对象包在ResponseEntity，可以获取到响应头及响应状态信息
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("token", "myToken");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Student studentRequest = new Student(1,"hello","12345",18);
        HttpEntity<?> httpEntity = new HttpEntity<>(studentRequest,httpHeaders);

        ResponseEntity<Student> resultEntity = restTemplate.exchange(url,HttpMethod.POST,httpEntity ,Student.class);
        Student student = resultEntity.getBody();

        log.info("response headers:{}",resultEntity.getHeaders());
        log.info("response status:{}",resultEntity.getStatusCode());
        log.info("response status value:{}",resultEntity.getStatusCodeValue());
        log.info("response body:{}",student);
        log.info("response student name :{}",student.getName());
    }
}
