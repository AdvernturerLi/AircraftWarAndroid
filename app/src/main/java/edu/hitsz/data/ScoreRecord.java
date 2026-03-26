// ScoreRecord.java

package edu.hitsz.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 排行榜数值对象实体类（Model/Entity）
 */
public class ScoreRecord implements Serializable, Comparable<ScoreRecord> {
    // private static final long serialVersionUID = 1L; // 序列化ID
    private final String userName;
    private final int score;
    private final LocalDateTime recordTime;

    public ScoreRecord(String userName, int score, LocalDateTime recordTime) {
        this.userName = userName;
        this.score = score;
        this.recordTime = recordTime;
    }

    public String getUserName() {
        return userName;
    }

    public int getScore() {
        return score;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    /**
     * 用于在控制台美观输出
     */
    @Override
    public String toString() {
        // 格式化时间，例如: 01-20 17:15
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        String formattedTime = recordTime.format(formatter);
        return String.format("%s,%d,%s", userName, score, formattedTime);
    }

    /**
     * 实现 Comparable 接口，按分数降序排序
     */
    @Override
    public int compareTo(ScoreRecord other) {
        return Integer.compare(other.score, this.score);
    }
}