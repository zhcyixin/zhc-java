package com.zhc.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DingTalkSendMsgUtil {
    // 配置自己创建的钉钉群webhook
    public static String URL = "";


    public static void sendToDingTalk(Exception exception) {
        try{
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msgtype", "text");
            JSONObject subsJsonObject = new JSONObject();
            subsJsonObject.put("content", "[LTID:" + MDC.get("traceId") + "]\n" + sw);
            jsonObject.put("text", subsJsonObject);
            Map<String, String> headersMap = new HashMap<>();
            headersMap.put("Content-Type", "application/json");
            HttpRequestUtils.postJson(URL, jsonObject.toJSONString(), headersMap);
            // 实际中可以根据环境不同，推送到不同的钉钉群
//            if(SpringUtils.getActiveProfile().equals("prd")){
//                HttpRequestUtils.postJson(URL, jsonObject.toJSONString(), headersMap);
//            }else{
//                HttpRequestUtils.postJson(URL, jsonObject.toJSONString(), headersMap);
//            }

        }catch (Exception e){
            log.error("消息推送钉钉发送异常",e);
        }
    }
}
