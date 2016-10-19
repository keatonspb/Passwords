package ru.discode.passwords.util;

import android.util.Log;

import ru.discode.passwords.BuildConfig;


/**
 * Created by broadcaster on 30.06.2016.
 */
public class SLog {
    public static void e(String tag, String msg) {
        if(BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }
    public static void e(String tag, String msg, Throwable tr) {
        if(BuildConfig.DEBUG) {
            Log.e(tag, msg, tr);
        }
    }
    public static void d(String tag, String msg) {
        if(BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
    public static void d(String tag, String msg, Throwable tr) {
        if(BuildConfig.DEBUG) {
            Log.d(tag, msg, tr);
        }
    }
    public static void i(String tag, String msg) {
        if(BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }
    public static void i(String tag, String msg, Throwable tr) {
        if(BuildConfig.DEBUG) {
            Log.i(tag, msg, tr);
        }
    }
}
