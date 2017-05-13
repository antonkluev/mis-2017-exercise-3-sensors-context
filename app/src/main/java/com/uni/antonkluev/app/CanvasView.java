
package com.uni.antonkluev.app;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import java.util.ArrayList;

// https://examples.javacodegeeks.com/android/core/graphics/canvas-graphics/android-canvas-example/
public class CanvasView extends View {
    private Path  mPath;
    private Paint mPaint;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(1f);
        mPath  = new Path();
    }
    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw text
        drawString(canvas, "jesus christ", 0, 0);

        ArrayList<Float> position = new ArrayList<Float>();
        position.add(150f);
        position.add(150f);
        position.add(0f);
        position.add(0f);
        drawLine(canvas, position, Color.LTGRAY);

        ArrayList<Float> position1 = new ArrayList<Float>();
        position1.add(0f);
        position1.add(0f);
        position1.add(150f);
        position1.add(150f);
        drawLine(canvas, position1, Color.RED);
    }
    void drawString (Canvas canvas, String text, int x, int y) {
        Paint textPaint = new Paint();
        canvas.drawText(
                text,
                x - textPaint.measureText(text) / 2,
                y - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint);
    }
    void drawLine (Canvas canvas, ArrayList<Float> position, int color) {
        mPaint.setColor(color);
        float width  = (float)canvas.getWidth() / (position.size() - 1f);
        float height = (float)canvas.getHeight();
        mPath.moveTo(0, height - position.get(0));
        for (int i = 1; i < position.size(); i ++)
            mPath.lineTo((float)i * width, height - position.get(i));
        canvas.drawPath(mPath, mPaint);
        mPath.reset();
    }
}
