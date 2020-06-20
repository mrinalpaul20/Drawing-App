package com.mrinal.zersey.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

public class ColorPicker extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {
    private CanvasView canvasView;

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(CanvasView canvasView) {
        this.canvasView = canvasView;
        setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        LinearGradient colors = new LinearGradient(0, 0, width, 0,
                new int[]{0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                        0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
                null, Shader.TileMode.CLAMP);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(colors);
        setProgressDrawable(shape);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            setColor(progress);
    }

    private void setColor(int progress) {
        int r = 0, g = 0, b = 0;
        if (progress < 256) {
            b = progress;
        } else if (progress < 256 * 2) {
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 3) {
            g = 255;
            b = progress % 256;
        } else if (progress < 256 * 4) {
            r = progress % 256;
            g = 256 - progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 5) {
            r = 255;
            g = 0;
            b = progress % 256;
        } else if (progress < 256 * 6) {
            r = 255;
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 7) {
            r = 255;
            g = 255;
            b = progress % 256;
        }
        canvasView.setColor(Color.argb(255, r, g, b));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}