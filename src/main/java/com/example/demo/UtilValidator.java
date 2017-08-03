package com.example.demo;

/**
 * 验证工具类
 * Created by rex on 2017/7/28.
 */
public class UtilValidator {
    public static boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isEmpty(Double s) {
        if (s == null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(Object s) {
        if (s == null) {
            return true;
        } else {
            return false;
        }
    }

}
