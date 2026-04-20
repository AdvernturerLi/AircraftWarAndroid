package edu.hitsz.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ScoreRecord implements Serializable, Comparable<ScoreRecord> {
    @SerializedName("username")
    private final String userName;
    
    @SerializedName("score")
    private final int score;
    
    @SerializedName("timestamp")
    private final long timestamp;

    @SerializedName("difficulty")
    private final int difficulty; // 1: Easy, 2: Normal, 3: Hard

    private transient LocalDateTime recordTime;

    public ScoreRecord(String userName, int score, LocalDateTime recordTime, int difficulty) {
        this.userName = userName;
        this.score = score;
        this.recordTime = recordTime;
        this.timestamp = recordTime.toEpochSecond(ZoneOffset.of("+8"));
        this.difficulty = difficulty;
    }

    public String getUserName() { return userName; }
    public int getScore() { return score; }
    public int getDifficulty() { return difficulty; }

    public LocalDateTime getRecordTime() {
        if (recordTime == null) {
            recordTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.of("+8"));
        }
        return recordTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        String formattedTime = getRecordTime().format(formatter);
        return String.format("%s,%d,%s", userName, score, formattedTime);
    }

    @Override
    public int compareTo(ScoreRecord other) {
        return Integer.compare(other.score, this.score);
    }
}