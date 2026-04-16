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

public class RankActivity extends AppCompatActivity {

    private ScoreDao scoreDao;
    private RankAdapter adapter;
    private NetworkManager networkManager;
    private String currentType = "local";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        scoreDao = new ScoreDaoImpl(this);
        networkManager = new NetworkManager();
        
        RecyclerView recyclerView = findViewById(R.id.rank_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new RankAdapter(scoreDao.getAllScores());
        adapter.setOnItemDeleteListener((record, position) -> {
            // 执行删除逻辑
            deleteScoreRecord(record);
        });
        recyclerView.setAdapter(adapter);

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggle_group);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_all_rank) {
                    currentType = "all";
                    fetchGlobalRank("all");
                } else if (checkedId == R.id.btn_weekly_rank) {
                    currentType = "weekly";
                    fetchGlobalRank("weekly");
                } else if (checkedId == R.id.btn_local_rank) {
                    currentType = "local";
                    adapter.updateData(scoreDao.getAllScores());
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            scoreDao.clearScores();
            if ("local".equals(currentType)) {
                adapter.updateData(scoreDao.getAllScores());
            }
            Toast.makeText(this, "本地记录已清空", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteScoreRecord(ScoreRecord record) {
        // 1. 从本地删除
        scoreDao.removeScore(record);
        
        // 2. 从云端删除
        networkManager.deleteScore(record, new NetworkManager.OnResponseListener<String>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> {
                    Toast.makeText(RankActivity.this, "云端同步删除成功", Toast.LENGTH_SHORT).show();
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
            adapter.updateData(scoreDao.getAllScores());
        } else {
            fetchGlobalRank(currentType);
        }
    }

    private void fetchGlobalRank(String type) {
        networkManager.getRankList(type, new NetworkManager.OnResponseListener<List<ScoreRecord>>() {
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