package com.mrinal.zersey.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class CanvasView extends View {
    private final Paint mPaint;
    private boolean ERASER_MODE;
    private Path path;
    private Paint paint;
    private ArrayList<DrawingPath> paths = new ArrayList<>();
    private ArrayList<DrawingPath> undonePaths = new ArrayList<>();
    private int currentColor;
    private Bitmap bitmap;
    private int w, h;
    private Canvas mCanvas;
    private CanvasListener listener;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        mPaint = new Paint(Paint.DITHER_FLAG);
        currentColor = Color.BLACK;
        startBrush();
    }

    public void setListener(CanvasListener listener) {
        this.listener = listener;
    }

    private void setBitmap() {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case ACTION_DOWN:
                path.moveTo(x, y);
                if (!ERASER_MODE)
                    listener.changeColorPickerVisibility(GONE);
                break;
            case ACTION_MOVE:
                path.lineTo(x, y);
                mCanvas.drawPath(path, paint);
                invalidate();
                break;
            case ACTION_UP:
                paths.add(new DrawingPath(path, paint));
                path.reset();
                if (!ERASER_MODE)
                    listener.changeColorPickerVisibility(VISIBLE);
                listener.onActionPerformed(paths.size() > 0, undonePaths.size() > 0);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        this.w = w;
        this.h = h;
        setBitmap();
    }

    public void clear() {
        paths.clear();
        undonePaths.clear();
        setBitmap();
        invalidate();
        currentColor = Color.BLACK;
        startBrush();
        listener.onActionPerformed(false, false);
    }

    public void undo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            refreshBitmap();
            listener.onActionPerformed(paths.size() > 0, true);
        }
    }

    public void redo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            refreshBitmap();
            listener.onActionPerformed(true, undonePaths.size() > 0);
        }
    }

    private void refreshBitmap() {
        setBitmap();
        for (DrawingPath drawingPath : paths) {
            paint.setColor(drawingPath.color);
            paint.setStrokeWidth(drawingPath.strokeWidth);
            mCanvas.drawPath(drawingPath, paint);
        }
        refreshPaint();
        invalidate();
    }

    public void startEraser() {
        ERASER_MODE = true;
        refreshPaint();
    }

    public void startBrush() {
        ERASER_MODE = false;
        refreshPaint();
    }

    public void setColor(int color) {
        currentColor = color;
        refreshPaint();
    }

    private void refreshPaint() {
        if (ERASER_MODE) {
            paint.setStrokeWidth(50f);
            paint.setColor(Color.WHITE);
        } else {
            paint.setStrokeWidth(20f);
            paint.setColor(currentColor);
        }
    }

    public Bitmap getBitmap() {
        Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        if (bitmap.sameAs(emptyBitmap))
            return null;
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);
        return bitmap;
    }

    public interface CanvasListener {
        void changeColorPickerVisibility(int visibility);

        void onActionPerformed(boolean isUndoAvailable, boolean isRedoAvailable);
    }

    private class DrawingPath extends Path {
        int color;
        float strokeWidth;

        DrawingPath(Path path, Paint paint) {
            super(path);
            color = paint.getColor();
            strokeWidth = paint.getStrokeWidth();
        }
    }
}