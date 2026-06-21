package com.carrom.autoplay.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.carrom.autoplay.model.Coin;
import com.carrom.autoplay.model.Striker;
import com.carrom.autoplay.model.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Thread gameThread;
    private volatile boolean running = false;
    private final SurfaceHolder holder;

    private Striker striker;
    private List<Coin> coins = new ArrayList<>();
    private Paint boardPaint;
    private Random random = new Random();

    public boolean autoPlayEnabled = false;
    private long lastAutoMoveTime = 0;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);

        boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boardPaint.setColor(Color.rgb(222, 184, 135));

        striker = new Striker();
        setupCoins();
    }

    private void setupCoins() {
        coins.clear();
        float cx = 300, cy = 300;
        int idx = 0;
        for (int row = -2; row <= 2; row++) {
            for (int col = -2; col <= 2; col++) {
                if (Math.abs(row) + Math.abs(col) > 3) continue;
                int type = (idx % 2 == 0) ? 0 : 1;
                if (idx == 12) type = 2; // center queen
                coins.add(new Coin(cx + col * 28, cy + row * 28, type));
                idx++;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (striker.position.x == 0) {
            striker.setBaseLinePosition(getWidth(), false);
        }
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        striker.setBaseLinePosition(width, false);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        running = false;
        try {
            if (gameThread != null) gameThread.join();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        while (running) {
            long now = System.currentTimeMillis();
            float dt = (now - lastTime) / 16f; // normalize to ~60fps step
            lastTime = now;

            update(dt);
            drawFrame();

            if (autoPlayEnabled && !striker.isMoving && now - lastAutoMoveTime > 900) {
                autoMove();
                lastAutoMoveTime = now;
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void update(float dt) {
        striker.update(dt);
        for (Coin c : coins) {
            c.update(dt);
            checkPocket(c);
        }
    }

    private void checkPocket(Coin c) {
        int w = getWidth() == 0 ? 600 : getWidth();
        int h = getHeight() == 0 ? 600 : getHeight();
        float margin = 30;
        float[][] pockets = {
                {margin, margin}, {w - margin, margin},
                {margin, h - margin}, {w - margin, h - margin}
        };
        for (float[] p : pockets) {
            float dx = c.position.x - p[0];
            float dy = c.position.y - p[1];
            if (Math.sqrt(dx * dx + dy * dy) < 22) {
                c.isPocketed = true;
            }
        }
    }

    /** Simple auto-play: aim striker roughly at nearest un-pocketed coin and fire. */
    public void autoMove() {
        Coin target = null;
        float bestDist = Float.MAX_VALUE;
        for (Coin c : coins) {
            if (c.isPocketed) continue;
            float dx = c.position.x - striker.position.x;
            float dy = c.position.y - striker.position.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < bestDist) {
                bestDist = dist;
                target = c;
            }
        }
        if (target == null) return;

        Vector2D dir = target.position.sub(striker.position).normalize();
        float power = 14f + random.nextFloat() * 4f;
        striker.velocity = dir.scale(power);
        striker.isMoving = true;
    }

    public void fire(float dirX, float dirY, float power) {
        Vector2D dir = new Vector2D(dirX, dirY).normalize();
        striker.velocity = dir.scale(power);
        striker.isMoving = true;
    }

    public void resetBoard() {
        striker.velocity = new Vector2D(0, 0);
        striker.isMoving = false;
        striker.setBaseLinePosition(getWidth(), false);
        setupCoins();
    }

    private void drawFrame() {
        if (!holder.getSurface().isValid()) return;
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;
        try {
            canvas.drawColor(Color.rgb(26, 26, 46));
            float margin = 20;
            canvas.drawRect(margin, margin, getWidth() - margin, getHeight() - margin, boardPaint);

            Paint pocketPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            pocketPaint.setColor(Color.BLACK);
            float pm = 30;
            canvas.drawCircle(pm, pm, 18, pocketPaint);
            canvas.drawCircle(getWidth() - pm, pm, 18, pocketPaint);
            canvas.drawCircle(pm, getHeight() - pm, 18, pocketPaint);
            canvas.drawCircle(getWidth() - pm, getHeight() - pm, 18, pocketPaint);

            for (Coin c : coins) c.draw(canvas);
            striker.draw(canvas);
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
