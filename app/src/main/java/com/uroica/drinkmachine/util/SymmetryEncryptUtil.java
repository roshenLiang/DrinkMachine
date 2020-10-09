package com.uroica.drinkmachine.util;
 
public class SymmetryEncryptUtil {
    /**	加密秘钥*/
    private final static char Secret_key = '¿';
    /**
     * 加密字符串
     * @param originalStr 加密前字符串
     * @return
     */
    public static String encryptString(String originalStr) {
        String encryptStr = "";
        if(originalStr != null) {
            char[] c = originalStr.toCharArray();
            for(int i = 0; i < c.length; i++) {
                c[i]= (char) (c[i] + Secret_key);
            }
            encryptStr = String.valueOf(c);
        }

        return encryptStr;
    }

    /**
     * 解密字符串
     * @param ciphertext 密文
     * @return 返回明文
     */
    public static String decryptString(String ciphertext) {
        String decryptStr = "";
        if(ciphertext != null) {
            char[] c = ciphertext.toCharArray();
            for(int i = 0; i < c.length; i++) {
                c[i]= (char) (c[i] - Secret_key);
            }
            decryptStr = String.valueOf(c);
        }
        return decryptStr;
    }
}
