package com.zxw.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 李振强 on 2017/8/25.
 */

public class StudyView extends View{
    Path path = new Path(); // 初始化 Path 对象
    Paint paint = new Paint();
    public StudyView(Context context) {
        this(context, null);
    }

    public StudyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public StudyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
        Shader shader1 = new BitmapShader(bitmap1, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        paint.setShader(shader1);

        ColorFilter lightingColorFilter = new LightingColorFilter(0x00ffff, 0x000000);
        paint.setColorFilter(lightingColorFilter);

        canvas.drawRect(0, 0, 700, 700, paint);
    }
}
