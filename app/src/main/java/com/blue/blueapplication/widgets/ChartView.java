package com.blue.blueapplication.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.blue.blueapplication.FrameApp;

/**
 * Created by wangxiaojian on 16/4/20.
 * com.blue.blueapplication.widgets.ChartView
 */
public class ChartView extends View {
    public int XPoint = 100;    //原点的X坐标
    public int YPoint = 260;     //原点的Y坐标
    public int XScale = 100;     //X的刻度长度
    public int YScale = 100;     //Y的刻度长度
    public int XLength = 4500;        //X轴的长度
    public int YLength = 240;        //Y轴的长度
    public int  textSize;
    public String[] XLabel;    //X的刻度
    public String[] YLabel;    //Y的刻度
    public String[] height;      //数据
    public String[] low;      //数据
    public String[] heart;      //数据
    public String Title;    //显示的标题

    public ChartView(Context context) {
        super(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        XPoint = FrameApp.mApp.ui.DipToPixels(-2);
        XLength =  FrameApp.mApp.ui.DipToPixels(950);
        XScale =  FrameApp.mApp.ui.DipToPixels(30);
        YPoint = FrameApp.mApp.ui.DipToPixels(200);
        YLength = FrameApp.mApp.ui.DipToPixels(180);
        textSize = FrameApp.mApp.ui.DipToPixels(12);
    }


    public void SetInfo(String[] XLabels, String[] YLabels, String[] heights, String[] lows, String[] hearts, String strTitle) {
        XLabel = XLabels;
        YLabel = YLabels;
        height = heights;
        low = lows;
        heart = hearts;
        Title = strTitle;
        YScale = (YLength - 10) / 6;
//        XScale = (XLength-5)/(XLabels.length-1);
        XLength = XScale * (XLabels.length);
        invalidate();
        measure(0, 0);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);//重写onDraw方法

        //canvas.drawColor(Color.WHITE);//设置背景颜色
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);//去锯齿
        paint.setColor(Color.BLACK);//颜色
        paint.setTextSize(textSize);  //设置轴文字大小
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);//去锯齿
        paint1.setColor(Color.BLACK);

//        //设置Y轴
//        canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);   //轴线
//        for (int i = 0; i * YScale < YLength; i++) {
//            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale, paint);  //刻度
//            try {
//                canvas.drawText(YLabel[i], XPoint - 25, YPoint - i * YScale + 5, paint);  //文字
//            } catch (Exception e) {
//            }
//        }
//        canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint - YLength + 6, paint);  //箭头
//        canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint - YLength + 6, paint);
        //设置X轴
        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint);   //轴线
        for (int i = 0; i * XScale < XLength; i++) {
            if (i != 0)
                canvas.drawLine(XPoint + i * XScale, YPoint, XPoint + i * XScale, YPoint - 5, paint);  //刻度
            try {
                if (i != 0)
                    canvas.drawText(XLabel[i], XPoint + i * XScale - 10, YPoint + textSize, paint);  //文字
                //数据值
                paint.setColor(0xff5ed7e7);

                if (i > 0 && YCoord(height[i - 1]) != -999 && YCoord(height[i]) != -999) {
                    //保证有效数据
                    if (!"0".equals(height[i - 1]) && !"0".equals(height[i]))
                        canvas.drawLine(XPoint + (i - 1) * XScale, YCoord(height[i - 1]), XPoint + i * XScale, YCoord(height[i]), paint);
                }
                if (!"0".equals(height[i]))
                    canvas.drawCircle(XPoint + i * XScale, YCoord(height[i]), 3, paint);

                paint.setColor(0xff26438e);
                if (i > 0 && YCoord(low[i - 1]) != -999 && YCoord(low[i]) != -999) {
                    //保证有效数据
                    if (!"0".equals(low[i - 1]) && !"0".equals(low[i]))
                        canvas.drawLine(XPoint + (i - 1) * XScale, YCoord(low[i - 1]), XPoint + i * XScale, YCoord(low[i]), paint);
                }
                if (!"0".equals(low[i]))
                    canvas.drawCircle(XPoint + i * XScale, YCoord(low[i]), 3, paint);

                paint.setColor(0xffb7006d);
                if (i > 0 && YCoord(heart[i - 1]) != -999 && YCoord(heart[i]) != -999) {
                    //保证有效数据
                    if (!"0".equals(heart[i - 1]) && !"0".equals(heart[i]))
                        canvas.drawLine(XPoint + (i - 1) * XScale, YCoord(heart[i - 1]), XPoint + i * XScale, YCoord(heart[i]), paint);
                }
                if (!"0".equals(heart[i]))
                    canvas.drawCircle(XPoint + i * XScale, YCoord(heart[i]), 3, paint);
            } catch (Exception e) {
            }
        }
//        paint.setColor(Color.BLUE);//颜色
//        canvas.drawLine(XPoint + XLength, YPoint, XPoint + XLength - 6, YPoint - 3, paint);    //箭头
//        canvas.drawLine(XPoint + XLength, YPoint, XPoint + XLength - 6, YPoint + 3, paint);
    }

    private int YCoord(String y0)  //计算绘制时的Y坐标，无数据时返回-999
    {
        int y;
        try {
            y = Integer.parseInt(y0);
        } catch (Exception e) {
            return -999;    //出错则返回-999
        }
        try {
            return YPoint - y * YScale / Integer.parseInt(YLabel[1]);
        } catch (Exception e) {
        }
        return y;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(XLength + 100, FrameApp.mApp.ui.DipToPixels(220));//这样就超出了屏幕大小
    }
}
