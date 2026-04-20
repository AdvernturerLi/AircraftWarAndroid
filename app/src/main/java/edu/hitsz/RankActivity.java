package edu.hitsz;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import edu.hitsz.data.NetworkManager;
import edu.hitsz.data.RankAdapter;
import edu.hitsz.data.ScoreDao;
import edu.hitsz.data.ScoreDaoImpl;
import edu.hitsz.data.ScoreRecord;
import java.util.List;
import java.util.stream.Collectors;

public class RankActivity extends AppCompatActivity {

    private ScoreDao scoreDao;
    private RankAdapter adapter;
    private NetworkManager networkManager;
    private String currentType = "all";
    private int currentDifficulty = 0; // 0: 全部, 1: Easy, 2: Normal, 3: Hard

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        scoreDao = new ScoreDaoImpl(this);
        networkManager = new NetworkManager();
        
        RecyclerView recyclerView = findViewById(R.id.rank_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new RankAdapter(scoreDao.getAllScores());
        adapter.setOnItemDeleteListener((record, position) -> deleteScoreRecord(record));
        recyclerView.setAdapter(adapter);

        // 类型切换 (总榜/周榜/本地)
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggle_group);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_all_rank) currentType = "all";
                else if (checkedId == R.id.btn_weekly_rank) currentType = "weekly";
                else if (checkedId == R.id.btn_local_rank) currentType = "local";
                refreshCurrentList();
            }
        });

        // 难度切换
        MaterialButtonToggleGroup difficultyGroup = findViewById(R.id.difficulty_toggle_group);
        difficultyGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_diff_all) currentDifficulty = 0;
                else if (checkedId == R.id.btn_diff_easy) currentDifficulty = 1;
                else if (checkedId == R.id.btn_diff_normal) currentDifficulty = 2;
                else if (checkedId == R.id.btn_diff_hard) currentDifficulty = 3;
                refreshCurrentList();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            scoreDao.clearScores();
            refreshCurrentList();
            Toast.makeText(this, "本地记录已清空", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteScoreRecord(ScoreRecord record) {
        scoreDao.removeScore(record);
        networkManager.deleteScore(record, new NetworkManager.OnResponseListener<String>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> {
                    Toast.makeText(RankActivity.this, "已同步云端删除", Toast.LENGTH_SHORT).show();
                    refreshCurrentList();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RankActivity.this, "云端删除失败: " + error, Toast.LENGTH_SHORT).show();
                    refreshCurrentList();
                });
            }
        });
    }

    private void refreshCurrentList() {
        if ("local".equals(currentType)) {
            List<ScoreRecord> localScores = scoreDao.getAllScores();
            if (currentDifficulty != 0) {
                localScores = localScores.stream()
                        .filter(s -> s.getDifficulty() == currentDifficulty)
                        .collect(Collectors.toList());
            }
            adapter.updateData(localScores);
        } else {
            fetchGlobalRank(currentType, currentDifficulty);
        }
    }

    private void fetchGlobalRank(String type, int difficulty) {
        networkManager.getRankList(type, difficulty, new NetworkManager.OnResponseListener<List<ScoreRecord>>() {
            @Override
            public void onSuccess(List<ScoreRecord> data) {
                runOnUiThread(() -> adapter.updateData(data));
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RankActivity.this, "获取排名失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}