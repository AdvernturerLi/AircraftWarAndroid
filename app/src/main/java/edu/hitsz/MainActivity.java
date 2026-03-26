package edu.hitsz;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import edu.hitsz.application.Game;
import edu.hitsz.application.SoundManager;
import edu.hitsz.data.ScoreDaoImpl;

public class MainActivity extends AppCompatActivity {

    private SoundManager soundManager;
    private boolean isMusicEnabled = true;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 【核心修改点】注册返回键监听器
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 如果当前正在游戏界面
                if (game != null) {
                    // 1. 停止音效
                    if (soundManager != null) {
                        soundManager.stopAll();
                    }
                    // 2. 清除游戏实例
                    game = null;
                    // 3. 返回菜单布局
                    showMenu();
                } else {
                    // 如果已经在菜单界面，直接结束当前 Activity（退出 App）
                    finish();
                }
            }
        });

        showMenu();
    }

    private void showMenu() {
        setContentView(R.layout.activity_main);

        // 每次显示菜单时重新初始化/绑定
        soundManager = new SoundManager(this);
        Button btnEasy = findViewById(R.id.btn_easy);
        Button btnNormal = findViewById(R.id.btn_normal);
        Button btnHard = findViewById(R.id.btn_hard);
        SwitchCompat switchMusic = findViewById(R.id.switch_music);

        switchMusic.setChecked(isMusicEnabled);
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isMusicEnabled = isChecked;
        });

        btnEasy.setOnClickListener(v -> startGame(1));
        btnNormal.setOnClickListener(v -> startGame(2));
        btnHard.setOnClickListener(v -> startGame(3));
    }

    private void startGame(int difficulty) {
        game = new Game(this, difficulty, new ScoreDaoImpl(), soundManager, isMusicEnabled);

        game.setOnGameOverListener(score -> {
            runOnUiThread(() -> {
                if (soundManager != null) soundManager.stopAll();
                Toast.makeText(MainActivity.this, "游戏结束！得分：" + score, Toast.LENGTH_LONG).show();
                game = null; // 游戏结束也要清理引用
                showMenu();
            });
        });

        setContentView(game);

        if (isMusicEnabled) {
            soundManager.playBgm();
        }
    }

    // 【删除点】请务必删除你代码里自定义的 OnBackPressedDispatcher() 方法
    // 因为它不是系统回调，反而会干扰逻辑

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