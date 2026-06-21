package com.carrom.autoplay.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Striker {
    public static final float RADIUS = 16f;
    public Vector2D position;
    public Vector2D velocity;
    public boolean isMoving = false;

    private Paint paint;

    public Striker() {
        this.position = new Vector2D(0, 0);
        this.velocity = new Vector2D(0, 0);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(255, 200, 100, 50));
        paint.setStyle(Paint.Style.FILL);
    }

    public void setBaseLinePosition(float boardWidth, boolean isTopPlayer) {
        float y = isTopPlayer ? 60 : 540;
        this.position = new Vector2D(boardWidth / 2f, y);
    }

    public void draw(Canvas canvas) {
        paint.setAlpha(80);
        canvas.drawCircle(position.x + 2, position.y + 2, RADIUS, paint);
        paint.setAlpha(255);
        paint.setColor(Color.argb(255, 200, 100, 50));
        canvas.drawCircle(position.x, position.y, RADIUS, paint);
        paint.setColor(Color.argb(80, 255, 200, 150));
        canvas.drawCircle(position.x - 3, position.y - 3, RADIUS / 3, paint);
    }

    public void update(float dt) {
        if (velocity.length() > 0) {
            position = position.add(velocity.scale(dt));
            velocity = velocity.scale(0.96f);
            if (velocity.length() < 0.5f) {
                velocity = new Vector2D(0, 0);
                isMoving = false;
            }
        }
    }
}
