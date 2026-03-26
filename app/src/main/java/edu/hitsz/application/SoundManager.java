package edu.hitsz.application;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import edu.hitsz.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 音效管理器：使用 SoundPool 播放短音效，MediaPlayer 播放 BGM。
 */
public class SoundManager {

    private boolean soundOn = true;
    private Context context;

    private SoundPool soundPool;
    private Map<Integer, Integer> soundMap = new HashMap<>();

    private MediaPlayer bgmPlayer;
    private MediaPlayer bossBgmPlayer;

    // 音效资源路径常量（为了兼容原有代码中的路径字符串，这里保留并映射到资源 ID）
    public static final String BOMB_PATH = "bomb";
    public static final String PROP_PATH = "prop";
    public static final String GAME_OVER_PATH = "game_over";
    public static final String BULLET_HIT_PATH = "bullet_hit";
    public static final String BULLET_PATH = "bullet";

    public SoundManager(Context context) {
        this.context = context;

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attributes)
                .build();

        // 预加载短音效 (假设资源存在)
        loadSound(BOMB_PATH, R.raw.bomb_explosion);
        loadSound(PROP_PATH, R.raw.get_supply);
        loadSound(GAME_OVER_PATH, R.raw.game_over);
        loadSound(BULLET_HIT_PATH, R.raw.bullet_hit);
        loadSound(BULLET_PATH, R.raw.bullet);

        // BGM 使用 MediaPlayer
        try {
            bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
            if (bgmPlayer != null) {
                bgmPlayer.setLooping(true);
            }

            bossBgmPlayer = MediaPlayer.create(context, R.raw.bgm_boss);
            if (bossBgmPlayer != null) {
                bossBgmPlayer.setLooping(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSound(String key, int resId) {
        try {
            int id = soundPool.load(context, resId, 1);
            soundMap.put(key.hashCode(), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
        if (!soundOn) {
            stopAll();
        }
    }

    public synchronized void playBgm() {
        if (!soundOn || bgmPlayer == null) return;
        stopBossBgm();
        if (!bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    public synchronized void playBossBgm() {
        if (!soundOn || bossBgmPlayer == null) return;
        stopBgm();
        if (!bossBgmPlayer.isPlaying()) {
            bossBgmPlayer.start();
        }
    }

    public synchronized void playSound(String path) {
        if (!soundOn || path == null) return;
        Integer soundId = soundMap.get(path.hashCode());
        if (soundId != null) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    public synchronized void stopBgm() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
            bgmPlayer.seekTo(0);
        }
    }

    public synchronized void stopBossBgm() {
        if (bossBgmPlayer != null && bossBgmPlayer.isPlaying()) {
            bossBgmPlayer.pause();
            bossBgmPlayer.seekTo(0);
        }
    }

    public synchronized void stopAll() {
        stopBgm();
        stopBossBgm();
    }

    public void release() {
        stopAll();
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (bossBgmPlayer != null) {
            bossBgmPlayer.release();
            bossBgmPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
