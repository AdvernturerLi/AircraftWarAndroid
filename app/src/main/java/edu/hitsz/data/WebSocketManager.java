package edu.hitsz.data;

import com.google.gson.Gson;
import java.util.Map;
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
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    public void connect(int difficulty, String username) {
        String url = WS_URL + "?difficulty=" + difficulty + "&username=" + username;
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                handleMessage(text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
            }
        });
    }

    private void handleMessage(String text) {
        if (listener == null) return;
        Map<String, Object> data = gson.fromJson(text, Map.class);
        String type = (String) data.get("type");

        switch (type) {
            case "START": // 服务器匹配成功
                listener.onMatchFound((String) data.get("opponent"));
                break;
            case "START_GAME": // 服务器确认双方都已准备
                listener.onRealStart();
                break;
            case "SCORE":
                listener.onOpponentScore(((Double) data.get("score")).intValue());
                break;
            case "DEAD":
                listener.onOpponentDead();
                break;
            case "END":
                int s1 = ((Double) data.get("p1_score")).intValue();
                int s2 = ((Double) data.get("p2_score")).intValue();
                listener.onGameOver(s1, s2);
                break;
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
        }
    }
}