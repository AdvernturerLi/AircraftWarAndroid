package edu.hitsz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
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

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        networkManager = new NetworkManager();

        findViewById(R.id.btn_login).setOnClickListener(v -> performLogin());

        findViewById(R.id.btn_to_register).setOnClickListener(v -> performRegister());
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        networkManager.login(username, password, new NetworkManager.OnResponseListener<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                runOnUiThread(() -> {
                    Config.userNickname = profile.getNickname();
                    // 可以将 profile 存入本地以实现下次免登录，此处先实现基本逻辑
                    Toast.makeText(LoginActivity.this, "欢迎回来, " + Config.userNickname, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录失败: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void performRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        networkManager.register(username, password, new NetworkManager.OnResponseListener<String>() {
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
}
