package edu.hitsz.application;

import android.view.MotionEvent;
import android.view.View;
import edu.hitsz.aircraft.HeroAircraft;

/**
 * 英雄机控制类
 * 监听触摸事件，控制英雄机的移动
 *
 * @author hitsz
 */
public class HeroController implements View.OnTouchListener {
    private HeroAircraft heroAircraft;

    public HeroController(HeroAircraft heroAircraft){
        this.heroAircraft = heroAircraft;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Game game = (Game) v;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (x < 0 || x > game.getScreenWidth() || y < 0 || y > game.getScreenHeight()) {
                    // 防止超出边界
                    return true;
                }
                heroAircraft.setLocation(x, y);
                break;
        }
        return true;
    }
}
