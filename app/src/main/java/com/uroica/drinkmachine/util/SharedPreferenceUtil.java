package com.uroica.drinkmachine.util;
 
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class SharedPreferenceUtil {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private final static String SAVE_DATA_STR = "SAVE_DATA_UTIL";

    /**
     * 获取不到时默认数据
     */
    private final static int INT_EER = 0;

    /**
     * 初始化SharedPreference
     */
    public static void initSharedPreferenc(Context context) {
        if (context != null && sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SymmetryEncryptUtil.encryptString(SAVE_DATA_STR), Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    /**
     * 存储String类型数据
     *
     * @param key
     * @param value
     */
    public static void saveData(String key, String value) {
        if (editor != null) {
            editor.putString(SymmetryEncryptUtil.encryptString(key), SymmetryEncryptUtil.encryptString(value));
            editor.commit();
        }
    }

    /**
     * 存储数组类型数据
     *
     * @param key
     * @param list
     */
    public static void saveData(String key, List<String> list) {
        StringBuffer stringBuffer = new StringBuffer();
        if (editor != null) {
//            Log.i("数组","editor!= null");
            for (int i = 0; i < list.size(); i++) {
                stringBuffer.append(list.get(i));
                if(i<list.size()-1){
                    stringBuffer.append("&");
                }

            }

            editor.putString(SymmetryEncryptUtil.encryptString(key), SymmetryEncryptUtil.encryptString(stringBuffer.toString()));
            editor.commit();
        }else{
//            Log.i("数组","editor== null");
        }
    }

    /**
     * 存储整形数据
     *
     * @param key
     * @param value
     */
    public static void saveData(String key, int value) {
        if (editor != null) {
            editor.putInt(SymmetryEncryptUtil.encryptString(key), value);
            editor.commit();
        }
    }

    /**
     * 存储boolean类型数据
     *
     * @param key
     * @param value
     */
    public static void saveData(String key, boolean value) {
        if (editor != null) {
            editor.putBoolean(SymmetryEncryptUtil.encryptString(key), value);
            editor.commit();
        }
    }

    /**
     * 获取整形数据
     *
     * @param key
     * @return
     */
    public static int getIntData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(SymmetryEncryptUtil.encryptString(key), INT_EER);
        }
        return INT_EER;
    }

    /**
     * 获取整形数据
     *
     * @param key          键值
     * @param defaultValue 数值
     * @return
     */
    public static int getIntData(String key, int defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(SymmetryEncryptUtil.encryptString(key), defaultValue);
        }
        return defaultValue;
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    public static String getStrData(String key) {
        if (sharedPreferences != null) {
            return SymmetryEncryptUtil.decryptString(sharedPreferences.getString(SymmetryEncryptUtil.encryptString(key), ""));
        }
        return "";
    }

    /**
     * 获取boolean类型
     *
     * @param key
     * @return
     */
    public static boolean getBooleanData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(SymmetryEncryptUtil.encryptString(key), false);
        }
        return false;
    }

}
