package edu.hitsz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.time.LocalDateTime;

import edu.hitsz.application.Game;
import edu.hitsz.application.SoundManager;
import edu.hitsz.data.NetworkManager;
import edu.hitsz.data.ScoreDao;
import edu.hitsz.data.ScoreDaoImpl;
import edu.hitsz.data.ScoreRecord;

public class MainActivity extends AppCompatActivity {

    private SoundManager soundManager;
    private boolean isMusicEnabled = true;
    private Game game;
    private ScoreDao scoreDao;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scoreDao = new ScoreDaoImpl(this);
        networkManager = new NetworkManager();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (game != null) {
                    if (soundManager != null) {
                        soundManager.stopAll();
                    }
                    game = null;
                    showMenu();
                } else {
                    finish();
                }
            }
        });

        showMenu();
    }

    private void showMenu() {
        setContentView(R.layout.activity_main);

        soundManager = new SoundManager(this);
        Button btnEasy = findViewById(R.id.btn_easy);
        Button btnNormal = findViewById(R.id.btn_normal);
        Button btnHard = findViewById(R.id.btn_hard);
        Button btnRank = findViewById(R.id.btn_rank);
        Button btnProfile = findViewById(R.id.btn_profile);
        SwitchCompat switchMusic = findViewById(R.id.switch_music);

        switchMusic.setChecked(isMusicEnabled);
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isMusicEnabled = isChecked;
        });

        btnEasy.setOnClickListener(v -> startGame(1));
        btnNormal.setOnClickListener(v -> startGame(2));
        btnHard.setOnClickListener(v -> startGame(3));
        btnRank.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RankActivity.class);
            startActivity(intent);
        });
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void startGame(int difficulty) {
        game = new Game(this, difficulty, scoreDao, soundManager, isMusicEnabled);

        game.setOnGameOverListener(score -> {
            runOnUiThread(() -> {
                if (soundManager != null) soundManager.stopAll();
                showScoreDialog(score);
            });
        });

        setContentView(game);

        if (isMusicEnabled) {
            soundManager.playBgm();
        }
    }

    private void showScoreDialog(int score) {
        final EditText editText = new EditText(this);
        editText.setHint("输入你的名字");
        
        new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage("你的最终得分是: " + score)
                .setView(editText)
                .setPositiveButton("保存成绩并上传", (dialog, which) -> {
                    String userName = editText.getText().toString().trim();
                    if (userName.isEmpty()) {
                        userName = "匿名玩家";
                    }
                    ScoreRecord record = new ScoreRecord(userName, score, LocalDateTime.now());
                    
                    // 1. 保存到本地
                    scoreDao.addScore(record);
                    
                    // 2. 上传到服务器
                    uploadScoreToServer(record);
                    
                    // 跳转到排行榜
                    game = null;
                    showMenu();
                    Intent intent = new Intent(MainActivity.this, RankActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    game = null;
                    showMenu();
                })
                .setCancelable(false)
                .show();
    }

    private void uploadScoreToServer(ScoreRecord record) {
        networkManager.uploadScore(record, new NetworkManager.OnResponseListener<String>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "云端同步成功", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "云端同步失败: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundManager != null) soundManager.stopAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (game != null && isMusicEnabled && soundManager != null) {
            soundManager.playBgm();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}