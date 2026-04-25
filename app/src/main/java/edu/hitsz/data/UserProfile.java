package edu.hitsz.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class UserProfile implements Serializable {
    @SerializedName("username")
    private String nickname;
    
    @SerializedName("signature")
    private String signature;
    
    @SerializedName("level")
    private int level;
    
    @SerializedName("registerTime")
    private String registerTime;

    public UserProfile(String nickname, String signature, int level, String registerTime) {
        this.nickname = nickname;
        this.signature = signature;
        this.level = level;
        this.registerTime = registerTime;
    }

    public String getNickname() { return nickname; }
    public String getSignature() { return signature; }
    public int getLevel() { return level; }
    public String getRegisterTime() { return registerTime; }
    
    public void setNickname(String nickname) { this.nickname = nickname; }
}