package com.yuandaima.peanutrobot.util;

public final class CenterCropCoordinateMapper {
    private final float sourceWidth;
    private final float sourceHeight;
    private final float viewWidth;
    private final float viewHeight;
    private final float scale;
    private final float translationX;
    private final float translationY;

    public CenterCropCoordinateMapper(
            float sourceWidth,
            float sourceHeight,
            float viewWidth,
            float viewHeight
    ) {
        if (sourceWidth <= 0f || sourceHeight <= 0f || viewWidth <= 0f || viewHeight <= 0f) {
            throw new IllegalArgumentException("Source and view dimensions must be positive");
        }
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        scale = Math.max(viewWidth / sourceWidth, viewHeight / sourceHeight);
        translationX = (viewWidth - sourceWidth * scale) / 2f;
        translationY = (viewHeight - sourceHeight * scale) / 2f;
    }

    public Coordinate toView(float normalizedX, float normalizedY) {
        return new Coordinate(
                normalizedX * sourceWidth * scale + translationX,
                normalizedY * sourceHeight * scale + translationY
        );
    }

    public Coordinate toNormalized(float viewX, float viewY) {
        float visibleViewX = clamp(viewX, 0f, viewWidth);
        float visibleViewY = clamp(viewY, 0f, viewHeight);
        float normalizedX = (visibleViewX - translationX) / (sourceWidth * scale);
        float normalizedY = (visibleViewY - translationY) / (sourceHeight * scale);
        return new Coordinate(normalizedX, normalizedY);
    }

    public float getScale() {
        return scale;
    }

    public float getTranslationX() {
        return translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    private float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public static final class Coordinate {
        private final float x;
        private final float y;

        public Coordinate(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }
}
