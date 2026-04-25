package edu.hitsz.data;

public class Config {
    // 穿透域名 (重启 cpolar 后记得修改这里)
    public static final String HOST = "10.250.95.44:8080";

    public static final String BASE_URL = "http://" + HOST + "/api";
    public static final String WS_URL = "ws://" + HOST + "/ws/game";

    // 当前登录的用户名（全局共享）
    public static String userNickname = "新晋飞行员";
}