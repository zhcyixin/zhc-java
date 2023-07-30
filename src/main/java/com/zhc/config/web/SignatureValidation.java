
package com.zhc.config.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zhc.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 相信小伙伴工作中，可能会遇到与第三方外部系统对接，需要将自己业务系统的接口提供给外部调用，为了接口的安全和管理，就需要对接口做鉴权；
 * 对于很多互联网大厂，很多都是在网关层有专门的鉴权控制，或者公司有自己的开放平台进行鉴权管理；
 * 但相对都比较重量级，今天给大家分享一种基于切面Spring Aop实现的一种简单鉴权逻辑，如果对大家有多帮助，给个关注三连支持下。
 *
 * 包含内容：
 * 1、请求超时限制；
 * 2、请求重复限制；
 * 3、appid验证。
 *
 *
 * 最简单的第三方接口鉴权实现
 *
 * @author zhouhengchao
 * @since 2023-07-23 16:29:00
 */
@Aspect
@Component
@Slf4j
public class SignatureValidation {

    @Resource
    private HttpServletRequest request;

    @Resource
    private RedissonClient redissonClient;

    public static final String SIGNATURE_PREFIX = "third:app:signature:";

    private String getFieldsName(JoinPoint joinPoint) {
        StringBuffer paramStr = new StringBuffer();
        for (Object object : joinPoint.getArgs()) {
            if (object == null) {
                continue;
            }
            paramStr.append(object.toString());
        }
        return paramStr.toString();

    }

    /**
     * 配置需要做接口鉴权的Controller包路径，在如下配置的包路径下就会触发鉴权功能
     */
    @Pointcut("execution(public * com.zhc.controller.signature..*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) {

        String appid = "";
        String timestamp = "";
        String nonce = "";
        String sign = "";

        try {
            // 分配给第三方的id，验证用户的真实性
            appid = request.getHeader("appid");
            // 请求发起的时间
            timestamp = request.getHeader("timestamp");
            // 随机数
            nonce = request.getHeader("nonce");
            // 签名算法生成的签名
            sign = request.getHeader("sign");
            if (StringUtil.isEmpty(appid) || StringUtil.isEmpty(timestamp) || StringUtil.isEmpty(nonce) || StringUtil.isEmpty(sign)) {
                throw new BusinessException("请求头参数不能为空");
            }
            // 限制为（含）3600秒以内发送的请求，超时后请求失效
            long time = 3600;
            long now = System.currentTimeMillis()/1000;
            if (now - Long.valueOf(timestamp) > time) {
                throw new BusinessException("请求发起时间超过服务器限制时间");
            }
            // 验证appid是否正确，这里实际中可以通过数据库维护，这里简化为固定的值为："thirdAppId1"
            if(!Objects.equals(appid,"thirdAppId1")){
                throw new BusinessException("传入的appId不合法。");
            }

            JSONObject signObj = new JSONObject();
            signObj.put("appid", appid);
            signObj.put("timestamp", timestamp);
            signObj.put("nonce", nonce);

            String requestBody = getFieldsName(point);
            signObj.put("sign", signDataParam(this.getBody(requestBody)));
            // secret与前端约定好
            String mySign = signHeaderParam(signObj, "apiKey.getSecret()");
            // 验证签名
            if (!mySign.equals(sign)) {
                throw new BusinessException("接口参数验签失败");
            }

            // 验证请求是否重复
            RBucket<Object> bucket = redissonClient.getBucket(nonce);
            String nonceRedis = (String) bucket.get();
            if (StringUtil.isNotEmpty(nonceRedis)) {
                throw new BusinessException("请不要发送重复的请求");
            } else {
                bucket.set(SIGNATURE_PREFIX + "_" + nonce);
                bucket.expire(120, TimeUnit.SECONDS);
            }

            try {
                return point.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return null;
    }

    // 获取请求body中的参数，对requestBody参数排序
    public static SortedMap<String, String> getBody(String requestBodyStr) {
        SortedMap<String, String> map = new TreeMap<>();
        try {
            // Remove class name and parentheses
            int start = requestBodyStr.indexOf('(') + 1;
            int end = requestBodyStr.lastIndexOf(')');
            String content = requestBodyStr.substring(start, end);
            // Split content into key-value pairs
            String[] pairs = content.split(", ");
            for (String pair : pairs) {
                String[] parts = pair.split("=");
                String key = parts[0];
                String value = parts[1];
                map.put(key, value);
            }
            return map;
        } catch (Exception e) {
            log.error("请求的body参数为空");
        }
        return null;
    }

    /**
     * 将body中的参数做一次md5的转换
     * @param params
     * @return
     */
    public static String signDataParam(SortedMap<String, String> params) {
        if(Objects.isNull(params)){
            return "";
        }
        return DigestUtils.md5DigestAsHex(JSON.toJSONString(params).getBytes()).toUpperCase();
    }

    /**
     * 获取签名信息
     *
     * @param data
     * @param secret
     * @return
     */

    private static String signHeaderParam(JSONObject data, String secret) throws NoSuchAlgorithmException {
        // 参数排序
        Set<String> keySet = data.keySet();
        String[] keyArr = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArr);
        StringBuilder sbd = new StringBuilder();
        for (String k : keyArr) {
            if (StringUtil.isNotEmpty(data.getString(k))) {
                sbd.append(k + "=" + data.getString(k) + "&");
            }
        }
        // secret最后拼接
        sbd.append("secret=").append(secret);

        //确定计算方法
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        String newstr = DigestUtils.md5DigestAsHex(sbd.toString().getBytes()).toUpperCase();
        return base64en.encode(newstr.getBytes());
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {

        JSONObject signObj = new JSONObject();
        signObj.put("appid", "thirdAppId1");
        signObj.put("timestamp", "1690703259");
        signObj.put("nonce", "981235");

        SortedMap<String,String> sortedMap = new TreeMap<>();
        sortedMap.put("name","小红");
        sortedMap.put("stuNo","1234");
        sortedMap.put("age","18");
        signObj.put("sign", signDataParam(sortedMap));
        String mySign = signHeaderParam(signObj, "apiKey.getSecret()");
        System.out.println(mySign);
    }
}


