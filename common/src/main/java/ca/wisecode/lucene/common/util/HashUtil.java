package ca.wisecode.lucene.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/16/2024 1:55 PM
 * @Version: 1.0
 * @description:
 */

public class HashUtil {

    private static MessageDigest md;
    private static HashUtil instance = new HashUtil();

    private HashUtil() {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    public static HashUtil getInstance() {
        return instance;
    }

    public String calcHash(String content) {
        // 计算第一个数据的哈希值
        byte[] hash1 = md.digest(content.getBytes());
        return bytesToHex(hash1);
    }

    // 将字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
