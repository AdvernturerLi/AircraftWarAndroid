package edu.hitsz.data;

import java.io.Serializable;

public class GameStats implements Serializable {
    private int totalMatches;
    private int totalWins;
    private double winRate;
    private int maxKillStreak;
    private int totalKills;
    private int totalDeaths;

    public GameStats(int totalMatches, int totalWins, int maxKillStreak, int totalKills, int totalDeaths) {
        this.totalMatches = totalMatches;
        this.totalWins = totalWins;
        this.totalKills = totalKills;
        this.totalDeaths = totalDeaths;
        this.maxKillStreak = maxKillStreak;
        this.winRate = totalMatches > 0 ? (double) totalWins / totalMatches * 100 : 0;
    }

    public int getTotalMatches() { return totalMatches; }
    public int getTotalWins() { return totalWins; }
    public double getWinRate() { return winRate; }
    public int getMaxKillStreak() { return maxKillStreak; }
    public int getTotalKills() { return totalKills; }
    public int getTotalDeaths() { return totalDeaths; }
}
