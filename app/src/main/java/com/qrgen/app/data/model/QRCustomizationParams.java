package com.qrgen.app.data.model;

import android.graphics.Color;

public class QRCustomizationParams {
    private int foregroundColor;
    private int backgroundColor;
    private int size;

    public QRCustomizationParams() {
        this.foregroundColor = Color.BLACK;
        this.backgroundColor = Color.WHITE;
        this.size = 512;
    }

    public QRCustomizationParams(int foregroundColor, int backgroundColor, int size) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.size = size;
    }

    public int getForegroundColor() { return foregroundColor; }
    public void setForegroundColor(int foregroundColor) { this.foregroundColor = foregroundColor; }

    public int getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public boolean hasLowContrast() {
        int rDiff = Math.abs(Color.red(foregroundColor) - Color.red(backgroundColor));
        int gDiff = Math.abs(Color.green(foregroundColor) - Color.green(backgroundColor));
        int bDiff = Math.abs(Color.blue(foregroundColor) - Color.blue(backgroundColor));
        return (rDiff + gDiff + bDiff) < 200;
    }
}