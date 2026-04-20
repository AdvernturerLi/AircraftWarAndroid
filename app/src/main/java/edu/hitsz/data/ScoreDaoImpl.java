// ScoreDaoImpl.java

package edu.hitsz.data;

import android.content.Context;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 得分数据访问对象实现类（DAO Implementation）
 * 使用文件序列化存储数据，适配 Android 环境
 */
public class ScoreDaoImpl implements ScoreDao {
    private final String dataFileName = "score_records.csv";
    private final File dataFile;
    private List<ScoreRecord> scoreRecords;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ScoreDaoImpl(Context context) {
        // 使用 Android 应用的内部存储目录
        this.dataFile = new File(context.getFilesDir(), dataFileName);
        // 初始化时，从文件中加载数据
        this.scoreRecords = loadFromFile();
    }

    @Override
    public List<ScoreRecord> getAllScores() {
        // 返回一个排序后的副本
        List<ScoreRecord> sortedScores = new LinkedList<>(this.scoreRecords);
        Collections.sort(sortedScores); // 默认按 ScoreRecord 的 compareTo 方法降序排序
        return sortedScores;
    }

    @Override
    public void addScore(ScoreRecord record) {
        this.scoreRecords.add(record);
        saveToFile(); // 添加后保存到文件
    }

    @Override
    public void removeScore(ScoreRecord record) {
        if (this.scoreRecords.remove(record)) {
            saveToFile();
        }
    }

    @Override
    public void removeScoreByIndex(int index) {
        List<ScoreRecord> sortedRecords = getAllScores();
        if (index >= 0 && index < sortedRecords.size()) {
            ScoreRecord recordToDelete = sortedRecords.get(index);
            if (this.scoreRecords.remove(recordToDelete)) {
                saveToFile();
            }
        }
    }

    @Override
    public void clearScores() {
        this.scoreRecords.clear();
        saveToFile();
    }

    @Override
    public void printScores(List<ScoreRecord> scoreRecords) {
        // Android 中通常不使用 System.out 打印排行榜，但在 Logcat 中可见
        for (int i = 0; i < scoreRecords.size(); i++) {
            ScoreRecord record = scoreRecords.get(i);
            android.util.Log.i("ScoreDao", "第" + (i + 1) + "名: " + record.toString());
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (ScoreRecord record : this.scoreRecords) {
                String line = record.getUserName() + "," +
                        record.getScore() + "," +
                        record.getRecordTime().format(formatter) + "," +
                        record.getDifficulty();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ScoreRecord> loadFromFile() {
        List<ScoreRecord> loadedList = new LinkedList<>();
        if (dataFile.exists() && dataFile.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        try {
                            String userName = parts[0];
                            int score = Integer.parseInt(parts[1].trim());
                            LocalDateTime recordTime = LocalDateTime.parse(parts[2].trim(), formatter);
                            int difficulty = 0; // Default or legacy
                            if (parts.length >= 4) {
                                difficulty = Integer.parseInt(parts[3].trim());
                            }
                            loadedList.add(new ScoreRecord(userName, score, recordTime, difficulty));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return loadedList;
    }
}
