package edu.hitsz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.time.LocalDateTime;

import edu.hitsz.application.Game;
import edu.hitsz.application.SoundManager;
import edu.hitsz.data.Config;
import edu.hitsz.data.NetworkManager;
import edu.hitsz.data.ScoreDao;
import edu.hitsz.data.ScoreDaoImpl;
import edu.hitsz.data.ScoreRecord;
import edu.hitsz.data.WebSocketManager;

public class MainActivity extends AppCompatActivity {

    private SoundManager soundManager;
    private boolean isMusicEnabled = true;
    private Game game;
    private ScoreDao scoreDao;
    private NetworkManager networkManager;
    private WebSocketManager wsManager;
    private int currentDifficulty = 1;
    private boolean isMultiplayer = false;

    // UI Elements
    private LinearLayout modeLayout;
    private LinearLayout difficultyLayout;
    private TextView tvModeSelected;
    private ProgressDialog matchDialog;
    private AlertDialog matchConfirmDialog;

    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.loadSession(this);
        scoreDao = new ScoreDaoImpl(this);
        networkManager = new NetworkManager();
        wsManager = new WebSocketManager();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (game != null) {
                    gameStop();
                } else if (difficultyLayout != null && difficultyLayout.getVisibility() == View.VISIBLE) {
                    showModeSelection();
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

        modeLayout = findViewById(R.id.mode_layout);
        difficultyLayout = findViewById(R.id.difficulty_layout);
        tvModeSelected = findViewById(R.id.tv_mode_selected);

        findViewById(R.id.btn_single_player).setOnClickListener(v -> {
            isMultiplayer = false;
            showDifficultySelection("模式：单人模式");
        });

        findViewById(R.id.btn_multi_player).setOnClickListener(v -> {
            if (!Config.isLoggedIn) {
                Toast.makeText(this, "联机对战需要先登录", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                // 不直接 finish，允许用户从登录页返回
                return;
            }
            isMultiplayer = true;
            showDifficultySelection("模式：联机对战");
        });

        findViewById(R.id.btn_easy).setOnClickListener(v -> prepareStart(1));
        findViewById(R.id.btn_normal).setOnClickListener(v -> prepareStart(2));
        findViewById(R.id.btn_hard).setOnClickListener(v -> prepareStart(3));
        findViewById(R.id.btn_back_to_mode).setOnClickListener(v -> showModeSelection());

        findViewById(R.id.btn_rank).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RankActivity.class)));
        findViewById(R.id.btn_profile).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        SwitchCompat switchMusic = findViewById(R.id.switch_music);
        switchMusic.setChecked(isMusicEnabled);
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> isMusicEnabled = isChecked);
        
        // ...existing code...
        // 统一显示当前用户名
        TextView tvTitle = findViewById(R.id.game_title);
        tvTitle.setText(getString(R.string.title_with_user, Config.getCurrentUserName()));

        // 处理主界面的退出登录按钮
        findViewById(R.id.btn_logout_main).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("确认退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确认", (dialog, which) -> {
                        Config.clearSession(this);
                        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finishAffinity();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        // 根据登录状态显示/隐藏退出按钮
        findViewById(R.id.btn_logout_main).setVisibility(Config.isLoggedIn ? View.VISIBLE : View.GONE);
    }

    private void showDifficultySelection(String modeTitle) {
        modeLayout.setVisibility(View.GONE);
        difficultyLayout.setVisibility(View.VISIBLE);
        tvModeSelected.setText(modeTitle);
    }

    private void showModeSelection() {
        difficultyLayout.setVisibility(View.GONE);
        modeLayout.setVisibility(View.VISIBLE);
    }

    private void prepareStart(int difficulty) {
        currentDifficulty = difficulty;
        if (isMultiplayer) {
            startMatchmaking();
        } else {
            startGame(difficulty);
        }
    }

    private void startMatchmaking() {
        matchDialog = new ProgressDialog(this);
        matchDialog.setMessage("正在匹配对手 [" + getDiffName(currentDifficulty) + "]...");
        matchDialog.setCancelable(true);
        matchDialog.setOnCancelListener(dialog -> {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            wsManager.close();
        });
        matchDialog.show();

        timeoutRunnable = () -> {
            if (matchDialog != null && matchDialog.isShowing()) {
                matchDialog.dismiss();
                wsManager.close();
                Toast.makeText(this, "匹配超时，请检查服务器连接", Toast.LENGTH_LONG).show();
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 15000);

        // 使用统一的用户名进行联机
        wsManager.connect(currentDifficulty, Config.getCurrentUserName());

        wsManager.setOnMessageListener(new WebSocketManager.OnMessageListener() {
            @Override
            public void onMatchFound(String opponentName) {
                runOnUiThread(() -> {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    if (matchDialog != null && matchDialog.isShowing()) {
                        matchDialog.dismiss();
                    }
                    showMatchConfirmDialog(opponentName);
                });
            }

            @Override
            public void onRealStart() {
                runOnUiThread(() -> {
                    if (matchConfirmDialog != null && matchConfirmDialog.isShowing()) {
                        matchConfirmDialog.dismiss();
                    }
                    startGame(currentDifficulty);
                });
            }

            @Override public void onOpponentScore(int score) {}
            @Override public void onOpponentDead() {}
            @Override public void onGameOver(int p1Score, int p2Score) {}

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    if (matchDialog != null && matchDialog.isShowing()) matchDialog.dismiss();
                    if (matchConfirmDialog != null && matchConfirmDialog.isShowing()) matchConfirmDialog.dismiss();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showMatchConfirmDialog(String opponentName) {
        matchConfirmDialog = new AlertDialog.Builder(this)
                .setTitle("匹配成功！")
                .setMessage("对手：" + opponentName + "\n难度：" + getDiffName(currentDifficulty) + "\n准备好开始对战了吗？")
                .setPositiveButton("准备就绪", null)
                .setNegativeButton("退出", (dialog, which) -> wsManager.close())
                .setCancelable(false)
                .create();

        matchConfirmDialog.show();

        matchConfirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            wsManager.sendReady();
            matchConfirmDialog.setMessage("等待对方准备...");
            v.setEnabled(false);
        });
    }

    private String getDiffName(int diff) {
        if (diff == 1) return "简单";
        if (diff == 3) return "困难";
        return "普通";
    }


    private void startGame(int difficulty) {
        game = new Game(this, difficulty, scoreDao, soundManager, isMusicEnabled, isMultiplayer, wsManager);
        game.setOnGameOverListener(new Game.GameHolder() {
            @Override
            public void onGameOver(int score) {
                runOnUiThread(() -> { gameStop(); showScoreDialog(score); });
            }
            @Override
            public void onMultiplayerGameOver(int myScore, int opponentScore) {
                runOnUiThread(() -> { gameStop(); showMultiplayerResult(myScore, opponentScore); });
            }
        });
        setContentView(game);
        if (isMusicEnabled) soundManager.playBgm();
    }

    private void gameStop() {
        if (soundManager != null) soundManager.stopAll();
        game = null;
        showMenu();
    }

    private void showMultiplayerResult(int myScore, int opponentScore) {
        String res = myScore > opponentScore ? "你赢了！" : (myScore < opponentScore ? "你输了..." : "平局");
        new AlertDialog.Builder(this)
                .setTitle("对战结果")
                .setMessage(res + "\n你的得分: " + myScore + "\n对手得分: " + opponentScore)
                .setPositiveButton("保存成绩", (dialog, which) -> {
                    if (wsManager != null) wsManager.close();
                    showScoreDialog(myScore);
                })
                .setCancelable(false)
                .show();
    }

    private void showScoreDialog(int score) {
        if (Config.isLoggedIn) {
            // 已登录用户：自动使用用户名，不再弹输入框
            new AlertDialog.Builder(this)
                    .setTitle("游戏结束")
                    .setMessage(Config.getCurrentUserName() + "，你的最终得分: " + score + "\n记录已自动保存。")
                    .setPositiveButton("查看排行榜", (dialog, which) -> {
                        saveAndUploadScore(Config.getCurrentUserName(), score);
                    })
                    .setCancelable(false)
                    .show();
        } else {
            // 访客模式：允许输入名字
            final EditText editText = new EditText(this);
            editText.setText("访客");
            editText.setSelectAllOnFocus(true);
            new AlertDialog.Builder(this)
                    .setTitle("保存成绩")
                    .setMessage("你的得分: " + score + "\n请输入昵称：")
                    .setView(editText)
                    .setPositiveButton("确定", (dialog, which) -> {
                        String name = editText.getText().toString().trim();
                        if (name.isEmpty()) name = "访客";
                        saveAndUploadScore(name, score);
                    })
                    .setNegativeButton("不保存", (dialog, which) -> {
                        startActivity(new Intent(MainActivity.this, RankActivity.class));
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void saveAndUploadScore(String name, int score) {
        String safeName = (name == null || name.trim().isEmpty()) ? Config.getCurrentUserName() : name.trim();
        ScoreRecord record = new ScoreRecord(safeName, score, LocalDateTime.now(), currentDifficulty);
        scoreDao.addScore(record);
        
        // 只有登录用户才尝试上传云端
        if (Config.isLoggedIn) {
            networkManager.uploadScore(record, new NetworkManager.OnResponseListener<>() {
                @Override
                public void onSuccess(String data) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "云端同步成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, RankActivity.class));
                    });
                }
                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "本地已保存 (服务器离线)", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, RankActivity.class));
                    });
                }
            });
        } else {
            Toast.makeText(this, "记录已保存到本地", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, RankActivity.class));
        }
    }

    @Override
    protected void onPause() { super.onPause(); if (soundManager != null) soundManager.stopAll(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeCallbacks(timeoutRunnable);
        if (wsManager != null) wsManager.close();
        if (soundManager != null) soundManager.release();
    }
}
