package edu.hitsz.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import edu.hitsz.aircraft.*;
import edu.hitsz.aircraft.observer.BombSubscriber;
import edu.hitsz.aircraft.shoot.StraightShoot;
import edu.hitsz.application.difficulty.DifficultyTemplate;
import edu.hitsz.application.difficulty.EasyDifficulty;
import edu.hitsz.application.difficulty.HardDifficulty;
import edu.hitsz.application.difficulty.NormalDifficulty;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.*;
import edu.hitsz.data.ScoreDao;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 * 重构为 SurfaceView 的子类
 *
 * @author hitsz
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private int backGroundTop = 0;

    /**
     * 时间间隔(ms)，控制刷新频率
     * 30ms 约为 33fps，改为 16ms 约为 60fps 提高流畅度
     */
    private int timeInterval = 16;

    // SurfaceHolder
    private SurfaceHolder mSurfaceHolder;
    // 绘制线程
    private Thread mThread;
    // 绘制标志位
    private volatile boolean isRunning;
    // 画笔
    private Paint mPaint;

    private int screenWidth;
    private int screenHeight;

    // 工厂模式实例
    private RandomEnemySpawner aircraftSpawner;
    private final RandomPropSpawner propSpawner = new RandomPropSpawner();
    private BossEnemyFactory bossFactory;

    // 单例模式实例
    private final HeroAircraft heroAircraft;

    // 游戏列表
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;

    private int enemyMaxNumber = 10;
    private volatile int score = 0;
    private int time = 0;

    private int spawnDuration = 960;
    private int shootDuration = 416; // 调整为 timeInterval 的倍数 16 * 26 = 416

    private int bossScoreThreshold = 200;
    private final int bossadd = 100;
    private int bossSpawnCount = 0;

    private final Random random = new Random();

    private volatile boolean gameOverFlag = false;

    private ScoreDao scoreDao;
    // Context or Activity reference
    private Context context;
    private SoundManager soundManager;
    private int difficulty;
    private int enemySpeedIncrement = 0;

    private int propDuration = 3000;
    private volatile long propEndTime = 0;
    private DifficultyTemplate difficultyTemplate;

    private boolean isMusicEnabled;

    public Game(Context context, int difficulty, ScoreDao scoreDao, SoundManager soundManager, boolean isMusicEnabled) {
        super(context);
        this.context = context;
        this.difficulty = difficulty;
        this.soundManager = soundManager;
        this.scoreDao = scoreDao;
        this.isMusicEnabled = isMusicEnabled;

        // 初始化 SurfaceHolder 和 Callback
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        // 单例模式 & 无参数方法
        this.heroAircraft = HeroAircraft.getInstance();
        this.heroAircraft.reset();

        // 设置触摸监听
        this.setOnTouchListener(new HeroController(heroAircraft));

        // 根据难度选择模板
        switch (difficulty) {
            case 1:
                this.difficultyTemplate = new EasyDifficulty();
                break;
            case 2:
                this.difficultyTemplate = new NormalDifficulty();
                break;
            case 3:
                this.difficultyTemplate = new HardDifficulty();
                break;
            default:
                this.difficultyTemplate = new NormalDifficulty();
        }
        // 应用难度设置
        difficultyTemplate.setupDifficulty();
        applyDifficultySettings();
        this.aircraftSpawner = new RandomEnemySpawner(
                difficultyTemplate.getMobEnemyHp(),
                difficultyTemplate.getEliteEnemyHp(),
                difficultyTemplate.getSuperEliteEnemyHp(),
                this
        );
        // 初始化Boss工厂
        this.bossFactory = new BossEnemyFactory(this);
        // 初始化列表
        this.enemyAircrafts = new CopyOnWriteArrayList<>();
        this.heroBullets = new CopyOnWriteArrayList<>();
        this.enemyBullets = new CopyOnWriteArrayList<>();
        this.props = new CopyOnWriteArrayList<>();
    }

    // 定义游戏监听器接口
    public interface GameHolder {
        void onGameOver(int score);
    }

    private GameHolder gameHolder;

    // 设置监听器的方法
    public void setOnGameOverListener(GameHolder holder) {
        this.gameHolder = holder;
    }

    private void applyDifficultySettings() {
        this.enemyMaxNumber = difficultyTemplate.getEnemyMaxNumber();
        this.shootDuration = difficultyTemplate.getShootDuration();
        this.propDuration = difficultyTemplate.getPropDuration();
        this.heroAircraft.setHp(difficultyTemplate.getHeroHp());
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // 加载图片 (假设 ImageManager.init 已经在 Activity 中调用，或者在此处调用)
        ImageManager.init(context);

        // 设置绘制标志位为 true
        isRunning = true;
        // 启动绘制线程
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // 获取屏幕宽高并同步更新 Main 常量
        this.screenWidth = width;
        this.screenHeight = height;
        Main.WINDOW_WIDTH = width;
        Main.WINDOW_HEIGHT = height;

        // 调整英雄机位置到屏幕底部中央
        heroAircraft.setLocation(width / 2.0, height - 150);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // 设置绘制标志位为 false
        isRunning = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            long startTime = System.currentTimeMillis();

            if (!gameOverFlag) {
                action();
            }

            draw();

            long endTime = System.currentTimeMillis();
            long diffTime = endTime - startTime;
            if (diffTime < timeInterval) {
                try {
                    Thread.sleep(timeInterval - diffTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void action() {
        // 安全检查：如果道具时间已过但策略未恢复
        if (propEndTime > 0 && System.currentTimeMillis() > propEndTime) {
            heroAircraft.setShootStrategy(new StraightShoot());
            propEndTime = 0;
        }

        time += timeInterval;
        checkAndIncreaseDifficulty();

        // 1. 射击周期判断
        if (time % shootDuration < timeInterval && time >= shootDuration) {
            shootAction();
        }

        // 2. 敌机生成周期判断
        if (time % spawnDuration < timeInterval && time >= spawnDuration) {
            if (enemyAircrafts.size() < enemyMaxNumber) {
                enemyAircrafts.add(aircraftSpawner.spawnEnemy());
            }

            // Boss 出现逻辑
            boolean hasBoss = enemyAircrafts.stream().anyMatch(e -> e instanceof BossEnemy);
            if (!hasBoss && score >= bossScoreThreshold && difficulty > 1) {
                bossSpawnCount++;
                int bossX = screenWidth / 2;
                int bossY = ImageManager.BOSS_ENEMY_IMAGE.getHeight() / 2;
                int bossHp = difficultyTemplate.getBossHp(bossSpawnCount);
                BossEnemy boss = new BossEnemy(bossX, bossY, 2, 0, bossHp);
                enemyAircrafts.add(boss);

                if (isMusicEnabled) {
                    soundManager.stopBgm();
                    soundManager.playBossBgm();
                }
                bossScoreThreshold += bossadd;
            }
        }

        bulletsMoveAction();
        aircraftsMoveAction();
        crashCheckAction();
        propsMoveAction();
        postProcessAction();

        // 游戏结束检查
        if (heroAircraft.getHp() <= 0) {
            gameOverFlag = true;
            isRunning = false;
            heroAircraft.setShootStrategy(new StraightShoot());
            propEndTime = 0;
            soundManager.stopAll();
            if (isMusicEnabled) {
                soundManager.playSound(SoundManager.GAME_OVER_PATH);
            }
            // 触发 Activity 回调
            if (gameHolder != null) {
                gameHolder.onGameOver(score);
            }
        }
    }

    private void draw() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            try {
                // 清理画布
                canvas.drawColor(Color.BLACK);

                // 绘制背景
                drawBackground(canvas);

                // 绘制子弹
                paintImageWithPositionRevised(canvas, enemyBullets);
                paintImageWithPositionRevised(canvas, heroBullets);

                // 绘制敌机和道具
                paintImageWithPositionRevised(canvas, enemyAircrafts);
                paintImageWithPositionRevised(canvas, props);

                // 绘制英雄机
                Bitmap heroBitmap = ImageManager.HERO_IMAGE;
                canvas.drawBitmap(heroBitmap,
                        (float) (heroAircraft.getLocationX() - heroBitmap.getWidth() / 2.0),
                        (float) (heroAircraft.getLocationY() - heroBitmap.getHeight() / 2.0), mPaint);

                // 绘制得分和生命值
                paintScoreAndLife(canvas);

            } finally {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawBackground(Canvas canvas) {
        Bitmap background;
        switch (difficulty) {
            case 1: background = ImageManager.SINGLE_BACKGROUND_IMAGE; break;
            case 3: background = ImageManager.HARD_BACKGROUND_IMAGE; break;
            default: background = ImageManager.NORMAL_BACKGROUND_IMAGE; break;
        }

        // 图片滚动逻辑优化
        backGroundTop += 5;
        if (backGroundTop >= screenHeight) {
            backGroundTop = 0;
        }

        // 绘制两张图片实现滚动
        canvas.drawBitmap(background, null, new android.graphics.Rect(0, backGroundTop - screenHeight, screenWidth, backGroundTop), mPaint);
        canvas.drawBitmap(background, null, new android.graphics.Rect(0, backGroundTop, screenWidth, backGroundTop + screenHeight), mPaint);
    }

    private void paintImageWithPositionRevised(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        for (AbstractFlyingObject object : objects) {
            Bitmap image = ImageManager.get(object);
            if (image != null) {
                canvas.drawBitmap(image,
                        (float) (object.getLocationX() - image.getWidth() / 2.0),
                        (float) (object.getLocationY() - image.getHeight() / 2.0), mPaint);
            }
        }
    }

    private void paintScoreAndLife(Canvas canvas) {
        int x = 50;
        int y = 100;
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(60);
        canvas.drawText("SCORE:" + this.score, x, y, mPaint);
        y += 70;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, mPaint);
    }

    // --- 游戏逻辑方法 ---

    private void shootAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft instanceof EliteEnemy || enemyAircraft instanceof SuperEliteEnemy || enemyAircraft instanceof BossEnemy) {
                enemyBullets.addAll(enemyAircraft.shoot());
            }
        }
        heroBullets.addAll(heroAircraft.shoot());
        if (isMusicEnabled) {
            soundManager.playSound(SoundManager.BULLET_PATH);
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
            // 子弹飞出屏幕后清除 (向上飞)
            if (bullet.getLocationY() < 0) {
                bullet.vanish();
            }
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
            // 子弹飞出屏幕后清除 (向下飞)
            if (bullet.getLocationY() > screenHeight) {
                bullet.vanish();
            }
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
            // 敌机飞出屏幕底部后清除
            if (enemyAircraft.getLocationY() > screenHeight) {
                enemyAircraft.vanish();
            }
        }
    }

    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
            if (prop.getLocationY() > screenHeight) {
                prop.vanish();
            }
        }
    }

    private void crashCheckAction() {
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) continue;
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) continue;
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) continue;
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (isMusicEnabled) {
                        soundManager.playSound(SoundManager.BULLET_HIT_PATH);
                    }
                    if (enemyAircraft.notValid()) {
                        score += 10;
                        handlePropDrop(enemyAircraft);
                    }
                }
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }
        for (AbstractProp prop : props) {
            if (prop.notValid()) continue;
            if (heroAircraft.crash(prop) || prop.crash(heroAircraft)) {
                if (isMusicEnabled) {
                    soundManager.playSound(SoundManager.PROP_PATH);
                }
                prop.effect(heroAircraft);
                if (prop instanceof FireProp || prop instanceof SuperFireProp) {
                    propEndTime = System.currentTimeMillis() + propDuration;
                } else if (prop instanceof BombProp) {
                    if (isMusicEnabled) {
                        soundManager.playSound(SoundManager.BOMB_PATH);
                    }
                    BombProp bomb = (BombProp) prop;
                    for (AbstractAircraft enemy : enemyAircrafts) if (enemy instanceof BombSubscriber) bomb.addSubscriber((BombSubscriber) enemy);
                    for (BaseBullet b : enemyBullets) if (b instanceof BombSubscriber) bomb.addSubscriber((BombSubscriber) b);
                    score += bomb.notifySubscribers();
                }
                prop.vanish();
            }
        }
    }

    private void handlePropDrop(AbstractAircraft enemyAircraft) {
        int dropCount = 0;
        if (enemyAircraft instanceof EliteEnemy) dropCount = random.nextDouble() < 0.7 ? 1 : 0;
        else if (enemyAircraft instanceof SuperEliteEnemy) dropCount = random.nextDouble() < 0.9 ? 1 : 0;
        else if (enemyAircraft instanceof BossEnemy) {
            dropCount = random.nextInt(3) + 1;
            soundManager.stopBossBgm();
            soundManager.playBgm();
        }
        if (dropCount > 0) {
            props.addAll(propSpawner.spawnMultipleProps(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), dropCount));
        }
    }

    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    private void checkAndIncreaseDifficulty() {
        if (difficultyTemplate.shouldIncreaseDifficultyOverTime()) {
            if (time > 0 && time % 30000 < timeInterval) {
                enemySpeedIncrement++;
                for (AbstractAircraft enemy : enemyAircrafts) {
                    enemy.increaseSpeed(1);
                }
            }
        }
    }

    public int getEnemySpeedIncrement() { return enemySpeedIncrement; }
    public int getBossSpawnCount() { return bossSpawnCount; }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
