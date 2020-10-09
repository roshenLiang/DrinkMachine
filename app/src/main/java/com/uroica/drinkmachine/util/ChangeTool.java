package com.uroica.drinkmachine.util;

/**
 * Created by WangChaowei on 2017/12/11.
 */

public class ChangeTool {
    //-------------------------------------------------------
    // 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
    public static int isOdd(int num) {
        return num & 1;
    }

    //    //-------------------------------------------------------
//    //Hex字符串转int
//    public static int HexToInt(String inHex) {
//        return Integer.parseInt(inHex, 16);
//    }
//
//    //-------------------------------------------------------
    //Hex字符串转byte
    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    //-------------------------------------------------------
    //1字节转2个Hex字符
    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", new Object[]{inByte}).toUpperCase();
    }

    /*
        0A-->10
        FF-->-1
     */
    public static String HexString2String(String s) {
        int i = Integer.parseInt(s, 16);
        byte b4 = (byte) (i & 0xff);
        return String.valueOf(b4);
    }

    //-------------------------------------------------------
    //字节数组转转hex字符串
    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte valueOf : inBytArr) {
            strBuilder.append(Byte2Hex(Byte.valueOf(valueOf)));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

//    //-------------------------------------------------------
//    //字节数组转转hex字符串，可选长度
//    public static String ByteArrToHex(byte[] inBytArr, int offset, int byteCount) {
//        StringBuilder strBuilder = new StringBuilder();
//        int j = byteCount;
//        for (int i = offset; i < j; i++) {
//            strBuilder.append(Byte2Hex(Byte.valueOf(inBytArr[i])));
//        }
//        return strBuilder.toString();
//    }

    //    /**
//     * 把十六进制表示的字节数组字符串，转换成十六进制字节数组
//     *
//     * @param
//     * @return byte[]
//     */
//    public static byte[] hexStr2bytes(String hex) {
//        int len = (hex.length() / 2);
//        byte[] result = new byte[len];
//        char[] achar = hex.toUpperCase().toCharArray();
//        for (int i = 0; i < len; i++) {
//            int pos = i * 2;
//            result[i] = (byte) (hexChar2byte(achar[pos]) << 4 | hexChar2byte(achar[pos + 1]));
//        }
//        return result;
//    }
//
//    /**
//     * 把16进制字符[0123456789abcde]（含大小写）转成字节
//     *
//     * @param c
//     * @return
//     */
//    private static int hexChar2byte(char c) {
//        switch (c) {
//            case '0':
//                return 0;
//            case '1':
//                return 1;
//            case '2':
//                return 2;
//            case '3':
//                return 3;
//            case '4':
//                return 4;
//            case '5':
//                return 5;
//            case '6':
//                return 6;
//            case '7':
//                return 7;
//            case '8':
//                return 8;
//            case '9':
//                return 9;
//            case 'a':
//            case 'A':
//                return 10;
//            case 'b':
//            case 'B':
//                return 11;
//            case 'c':
//            case 'C':
//                return 12;
//            case 'd':
//            case 'D':
//                return 13;
//            case 'e':
//            case 'E':
//                return 14;
//            case 'f':
//            case 'F':
//                return 15;
//            default:
//                return -1;
//        }
//    }
    //-------------------------------------------------------
    //把hex字符串转字节数组
    public static byte[] HexToByteArr(String inHex) {
        byte[] result;
        int hexlen = inHex.length();
        if (isOdd(hexlen) == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * 将int数值转换为占两个字节的byte数组
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[2];
        src[0] = (byte) (value & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
//        src[2] =  (byte) ((value>>16) & 0xFF);
//        src[3] =  (byte) ((value>>24) & 0xFF);
        return src;
    }

    /**
     * byte数组中取int数值
     *
     * @param src
     *            byte数组
     * @return int数值
     */
//    public static int bytesToInt(byte[] src) {
//        int value;
//        value = (int) ((src[0] & 0xFF)
//                | ((src[1] & 0xFF)<<8)
//                | ((src[2] & 0xFF)<<16)
//                | ((src[3] & 0xFF)<<24));
//        return value;
//    }

    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 0) & 0x1);
    }

    /**
     * Bit转Byte
     */
    public static byte bitToByte(String bit) {
        int re, len;
        if (null == bit) {
            return 0;
        }
        len = bit.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (bit.charAt(0) == '0') {// 正数
                re = Integer.parseInt(bit, 2);
            } else {// 负数
                re = Integer.parseInt(bit, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(bit, 2);
        }
        return (byte) re;
    }

    /*
        自动补0
     */
    public static String codeAddOne(String code, int len) {
        while (code.length() < len) {
            code = "0" + code;
        }
        return code;
    }

    public static byte[] hexStr2bytes(String hexStr) {
        if (hexStr.length() % 2 != 0) {//长度为单数
            hexStr = "0" + hexStr;//前面补0
        }
        char[] chars = hexStr.toCharArray();
        int len = chars.length / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            int x = i * 2;
            bytes[i] = (byte) Integer.parseInt(String.valueOf(new char[]{chars[x], chars[x + 1]}), 16);
        }
        return bytes;
    }

    /**
     * 字节数组转换成对应的16进制表示的字符串
     *
     * @param src
     * @return
     */
    public static String bytes2HexStr(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            builder.append(buffer);
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 十六进制字节数组转字符串
     *
     * @param src    目标数组
     * @param dec    起始位置
     * @param length 长度
     * @return
     */
    public static String bytes2HexStr(byte[] src, int dec, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(src, dec, temp, 0, length);
        return bytes2HexStr(temp);
    }
}
