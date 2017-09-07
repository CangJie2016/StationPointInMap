package com.zxw.mappoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;


/**
 * Created by 李振强 on 2017/9/6.
 */
public class MapPointHelper {
    private static final String TAG = "MapPointHelper";
    private static MapPointHelper bpUtil;
    private final Context mContext;
    private static final int DEFAULT_TEXT_SIZE = 36;
    private final Paint storkPaint;

    private int defaultPadding = 8;

    private State mState;
    private Station mStation;
    private final DisplayMetrics displayMetrics;
    private final Paint paint;

    /*
     带旗子-文字背景绿色  --- 选中
     只有点状图 --距离远的
     点状图 + 文字 --距离近的
      */
    public enum State {
        CHOOSE, FAR, NEAR
    }
    /*
    中间点的选择，上站还是下站
     */
    public enum Station{
        UP, DOWN
    }

    public MapPointHelper(Context context) {
        this.mContext = context;

        // 初始化像素比例， 画笔种类
        displayMetrics = mContext.getResources().getDisplayMetrics();
        int color = Color.WHITE;
        Typeface font = Typeface.create("宋体", Typeface.BOLD);

        paint = new Paint();

        paint.setColor(color);
        paint.setTypeface(font);
        paint.setAntiAlias(true);//去除锯齿
        paint.setFilterBitmap(true);//对位图进行滤波处理

        storkPaint = new Paint();
        storkPaint.setStyle(Paint.Style.STROKE);
        storkPaint.setStrokeWidth(2);
    }

    public Bitmap generateBitmap(State state, Station station, String stationName, String time){
        return  generateBitmap(state,station,DEFAULT_TEXT_SIZE, stationName, time);
    }

    public Bitmap generateBitmap(State state, Station station, int textSize, String stationName, String time){
        paint.setTextSize(scalaFonts(textSize));
        this.mState = state;
        this.mStation = station;
        return  getImage(stationName, time);
    }

    public static MapPointHelper getInstance(Context context) {
        if (bpUtil == null) {
            bpUtil = new MapPointHelper(context);
        }
        return bpUtil;
    }


    /*
     * 默认采用白色字体，宋体文字加粗
     */
    private Bitmap getImage(String stationName, String time) {
        // 先画点，再画旗子
        Bitmap flagBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.flag2);
        int flagWidth = flagBitmap.getScaledWidth(displayMetrics);
        int flagHeight = flagBitmap.getScaledHeight(displayMetrics);
//2 * defaultPadding +
        int textWidth = (int) (getFontlength(paint, stationName));
        // 设置过字体大小，才知道画笔对应字符串有多宽
        int sumWidth = textWidth > flagWidth ? textWidth : flagWidth;
        sumWidth += 4* defaultPadding;
        int flagX = (sumWidth - flagWidth) / 2;
        int flagY = defaultPadding;

        Bitmap pointBitmap;
        if (mStation == Station.UP){
            pointBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.up_point);
        }else{
            pointBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.down_point);
        }
        int pointWidth = pointBitmap.getScaledWidth(displayMetrics);
        int pointHeight = pointBitmap.getScaledHeight(displayMetrics);
        // +2 是因为看着像是左倾，所以往右边去两个单位
        int pointX = (sumWidth - pointWidth) / 2 + 2;
        // 旗子的落脚点在点的中心，因此要减去2分之1的高度
        int pointY = defaultPadding + flagHeight - pointHeight / 2;

        float stationTextWidth = getFontlength(paint, stationName);
        // 字体高度，由于站点名称和时间都是一行来表示，所以此高度就是通用的
        float textHeight = getFontHeight(paint);
        float nameTextX = (sumWidth - stationTextWidth) / 2;
//        float tY = (y - getFontHeight(paint))/2+getFontLeading(paint);
        // canvas 绘制text的y坐标是文字基线baseLine的高度，因此要加上文字的高度
        float nameTextY = 2 * defaultPadding + flagHeight + pointHeight/2 + textHeight;

        // 计算时间文字显示的宽高及绘制的xy起点
        float timeTextWidth = getFontlength(paint, time);
        float timeTextX = (sumWidth - timeTextWidth) / 2;
        float timeTextY = defaultPadding + nameTextY + textHeight;

        // 总高度等于旗子高度 + 默认padding 之和 *2
        float sumHeight = (flagHeight + defaultPadding) * 2;
        Log.w(TAG, "sumWidth: " + sumWidth);
        Log.w(TAG, "sumHeight: " + sumHeight);

        // 具有内边距的矩形背景
        RectF reftF = new RectF(nameTextX - defaultPadding, nameTextY - textHeight, nameTextX + stationTextWidth + defaultPadding, nameTextY + textHeight + 2 * defaultPadding);

        Bitmap bmp = Bitmap.createBitmap(sumWidth, (int) sumHeight, Bitmap.Config.ARGB_8888);

        //图象大小要根据文字大小算下,以和文本长度对应
        Canvas canvasTemp = new Canvas(bmp);
        canvasTemp.drawColor(Color.TRANSPARENT);

        switch (mState){
            case CHOOSE:
                canvasTemp.drawBitmap(pointBitmap, pointX, pointY, paint);
                canvasTemp.drawBitmap(flagBitmap, flagX, flagY, paint);
                paint.setColor(Color.BLUE);
                canvasTemp.drawRoundRect(reftF, 4, 4, paint);
                paint.setColor(Color.WHITE);
                storkPaint.setColor(Color.WHITE);
                canvasTemp.drawRoundRect(reftF, 4, 4, storkPaint);
                canvasTemp.drawText(stationName, nameTextX, nameTextY, paint);
                canvasTemp.drawText(time, timeTextX, timeTextY, paint);
                break;
            case FAR:
                canvasTemp.drawBitmap(pointBitmap, pointX, pointY, paint);
                break;
            case NEAR:
                canvasTemp.drawBitmap(pointBitmap, pointX, pointY, paint);

                paint.setColor(Color.WHITE);
                canvasTemp.drawRoundRect(reftF, 4, 4, paint);
                paint.setColor(Color.BLUE);
                storkPaint.setColor(Color.BLUE);
                canvasTemp.drawRoundRect(reftF, 4, 4, storkPaint);
                canvasTemp.drawText(stationName, nameTextX, nameTextY, paint);
                canvasTemp.drawText(time, timeTextX, timeTextY, paint);
                break;
        }
        Log.w(TAG, "getImage:  draw" );
        return bmp;
    }

    /**
     * 根据屏幕系数比例获取文字大小
     *
     * @return
     */
    private static float scalaFonts(int size) {
        //暂未实现
        return size;
    }

    /**
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * @return 返回指定笔离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }

}