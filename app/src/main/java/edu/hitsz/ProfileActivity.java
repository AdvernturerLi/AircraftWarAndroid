package edu.hitsz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.hitsz.data.GameStats;
import edu.hitsz.data.Medal;
import edu.hitsz.data.ScoreDao;
import edu.hitsz.data.ScoreDaoImpl;
import edu.hitsz.data.ScoreRecord;
import edu.hitsz.data.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        scoreDao = new ScoreDaoImpl(this);

        UserProfile profile = loadUserProfile();
        GameStats stats = calculateStats();
        List<Medal> achievements = loadAchievements(stats);

        setupProfileCard(profile);
        setupStatisticsPanel(stats);
        setupAchievementWall(achievements);
    }

    private UserProfile loadUserProfile() {
        List<ScoreRecord> scores = scoreDao.getAllScores();
        String name = scores.isEmpty() ? "New Pilot" : scores.get(0).getUserName();
        // Mock data for level and signature as they aren't in the current DB
        return new UserProfile(name, "Aim for the stars, even if you land among the clouds.", 12, "2024-03-01");
    }

    private GameStats calculateStats() {
        List<ScoreRecord> scores = scoreDao.getAllScores();
        int totalMatches = scores.size();
        int totalWins = 0;
        int maxScore = 0;
        int totalKills = 0;

        for (ScoreRecord r : scores) {
            // Logic to estimate "wins" and other stats from game records
            if (r.getScore() > 500) totalWins++;
            if (r.getScore() > maxScore) maxScore = r.getScore();
            totalKills += r.getScore() / 10; 
        }

        // Mocking deaths as matches * 1.5 for display purposes
        return new GameStats(totalMatches, totalWins, maxScore / 100, totalKills, totalMatches + (totalMatches/2));
    }

    private List<Medal> loadAchievements(GameStats stats) {
        List<Medal> medals = new ArrayList<>();
        // Define achievements based on stats
        medals.add(new Medal("Ace Pilot", "Down 500 enemies", R.drawable.prop_bullet, stats.getTotalKills() >= 500));
        medals.add(new Medal("Survivor", "Play 10 matches", R.drawable.prop_blood, stats.getTotalMatches() >= 10));
        medals.add(new Medal("Bomb Master", "High score achievement", R.drawable.prop_bomb, stats.getMaxKillStreak() >= 20));
        medals.add(new Medal("Elite Force", "Reach Level 10", R.drawable.elite, true));
        medals.add(new Medal("Sky Hero", "Score over 2000", R.drawable.hero, stats.getMaxKillStreak() >= 20));
        medals.add(new Medal("Boss Slayer", "Destroy Bosses", R.drawable.boss, stats.getTotalWins() >= 5));
        
        return medals;
    }

    private void setupProfileCard(UserProfile profile) {
        TextView tvNickname = findViewById(R.id.tv_nickname);
        TextView tvLevel = findViewById(R.id.tv_level);
        TextView tvSignature = findViewById(R.id.tv_signature);
        TextView tvRegDate = findViewById(R.id.tv_reg_date);

        tvNickname.setText(profile.getNickname());
        tvLevel.setText(String.format(Locale.getDefault(), "Lv. %d", profile.getLevel()));
        tvSignature.setText(profile.getSignature());
        tvRegDate.setText(String.format(Locale.getDefault(), "Registered: %s", profile.getRegisterTime()));
    }

    private void setupStatisticsPanel(GameStats stats) {
        ((TextView) findViewById(R.id.tv_stat_matches)).setText(String.valueOf(stats.getTotalMatches()));
        ((TextView) findViewById(R.id.tv_stat_wins)).setText(String.valueOf(stats.getTotalWins()));
        ((TextView) findViewById(R.id.tv_stat_winrate)).setText(String.format(Locale.getDefault(), "%.1f%%", stats.getWinRate()));
        ((TextView) findViewById(R.id.tv_stat_streak)).setText(String.valueOf(stats.getMaxKillStreak()));
        ((TextView) findViewById(R.id.tv_stat_kills)).setText(String.valueOf(stats.getTotalKills()));
        ((TextView) findViewById(R.id.tv_stat_deaths)).setText(String.valueOf(stats.getTotalDeaths()));
    }

    private void setupAchievementWall(List<Medal> achievements) {
        RecyclerView rvMedals = findViewById(R.id.rv_medals);
        TextView tvProgress = findViewById(R.id.tv_achievement_progress);

        rvMedals.setLayoutManager(new GridLayoutManager(this, 3));
        
        int unlockedCount = 0;
        for (Medal m : achievements) {
            if (m.isUnlocked()) unlockedCount++;
        }
        tvProgress.setText(String.format(Locale.getDefault(), "%d / %d", unlockedCount, achievements.size()));

        rvMedals.setAdapter(new MedalGridAdapter(achievements));
    }

    private static class MedalGridAdapter extends RecyclerView.Adapter<MedalGridAdapter.ViewHolder> {
        private final List<Medal> medals;

        MedalGridAdapter(List<Medal> medals) { this.medals = medals; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medal, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Medal medal = medals.get(position);
            holder.tvName.setText(medal.getName());
            holder.ivIcon.setImageResource(medal.getIconResId());
            
            if (medal.isUnlocked()) {
                holder.ivIcon.setAlpha(1.0f);
            } else {
                holder.ivIcon.setAlpha(0.2f);
                holder.tvName.setTextColor(0xFF9E9E9E);
            }
        }

        @Override
        public int getItemCount() { return medals.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvName;

            ViewHolder(View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_medal_icon);
                tvName = itemView.findViewById(R.id.tv_medal_name);
                // Hide description in grid wall for cleaner appearance
                View desc = itemView.findViewById(R.id.tv_medal_desc);
                if (desc != null) desc.setVisibility(View.GONE);
            }
        }
    }
}
