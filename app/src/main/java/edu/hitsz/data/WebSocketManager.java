package edu.hitsz.data;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    // 使用你内网穿透的域名，注意协议是 ws:// 或 wss://
    private static final String WS_URL = Config.WS_URL;
    private final OkHttpClient client = new OkHttpClient();
    private WebSocket webSocket;
    private final Gson gson = new Gson();
    private OnMessageListener listener;

    public interface OnMessageListener {
        void onMatchFound(String opponentName); // 原来的 onGameStart 改名为这个，表示发现对手
        void onRealStart();                     // 新增：表示真正的对战开始

        void onOpponentScore(int score);
        void onOpponentDead();
        void onGameOver(int p1Score, int p2Score);
        
        void onError(String message); // 新增错误回调
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    public void connect(int difficulty, String username) {
        String rawUsername = (username == null || username.trim().isEmpty()) ? "访客" : username.trim();
        String safeUsername;
        try {
            safeUsername = URLEncoder.encode(rawUsername, StandardCharsets.UTF_8);
        } catch (Exception e) {
            safeUsername = rawUsername;
        }
        String url = WS_URL + "?difficulty=" + difficulty + "&username=" + safeUsername;
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                handleMessage(text);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                t.printStackTrace();
                if (listener != null) {
                    listener.onError("连接服务器失败: " + t.getMessage());
                }
            }
            
            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                if (listener != null && code != 1000) {
                    listener.onError("连接已关闭: " + reason);
                }
            }
        });
    }

    private void handleMessage(String text) {
        if (listener == null) return;
        try {
            Map<String, Object> data = gson.fromJson(text, Map.class);
            String type = (String) data.get("type");
            if (type == null) {
                return;
            }

            switch (type) {
                case "START": // 服务器匹配成功
                    listener.onMatchFound((String) data.get("opponent"));
                    break;
                case "START_GAME": // 服务器确认双方都已准备
                    listener.onRealStart();
                    break;
                case "SCORE":
                    listener.onOpponentScore(toInt(data.get("score")));
                    break;
                case "DEAD":
                    listener.onOpponentDead();
                    break;
                case "END":
                    int s1 = toInt(data.get("p1_score"));
                    int s2 = toInt(data.get("p2_score"));
                    listener.onGameOver(s1, s2);
                    break;
                case "ERROR":
                    listener.onError((String) data.get("message"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return value == null ? 0 : Integer.parseInt(String.valueOf(value));
        } catch (Exception ignored) {
            return 0;
        }
    }

    // 新增发送准备信号的方法
    public void sendReady() {
        if (webSocket != null) {
            webSocket.send("{\"type\":\"READY\"}");
        }
    }
    public void sendScore(int score) {
        if (webSocket != null) {
            webSocket.send("{\"type\":\"SCORE\", \"score\":" + score + "}");
        }
    }

    public void sendDead() {
        if (webSocket != null) {
            webSocket.send("{\"type\":\"DEAD\"}");
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Game Over");
            webSocket = null;
        }
    }
}
