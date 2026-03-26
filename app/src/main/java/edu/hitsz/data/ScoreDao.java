// ScoreDao.java

package edu.hitsz.data;

import java.util.List;

/**
 * 得分数据访问对象接口（DAO Interface）
 */
public interface ScoreDao {
    /**
     * 获取所有得分记录，并按分数降序排列
     * @return 排好序的得分记录列表
     */
    List<ScoreRecord> getAllScores();

    /**
     * 增加一条新的得分记录
     * @param record 新的得分记录
     */
    void addScore(ScoreRecord record);

    /**
     * 清空所有得分记录
     */
    void clearScores();

    void removeScore(ScoreRecord record);
    /**
     * 打印排行榜到控制台
     * @param scoreRecords 要打印的记录列表
     */
    void printScores(List<ScoreRecord> scoreRecords);

    /**
     * 移除排行榜中指定索引位置的记录
     * @param index 要移除的记录在当前排序列表中的索引
     */
    void removeScoreByIndex(int index);
}