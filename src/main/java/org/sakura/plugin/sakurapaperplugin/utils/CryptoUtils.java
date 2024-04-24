package org.sakura.plugin.sakurapaperplugin.utils;


import org.sakura.plugin.sakurapaperplugin.entity.EnvironmentConfig;
import org.sakura.plugin.sakurapaperplugin.websocket.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import static org.sakura.plugin.sakurapaperplugin.utils.ConfigLoader.loadEnvironmentConfig;

public class CryptoUtils {

    private static final int KEY_SIZE = 32; // 256 bits
    private static final int IV_SIZE = 16;  // 128 bits

    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);


    private SecretKeyAndIv keyAndIv;

    EnvironmentConfig config = loadEnvironmentConfig("env.json");


    public CryptoUtils(){
        String base64Encoded = config.getAES_BASE64_KEY();
        logger.info("AES_BASE64_KEY: " + base64Encoded);
        keyAndIv = extractKeyAndIv(base64Encoded);
    }

    /**
     * 生成Base64编码的密钥和IV字符串
     */
    public static String generateKeyIvBase64() {
        // 生成密钥和IV
        byte[] keyBytes = new byte[KEY_SIZE];
        byte[] ivBytes = new byte[IV_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(keyBytes);
        secureRandom.nextBytes(ivBytes);

        // 拼接密钥和IV
        byte[] combined = new byte[KEY_SIZE + IV_SIZE];
        System.arraycopy(keyBytes, 0, combined, 0, KEY_SIZE);
        System.arraycopy(ivBytes, 0, combined, KEY_SIZE, IV_SIZE);

        // Base64编码
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * 加密文本
     */
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 使用预先生成的密钥和IV
        cipher.init(Cipher.ENCRYPT_MODE,keyAndIv.secretKey, keyAndIv.iv);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 解密文本
     */
    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyAndIv.secretKey, keyAndIv.iv);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    /**
     * 从Base64编码的字符串中提取密钥和IV
     */
    public SecretKeyAndIv extractKeyAndIv(String base64Encoded) {
        byte[] combined = Base64.getDecoder().decode(base64Encoded);
        byte[] keyBytes = Arrays.copyOfRange(combined, 0, KEY_SIZE);
        byte[] ivBytes = Arrays.copyOfRange(combined, KEY_SIZE, KEY_SIZE + IV_SIZE);

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        return new SecretKeyAndIv(secretKey, iv);
    }

    /**
     * 简单的数据类来持有密钥和IV
     */
    public class SecretKeyAndIv {
        public final SecretKey secretKey;
        public final IvParameterSpec iv;

        public SecretKeyAndIv(SecretKey secretKey, IvParameterSpec iv) {
            this.secretKey = secretKey;
            this.iv = iv;
        }


    }

    public static void main(String[] args) {
        String t = CryptoUtils.generateKeyIvBase64();
        System.out.println(t);
    }
}
