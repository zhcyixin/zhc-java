package com.zhc.controller.hole.file;

/**
 * 相信很多小伙伴工作中都有使用过IO操作，对文件进行读取、写入操作，
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author zhouhengchao
 * @since 2023-08-28 15:01:00
 */
@Slf4j
public class FileHoleTest {

    /**
     * 以GBK编码的形式写入一段文字"hello world:乱码问题测试"到文件hello.txt中，同时输出文件内容的16进制形式
     * @throws IOException
     */
    @Test
    void testFile01() throws IOException {
        String fileName = "hello.txt";
        Path path = Paths.get(fileName);

        Files.deleteIfExists(path);
        Files.write(path, "hello world:乱码问题测试".getBytes(Charset.forName("GBK")));
        log.info("bytes:{}", Hex.encodeHexString(Files.readAllBytes(path)).toUpperCase());
    }

    @Test
    void testFile02(){
        char[] chars = new char[10];
        String content = "";
        try (FileReader fileReader = new FileReader("hello.txt")) {
            int count;
            while ((count = fileReader.read(chars)) != -1) {
                content += new String(chars, 0, count);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("result:{}", content);

    }
}
