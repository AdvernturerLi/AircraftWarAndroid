package edu.hitsz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.hitsz.data.Config;
import edu.hitsz.data.MatchRecord;
import edu.hitsz.data.NetworkManager;
import edu.hitsz.data.ScoreDao;
import edu.hitsz.data.ScoreDaoImpl;
import edu.hitsz.data.ScoreRecord;

public class ProfileActivity extends AppCompatActivity {

    private ScoreDao scoreDao;
    private NetworkManager networkManager;
    private HistoryAdapter historyAdapter;
    private TextView tvSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Config.loadSession(this);
        scoreDao = new ScoreDaoImpl(this);
        networkManager = new NetworkManager();

        setupUI();
        loadData();
    }

    private void setupUI() {
        tvSignature = findViewById(R.id.tv_signature);
        
        // 点击签名进行修改
        tvSignature.setOnClickListener(v -> showEditSignatureDialog());

        RecyclerView rvHistory = findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        rvHistory.setAdapter(historyAdapter);

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            Config.clearSession(this);
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }

    private void showEditSignatureDialog() {
        if (!Config.isLoggedIn) {
            Toast.makeText(this, "离线访客模式下暂不支持云端签名", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditText editText = new EditText(this);
        editText.setText(tvSignature.getText());
        editText.setHint("输入你的新个性签名");

        new AlertDialog.Builder(this)
                .setTitle("修改个人描述")
                .setView(editText)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newSig = editText.getText().toString().trim();
                    if (!newSig.isEmpty()) {
                        updateSignatureOnServer(newSig);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateSignatureOnServer(String signature) {
        networkManager.updateSignature(Config.getCurrentUserName(), signature, new NetworkManager.OnResponseListener<>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> {
                    Config.saveSession(ProfileActivity.this, true, Config.getCurrentUserName(), signature, Config.registerTime);
                    tvSignature.setText(signature);
                    Toast.makeText(ProfileActivity.this, "描述已更新", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Config.saveSession(ProfileActivity.this, true, Config.getCurrentUserName(), signature, Config.registerTime);
                    tvSignature.setText(signature);
                    Toast.makeText(ProfileActivity.this, "云端更新失败，已本地保存", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadData() {
        TextView tvNickname = findViewById(R.id.tv_nickname);
        tvNickname.setText(Config.getCurrentUserName());
        ((TextView) findViewById(R.id.tv_signature)).setText(Config.isLoggedIn ? Config.userSignature : "离线访客模式，云端资料不可用");

        // 格式化加入时间显示
        String displayRegisterTime = formatRegisterTime(Config.registerTime);
        ((TextView) findViewById(R.id.tv_reg_date)).setText(Config.isLoggedIn
                ? "加入时间: " + displayRegisterTime
                : "加入时间: 离线模式");

        // 如果未登录，显示本地最高分
        if (!Config.isLoggedIn) {
            List<ScoreRecord> localScores = scoreDao.getAllScores();
            int maxScore = localScores.isEmpty() ? 0 : localScores.get(0).getScore();
            ((TextView) findViewById(R.id.tv_stat_streak)).setText(String.valueOf(maxScore));
            historyAdapter.updateData(new ArrayList<>());
            calculateMatchStats(new ArrayList<>());
            return;
        }

        // 已登录：从云端获取该用户的最高分
        loadUserHighestScore();

        networkManager.getMatchHistory(Config.getCurrentUserName(), new NetworkManager.OnResponseListener<>() {
            @Override
            public void onSuccess(List<MatchRecord> history) {
                runOnUiThread(() -> {
                    historyAdapter.updateData(history);
                    calculateMatchStats(history);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "战绩加载失败，已显示本地数据", Toast.LENGTH_SHORT).show();
                    historyAdapter.updateData(new ArrayList<>());
                    calculateMatchStats(new ArrayList<>());
                });
            }
        });
    }

    /**
     * 格式化注册时间显示
     */
    private String formatRegisterTime(String registerTime) {
        if (registerTime == null || registerTime.isEmpty()) {
            return "暂未获取";
        }
        try {
            // 尝试解析为 long 时间戳（毫秒）
            long timestamp = Long.parseLong(registerTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        } catch (NumberFormatException e) {
            // 如果不是时间戳，直接返回（可能已是格式化的日期）
            return registerTime;
        }
    }

    /**
     * 从云端排行榜获取用户最高分
     */
    private void loadUserHighestScore() {
        networkManager.getRankList("single", 2, new NetworkManager.OnResponseListener<>() {
            @Override
            public void onSuccess(List<ScoreRecord> rankList) {
                runOnUiThread(() -> {
                    int maxScore = 0;
                    for (ScoreRecord record : rankList) {
                        if (Config.getCurrentUserName().equals(record.getUserName())) {
                            maxScore = Math.max(maxScore, record.getScore());
                        }
                    }
                    ((TextView) findViewById(R.id.tv_stat_streak)).setText(String.valueOf(maxScore));
                });
            }

            @Override
            public void onFailure(String error) {
                // 云端获取失败，使用本地最高分作为备用
                runOnUiThread(() -> {
                    List<ScoreRecord> localScores = scoreDao.getAllScores();
                    int maxScore = localScores.isEmpty() ? 0 : localScores.get(0).getScore();
                    ((TextView) findViewById(R.id.tv_stat_streak)).setText(String.valueOf(maxScore));
                });
            }
        });
    }

    private void calculateMatchStats(List<MatchRecord> history) {
        int totalMatches = history.size();
        int wins = 0;
        for (MatchRecord r : history) {
            if ("WIN".equals(r.getResult(Config.getCurrentUserName()))) wins++;
        }
        double winRate = totalMatches == 0 ? 0 : (wins * 100.0 / totalMatches);

        ((TextView) findViewById(R.id.tv_stat_matches)).setText(String.valueOf(totalMatches));
        ((TextView) findViewById(R.id.tv_stat_wins)).setText(String.valueOf(wins));
        ((TextView) findViewById(R.id.tv_stat_winrate)).setText(String.format(Locale.getDefault(), "%.1f%%", winRate));
    }

    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<MatchRecord> records;
        HistoryAdapter(List<MatchRecord> records) { this.records = records; }
        void updateData(List<MatchRecord> newData) { this.records = newData; notifyDataSetChanged(); }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MatchRecord r = records.get(position);
            String res = r.getResult(Config.getCurrentUserName());
            holder.text1.setText(String.format("[%s] VS %s", res, r.getOpponent(Config.getCurrentUserName())));
            holder.text2.setText(String.format(Locale.getDefault(), "分数: %d - %d | 时间: %s", r.getMyScore(Config.getCurrentUserName()), r.getOpScore(Config.getCurrentUserName()), r.getTimeString()));
            if ("WIN".equals(res)) holder.text1.setTextColor(0xFF388E3C);
            else if ("LOSS".equals(res)) holder.text1.setTextColor(0xFFD32F2F);
        }

        @Override
        public int getItemCount() { return records.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            ViewHolder(View v) { super(v); text1 = v.findViewById(android.R.id.text1); text2 = v.findViewById(android.R.id.text2); }
        }
    }
}