package com.pudutech.mqtt.component.client.utils;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final String ALGORITHM_AES = "AES";// 加密算法名称
    public static final int KEY_SIZE_128 = 128;
    public static final int KEY_SIZE_192 = 192;
    public static final int KEY_SIZE_256 = 256;

    public static final String MODE_ECB = "ECB";
    public static final String MODE_CBC = "CBC";

    public static final String PADDING_ISO10126 = "ISO10126Padding";
    public static final String PADDING_PKCS5 = "PKCS5Padding";

//    private static byte[] aesKeyBytes = null;
//    private static byte[] aesIvBytes = null;

    private static String aesKey = "pudu202020192018";
    private static String aesIv = "pudu201820192020";

    @IntDef({KEY_SIZE_128, KEY_SIZE_192, KEY_SIZE_256})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AesKeySize {
    }

    @StringDef({MODE_ECB, MODE_CBC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    @StringDef({PADDING_ISO10126, PADDING_PKCS5})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Padding {
    }

//    static {
//        aesKeyBytes = generateKey();
//        System.out.println("aesKey=" + HexUtil.bytesToHexStr(aesKeyBytes));
//        aesIvBytes = generateIvBytes(aesKeyBytes.length);
//        System.out.println("aesIv=" + HexUtil.bytesToHexStr(aesIvBytes));
//    }

    /**
     * 产生一把AES密钥
     *
     * @return 字节数组形式的AES密钥
     */
    public static byte[] generateKey() {
        return AES.generateKey(KEY_SIZE_128);
    }

    /**
     * 产生一把AES密钥
     * <p>
     * 重载
     *
     * @param keySize 密钥大小，只能是128位（16字节）、192位（24字节）、256位（32字节）
     * @return 字节数组形式的AES密钥
     */
    public static byte[] generateKey(@AesKeySize int keySize) {
        // 保存AES密钥的字节数组
        byte[] keyBytes = null;

        try {
            // 获取密钥生成器
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM_AES);
            // 设置密钥长度，如果不调用该方法，默认生成192位的密钥
            generator.init(keySize);
            // 获取密钥对象
            SecretKey key = generator.generateKey();
            // 密钥转成字节数组
            keyBytes = key.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return keyBytes;
    }

    /**
     * 加密数据
     * 默认使用128位AesKey，"AES/ECB/PKCS5Padding"
     *
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        return AES.encrypt(data, aesKey.getBytes(), MODE_CBC, PADDING_PKCS5);
    }

    /**
     * 加密数据
     * 默认使用128位AesKey，"AES/ECB/PKCS5Padding"
     * 重载
     *
     * @param data
     * @param keyBytes
     * @param model
     * @param padding
     * @return
     */
    public static String encrypt(String data, byte[] keyBytes, String model, String padding) {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM_AES);
        try {
            // 构建Cipher对象，需要传入一个字符串，格式必须为"algorithm/mode/padding"或者"algorithm/",意为"算法/加密模式/填充方式"
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES + "/" + model + "/" + padding);

            // 初始化Cipher对象
            if (MODE_ECB.equals(model)) {
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(aesIv.getBytes());
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            }

            // 加密数据
            byte[] resultBytes = cipher.doFinal(data.getBytes());
            // 结果用Base64转码
            return HexUtil.bytesToHexStr(resultBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return "加密失败：" + data;
    }

    /**
     * 解密数据
     * 默认使用128位AesKey，"AES/ECB/PKCS5Padding"
     *
     * @param data
     * @return
     */
    public static String decrypt(String data) {
        return AES.decrypt(data, aesKey.getBytes(), MODE_CBC, PADDING_PKCS5);
    }

    /**
     * 解密数据
     * 默认使用128位AesKey，"AES/ECB/PKCS5Padding"
     * 重载
     *
     * @param data
     * @param keyBytes
     * @param model
     * @param padding
     * @return
     */
    public static String decrypt(String data, byte[] keyBytes, String model, String padding) {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM_AES);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_AES + "/" + model + "/" + padding);

            // 初始化Cipher对象
            if (MODE_ECB.equals(model)) {
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(aesIv.getBytes());
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            }

            byte[] temp = HexUtil.decodeHex(data.toCharArray());
            byte[] resultBytes = cipher.doFinal(temp);
            return new String(resultBytes, "UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return "解密失败：" + data;
    }

    private static byte[] generateIvBytes(int blockSize) {
        Random random = new Random();
        byte[] ivParam = new byte[blockSize];
        for (int index = 0; index < blockSize; index++) {
            ivParam[index] = (byte) random.nextInt(256);
        }

        return ivParam;
    }

    public static void main(String[] args) {
//        String data = "FreddyChen Hello world";
//        String encryptData = AES.encrypt(data);
//        System.out.println("加密后的数据：" + encryptData);
//
//        String decryptData = AES.decrypt(encryptData);
//        System.out.println("解密后的数据：" + decryptData);
//        byte[] aesKeyBytes = AES.generateKey();
//        System.out.println(aesKeyBytes.length);
//        String aesKey = HexUtil.bytesToHexStr(aesKeyBytes).toLowerCase();
//        byte[] aesIvBytes = AES.generateIvBytes(aesKeyBytes.length);
//        System.out.println(aesIvBytes.length);
//        String aesIv = HexUtil.bytesToHexStr(aesIvBytes).toLowerCase();
//
//        System.out.println("aesKey=" + aesKey + "\taesIv=" + aesIv);

//        String data = "FreddyChen Hello world";
//        String encryptData = AES.encrypt(data, aesKey.getBytes(), MODE_CBC, PADDING_PKCS5);
//        String encryptData = "A64C57D0521B3D4B15E562327E348961";
//        System.out.println("加密后的数据：" + encryptData);
//
//        String decryptData = AES.decrypt(encryptData, aesKey.getBytes(), MODE_CBC, PADDING_PKCS5);
//        System.out.println("解密后的数据：" + decryptData);
    }
}
