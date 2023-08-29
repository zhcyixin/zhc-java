package com.zhc.password;

import org.passay.CharacterData;

/**
 * 密码生成-自定义特殊字符规则校验
 *
 * @author zhouhengchao
 * @since  2023-07-19 12:08
 */
public enum CustomEnglishCharacterData implements CharacterData {
    /**
     * Special characters.
     */
    Special("INSUFFICIENT_SPECIAL", "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");

    /** Error code. */
    private final String errorCode;

    /** Characters. */
    private final String characters;

    CustomEnglishCharacterData(String errorCode, String characters) {
        this.errorCode = errorCode;
        this.characters = characters;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getCharacters() {
        return characters;
    }


}
