package com.carrom.autoplay.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Coin {
    public static final float RADIUS = 12f;
    public Vector2D position;
    public Vector2D velocity;
    public boolean isPocketed = false;
    public int colorType; // 0 = white, 1 = black, 2 = red(queen)

    private Paint paint;

    public Coin(float x, float y, int colorType) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
        this.colorType = colorType;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    private int baseColor() {
        switch (colorType) {
            case 1: return Color.rgb(30, 30, 30);
            case 2: return Color.rgb(200, 30, 30);
            default: return Color.rgb(245, 245, 230);
        }
    }

    public void draw(Canvas canvas) {
        if (isPocketed) return;
        paint.setColor(baseColor());
        canvas.drawCircle(position.x, position.y, RADIUS, paint);
    }

    public void update(float dt) {
        if (velocity.length() > 0) {
            position = position.add(velocity.scale(dt));
            velocity = velocity.scale(0.97f);
            if (velocity.length() < 0.3f) {
                velocity = new Vector2D(0, 0);
            }
        }
    }
}
