package edu.hitsz.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {
    private static final String BASE_URL = "http://5d700464.r23.cpolar.top/api/rank";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface OnResponseListener<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    public void uploadScore(ScoreRecord record, OnResponseListener<String> listener) {
        String json = gson.toJson(record);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/upload")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.onSuccess("上传成功");
                } else {
                    listener.onFailure("上传失败: " + response.code());
                }
            }
        });
    }

    /**
     * 获取排行榜
     * @param type "all" 或 "weekly"
     * @param difficulty 0: 全部, 1: 简单, 2: 普通, 3: 困难
     */
    public void getRankList(String type, int difficulty, OnResponseListener<List<ScoreRecord>> listener) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/list?type=" + type + "&difficulty=" + difficulty)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    List<ScoreRecord> list = gson.fromJson(json, new TypeToken<List<ScoreRecord>>(){}.getType());
                    listener.onSuccess(list);
                } else {
                    listener.onFailure("获取失败: " + response.code());
                }
            }
        });
    }

    public void deleteScore(ScoreRecord record, OnResponseListener<String> listener) {
        String json = gson.toJson(record);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/delete")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.onSuccess("删除成功");
                } else {
                    listener.onFailure("服务端删除失败: " + response.code());
                }
            }
        });
    }
}