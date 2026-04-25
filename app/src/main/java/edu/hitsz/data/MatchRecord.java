package edu.hitsz.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MatchRecord implements Serializable {
    @SerializedName("player1")
    private String player1;
    
    @SerializedName("player2")
    private String player2;
    
    @SerializedName("score1")
    private int score1;
    
    @SerializedName("score2")
    private int score2;
    
    @SerializedName("timestamp")
    private long timestamp;

    public String getOpponent(String myName) {
        return player1.equals(myName) ? player2 : player1;
    }

    public String getResult(String myName) {
        int myScore = player1.equals(myName) ? score1 : score2;
        int opScore = player1.equals(myName) ? score2 : score1;
        if (myScore > opScore) return "WIN";
        if (myScore < opScore) return "LOSS";
        return "DRAW";
    }

    public String getTimeString() {
        LocalDateTime time = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.of("+8"));
        return time.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
    }

    public int getMyScore(String myName) {
        return player1.equals(myName) ? score1 : score2;
    }
    
    public int getOpScore(String myName) {
        return player1.equals(myName) ? score2 : score1;
    }
}