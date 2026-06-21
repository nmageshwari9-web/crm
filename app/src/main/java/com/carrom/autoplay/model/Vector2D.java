package com.carrom.autoplay.model;

public class Vector2D {
    public float x, y;

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D o) {
        return new Vector2D(x + o.x, y + o.y);
    }

    public Vector2D sub(Vector2D o) {
        return new Vector2D(x - o.x, y - o.y);
    }

    public Vector2D scale(float s) {
        return new Vector2D(x * s, y * s);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        float len = length();
        if (len == 0) return new Vector2D(0, 0);
        return new Vector2D(x / len, y / len);
    }
}
