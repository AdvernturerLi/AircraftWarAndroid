package edu.hitsz.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {
    private static final String PREFS_NAME = "aircraftwar_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_NICKNAME = "user_nickname";
    private static final String KEY_USER_SIGNATURE = "user_signature";
    private static final String KEY_REGISTER_TIME = "register_time";

    // 穿透域名 (重启 cpolar 后记得修改这里)
    public static final String HOST = "10.250.95.44:8080";
    public static final String BASE_URL = "http://" + HOST + "/api";
    public static final String WS_URL = "ws://" + HOST + "/ws/game";

    // 当前登录状态
    public static boolean isLoggedIn = false;
    // 当前登录的用户名（全局共享）
    public static String userNickname = "访客";
    public static String userSignature = "点击这里修改个性签名...";
    public static String registerTime = "";

    public static void loadSession(Context context) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);
        userNickname = preferences.getString(KEY_USER_NICKNAME, "访客");
        userSignature = preferences.getString(KEY_USER_SIGNATURE, "点击这里修改个性签名...");
        registerTime = preferences.getString(KEY_REGISTER_TIME, "");
        if (userNickname == null || userNickname.trim().isEmpty()) userNickname = "访客";
        if (userSignature == null || userSignature.trim().isEmpty()) userSignature = "点击这里修改个性签名...";
        if (registerTime == null) registerTime = "";
    }

    public static void saveSession(Context context, boolean loggedIn, String nickname) {
        saveSession(context, loggedIn, nickname, userSignature, registerTime);
    }

    public static void saveSession(Context context, boolean loggedIn, String nickname, String signature, String time) {
        String safeNickname = (nickname == null || nickname.trim().isEmpty()) ? "访客" : nickname.trim();
        String safeSignature = (signature == null || signature.trim().isEmpty()) ? "点击这里修改个性签名..." : signature.trim();
        String safeRegisterTime = (time == null) ? "" : time.trim();
        isLoggedIn = loggedIn;
        userNickname = safeNickname;
        userSignature = safeSignature;
        registerTime = safeRegisterTime;

        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, loggedIn)
                .putString(KEY_USER_NICKNAME, safeNickname)
                .putString(KEY_USER_SIGNATURE, safeSignature)
                .putString(KEY_REGISTER_TIME, safeRegisterTime)
                .apply();
    }

    public static void clearSession(Context context) {
        isLoggedIn = false;
        userNickname = "访客";
        userSignature = "点击这里修改个性签名...";
        registerTime = "";

        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .putString(KEY_USER_NICKNAME, "访客")
                .putString(KEY_USER_SIGNATURE, "点击这里修改个性签名...")
                .putString(KEY_REGISTER_TIME, "")
                .apply();
    }

    public static String getCurrentUserName() {
        return (userNickname == null || userNickname.trim().isEmpty()) ? "访客" : userNickname;
    }
}