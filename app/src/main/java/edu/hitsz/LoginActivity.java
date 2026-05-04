package edu.hitsz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import edu.hitsz.data.Config;
import edu.hitsz.data.NetworkManager;
import edu.hitsz.data.UserProfile;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Config.loadSession(this);
        if (Config.isLoggedIn) {
            openMainPage();
            return;
        }

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        networkManager = new NetworkManager();

        findViewById(R.id.btn_login).setOnClickListener(v -> performLogin());
        findViewById(R.id.btn_to_register).setOnClickListener(v -> performRegister());
        
        // 访客模式点击事件
        findViewById(R.id.btn_guest_mode).setOnClickListener(v -> enterGuestMode());
    }

    private void performLogin() {
        String username = getInputText(etUsername);
        String password = getInputText(etPassword);

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        networkManager.login(username, password, new NetworkManager.OnResponseListener<>() {
            @Override
            public void onSuccess(UserProfile profile) {
                runOnUiThread(() -> {
                    String nickname = profile != null && profile.getNickname() != null && !profile.getNickname().trim().isEmpty()
                            ? profile.getNickname().trim()
                            : username;
                    String signature = profile != null && profile.getSignature() != null && !profile.getSignature().trim().isEmpty()
                            ? profile.getSignature().trim()
                            : Config.userSignature;
                    String registerTime = profile != null && profile.getRegisterTime() != null && !profile.getRegisterTime().trim().isEmpty()
                            ? profile.getRegisterTime().trim()
                            : Config.registerTime;
                    Config.saveSession(LoginActivity.this, true, nickname, signature, registerTime);
                    Toast.makeText(LoginActivity.this, "欢迎回来, " + Config.getCurrentUserName(), Toast.LENGTH_SHORT).show();
                    openMainPage();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "登录失败: " + error, Toast.LENGTH_SHORT).show();
                    if (isNetworkUnavailable(error)) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("服务器暂不可用")
                                .setMessage("当前无法连接登录服务器，是否直接以离线访客模式进入游戏？")
                                .setPositiveButton("离线进入", (dialog, which) -> enterGuestMode())
                                .setNegativeButton("继续留在登录页", null)
                                .show();
                    }
                });
            }
        });
    }

    private void performRegister() {
        String username = getInputText(etUsername);
        String password = getInputText(etPassword);

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        networkManager.register(username, password, new NetworkManager.OnResponseListener<>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "注册失败: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void enterGuestMode() {
        Config.saveSession(this, false, "访客");
        openMainPage();
    }

    private void openMainPage() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private boolean isNetworkUnavailable(String error) {
        if (error == null) {
            return false;
        }
        String lower = error.toLowerCase();
        return lower.contains("failed to connect")
                || lower.contains("connect exception")
                || lower.contains("unknown host")
                || lower.contains("timeout")
                || lower.contains("connection refused")
                || lower.contains("econnrefused")
                || lower.contains("socket");
    }

    private String getInputText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
