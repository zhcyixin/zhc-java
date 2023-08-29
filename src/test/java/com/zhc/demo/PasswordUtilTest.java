package com.zhc.demo;

import com.zhc.enums.PasswordErrorCodeMsgEnum;
import com.zhc.password.CustomEnglishCharacterData;
import com.zhc.password.CustomPasswordData;
import com.zhc.password.MobileRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.passay.*;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 相信很多小伙伴工作中都有遇到过自动生成指定规则的字符串作为密码，对密码进行规则性校验，并输出自定义规则校验提示，
 * 今天给大家分享一个开源项目，让你优雅轻松实现复杂密码规则校验、自定义密码生成规则、快速简单应对项目密码要求，通过公司安全组规范。
 * 只需要引入依赖
 * <dependency>
*   <groupId>org.passay</groupId>
*   <artifactId>passay</artifactId>
*   <version>1.6.3</version>
* </dependency>
 * Passay API 有 3 个核心组件:
 * 1、规则 - 定义密码策略规则集的一个或多个规则；
 * 2、PasswordValidator− 根据给定规则集验证密码的验证器组件；
 * 3、PasswordGenerator− 生成密码以满足给定规则集的生成器组件。
 *
 * @author zhouhengchao
 * @since 2023-08-29 09:35:00
 */
@Slf4j
public class PasswordUtilTest {

    /**
     * 场景一：密码校验，长度必须为8到16位，必须包含字母、数字、符号中至少两位，不包含空格
     * 传统的实现方式需要自己开发逻辑，或者通过正则表达式进行匹配判断，使用password api非常丝滑简单
     */
    @Test
    void testPassword01(){
        List<Rule> rules = new ArrayList<>();
        // 设置长度为8到16
        rules.add(new LengthRule(8, 16));
        // 设置验证规则必须包含字母、数字、特殊字符，且必须满足两个规则
        CharacterCharacteristicsRule characteristicsRule = new CharacterCharacteristicsRule(2,
                new CharacterRule(EnglishCharacterData.Alphabetical, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1));
        rules.add(characteristicsRule);
        // 不能包含空格
        rules.add(new WhitespaceRule(new char[]{0x20}));

        PasswordValidator qqValidator = new PasswordValidator(rules);
        // 该密码字符包含空格，应该检查不通过
        String pass = "12345678 a";
        RuleResult ruleResult = qqValidator.validate(new PasswordData(pass));
        if(!ruleResult.isValid()){
            for(RuleResultDetail detail: ruleResult.getDetails()){
                log.info(detail.getErrorCode());
            }
        }

        Assert.isTrue(ruleResult.isValid(),"密码规则校验不通过。");
    }

    /**
     * 场景二：密码校验，长度必须为8到16位，必须包含字母大小写、数字、符号同时存在，不包含空格
     *
     */
    @Test
    void testPassword02(){
        List<Rule> rules = new ArrayList<>();
        // 设置长度为8到16
        rules.add(new LengthRule(8, 16));
        // 设置验证规则必须包含字母、数字、特殊字符，且必须满足两个规则
        CharacterCharacteristicsRule characteristicsRule = new CharacterCharacteristicsRule(4,
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1));
        rules.add(characteristicsRule);
        // 不能包含空格
        rules.add(new WhitespaceRule(new char[]{0x20}));

        PasswordValidator qqValidator = new PasswordValidator(rules);
        // 该密码字符不包含大写和特殊字符，应该检查不通过
        String pass = "12345678a";
        //String pass = "A1234%5678a";
        RuleResult ruleResult = qqValidator.validate(new PasswordData(pass));
        if(!ruleResult.isValid()){
            for(RuleResultDetail detail: ruleResult.getDetails()){
                log.info(detail.getErrorCode());
            }
        }

        Assert.isTrue(ruleResult.isValid(),"密码规则校验不通过。");
    }

    /**
     * 场景三：密码校验，长度必须为8到16位，必须包含字母大小写、数字、符号同时存在，不包含空格
     * 在之前的基础上，规定数字和字母不能四个连续递增，可以通过IllegalSequenceRule实现
     *
     */
    @Test
    void testPassword03(){
        List<Rule> rules = new ArrayList<>();
        // 设置长度为8到16
        rules.add(new LengthRule(8, 16));
        // 设置验证规则必须包含字母、数字、特殊字符，且必须满足两个规则
        CharacterCharacteristicsRule characteristicsRule = new CharacterCharacteristicsRule(4,
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1));
        rules.add(characteristicsRule);
        // 不能包含空格
        rules.add(new WhitespaceRule(new char[]{0x20}));
        // 添加数字不能4个连续规则、字母不能4个连续，第三个布尔参数表示是否允许循环，如yzab形式
        rules.add(new IllegalSequenceRule(EnglishSequenceData.Numerical,4,false));
        rules.add(new IllegalSequenceRule(EnglishSequenceData.Alphabetical,4,false));
        PasswordValidator qqValidator = new PasswordValidator(rules);
        // 该密码字符不包含大写和特殊字符，应该检查不通过
        String pass = "A123%5678a";
        RuleResult ruleResult = qqValidator.validate(new PasswordData(pass));
        if(!ruleResult.isValid()){
            for(RuleResultDetail detail: ruleResult.getDetails()){
                PasswordErrorCodeMsgEnum.getMsg(detail.getErrorCode()).ifPresent(System.out::println);
            }
        }

        Assert.isTrue(ruleResult.isValid(),"密码规则校验不通过。");
    }

    /**
     * 场景四：在场景四的基础上，添加校验密码不能包含手机号，通过自定义校验规则和校验传参
     */
    @Test
    void testPassword04(){
        List<Rule> rules = new ArrayList<>();
        // 设置长度为8到16
        rules.add(new LengthRule(8, 20));
        // 设置验证规则必须包含字母、数字、特殊字符，且必须满足两个规则
        CharacterCharacteristicsRule characteristicsRule = new CharacterCharacteristicsRule(4,
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1));
        rules.add(characteristicsRule);
        // 不能包含空格
        rules.add(new WhitespaceRule(new char[]{0x20}));
        // 添加数字不能4个连续规则、字母不能4个连续，第三个布尔参数表示是否允许循环，如yzab形式
        rules.add(new IllegalSequenceRule(EnglishSequenceData.Numerical,4,false));
        rules.add(new IllegalSequenceRule(EnglishSequenceData.Alphabetical,4,false));
        // 添加手机号规则
        rules.add(new MobileRule());
        PasswordValidator qqValidator = new PasswordValidator(rules);
        // 该密码字符不包含大写和特殊字符，应该检查不通过
        String pass = "A123%13458176021a";
        RuleResult ruleResult = qqValidator.validate(new CustomPasswordData("",pass,"13458176021"));
        if(!ruleResult.isValid()){
            for(RuleResultDetail detail: ruleResult.getDetails()){
                PasswordErrorCodeMsgEnum.getMsg(detail.getErrorCode()).ifPresent(System.out::println);
            }
        }

        Assert.isTrue(ruleResult.isValid(),"密码规则校验不通过。");
    }

    /**
     * 场景五：密码生成，规则必须包含大小写、数字、特殊字符，大写字母至少一个，小写至少3个、数字至少两个，特殊字符至少2个
     * 通过运行输出可能看到特殊字符包含所有特殊字符情况，如!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿×÷–—―‗，
     * 并不是都适合密码使用的，其实passay库也是支持自定义字符规则的。
     */
    @Test
    void testPassword05(){
        List<CharacterRule> characterRuleList = Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 3),
                new CharacterRule(EnglishCharacterData.Digit, 2),
                new CharacterRule(EnglishCharacterData.Special, 2)
        );

        PasswordGenerator generator = new PasswordGenerator();
        // 第一个参数为密码长度，第二个为密码规则列表
        String s = generator.generatePassword(10, characterRuleList);
        log.info("生成的密码为:{}", s);

    }

    /**
     * 场景六：密码生成，规则必须包含大小写、数字、特殊字符，大写字母至少一个，小写至少3个、数字至少两个，特殊字符至少2个
     * 自定义特殊字符，特殊字符只能包含!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
     * 类似的也可以自定义数字范围和字母范围，参考特殊字符实现方式。，实际中passay提供了丰富的规则库，感兴趣的可以下来学习下。
     */
    @Test
    void testPassword06(){
        List<CharacterRule> characterRuleList = Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 3),
                new CharacterRule(EnglishCharacterData.Digit, 2),
                new CharacterRule(CustomEnglishCharacterData.Special, 2)
        );

        PasswordGenerator generator = new PasswordGenerator();
        // 第一个参数为密码长度，第二个为密码规则列表
        String s = generator.generatePassword(10, characterRuleList);
        log.info("生成的密码为:{}", s);
    }

    /**
     * 总结：
     * 1、passay开源项目提供了丰富的密码规则库，可以用于密码校验和密码生成；
     * 2、合理使用避免重复造轮子，提高工作效率，passay也支持自定义字符规则，通过实现CharacterData接口；
     * 3、对于校验密码返回值文案显示，也支持自定义，可以将对应文案定义在枚举中，通过code获取到对应中文信息；
     * 4、通过实现Rule接口，也支持自定义参数校验，校验密码中不能包含手机号
     */
}
