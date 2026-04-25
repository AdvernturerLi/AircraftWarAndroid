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
    private static final String BASE_URL = Config.BASE_URL + "/rank";
    private static final String USER_URL = Config.BASE_URL + "/user";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface OnResponseListener<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    // --- 用户相关接口 ---

    public void login(String username, String password, OnResponseListener<UserProfile> listener) {
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(USER_URL + "/login").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { listener.onFailure(e.getMessage()); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = gson.fromJson(response.body().string(), UserProfile.class);
                    if (profile != null) listener.onSuccess(profile);
                    else listener.onFailure("用户名或密码错误");
                } else {
                    listener.onFailure("登录失败: " + response.code());
                }
            }
        });
    }

    public void register(String username, String password, OnResponseListener<String> listener) {
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(USER_URL + "/register").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { listener.onFailure(e.getMessage()); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    if ("USER_EXISTS".equals(res)) listener.onFailure("用户名已存在");
                    else listener.onSuccess("注册成功");
                } else {
                    listener.onFailure("注册失败: " + response.code());
                }
            }
        });
    }

    /**
     * 更新用户个性签名
     */
    public void updateSignature(String username, String signature, OnResponseListener<String> listener) {
        String json = String.format("{\"username\":\"%s\", \"signature\":\"%s\"}", username, signature);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(USER_URL + "/updateSignature").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { listener.onFailure(e.getMessage()); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) listener.onSuccess("更新成功");
                else listener.onFailure("更新失败");
            }
        });
    }

    /**
     * 获取对战历史记录
     */
    public void getMatchHistory(String username, OnResponseListener<List<MatchRecord>> listener) {
        Request request = new Request.Builder().url(USER_URL + "/history?username=" + username).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { listener.onFailure(e.getMessage()); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    List<MatchRecord> list = gson.fromJson(response.body().string(), new TypeToken<List<MatchRecord>>(){}.getType());
                    listener.onSuccess(list);
                } else {
                    listener.onFailure("历史记录获取失败");
                }
            }
        });
    }

    // --- 排行榜相关接口 ---

    public void uploadScore(ScoreRecord record, OnResponseListener<String> listener) {
        String json = gson.toJson(record);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(BASE_URL + "/upload").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { listener.onFailure(e.getMessage()); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) listener.onSuccess("上传成功");
                else listener.onFailure("上传失败: " + response.code());
            }
        });
    }

    public void getRankList(String type, int difficulty, OnResponseListener<List<ScoreRecord>> listener) {
        Request request = new Request.Builder().url(BASE_URL + "/list?type=" + type + "&difficulty=" + difficulty).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { listener.onFailure(e.getMessage()); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    List<ScoreRecord> list = gson.fromJson(response.body().string(), new TypeToken<List<ScoreRecord>>(){}.getType());
                    listener.onSuccess(list);
                } else {
                    listener.onFailure("获取失败");
                }
            }
        });
    }

    public void deleteScore(ScoreRecord record, OnResponseListener<String> listener) {
        String json = gson.toJson(record);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(BASE_URL + "/delete").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { listener.onFailure(e.getMessage()); }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) listener.onSuccess("删除成功");
                else listener.onFailure("删除失败");
            }
        });
    }
}
