package edu.hitsz.data;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String avatar;
    private String nickname;
    private String signature;
    private int level;
    private String registerTime;

    public UserProfile(String nickname, String signature, int level, String registerTime) {
        this.nickname = nickname;
        this.signature = signature;
        this.level = level;
        this.registerTime = registerTime;
    }

    public String getAvatar() { return avatar; }
    public String getNickname() { return nickname; }
    public String getSignature() { return signature; }
    public int getLevel() { return level; }
    public String getRegisterTime() { return registerTime; }
}
