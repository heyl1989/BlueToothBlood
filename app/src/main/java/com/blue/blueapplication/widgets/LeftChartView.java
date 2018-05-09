package com.blue.blueapplication.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.blue.blueapplication.FrameApp;
import com.blue.blueapplication.utils.DP2PX;

/**
 * Created by wangxiaojian on 16/4/20.
 * com.blue.blueapplication.widgets.ChartView
 */
public class LeftChartView extends View {
    public int XPoint = 40;    //原点的X坐标
    public int YPoint = 260;     //原点的Y坐标
    public int XScale = 55;     //X的刻度长度
    public int YScale = 40;     //Y的刻度长度
    public int XLength = 380;        //X轴的长度
    public int YLength = 240;        //Y轴的长度
    public int textSize;    //文字大小
    public String[] XLabel;    //X的刻度
    public String[] YLabel = new String[]{"0", "30", "60", "90", "120", "150", "180"};    //Y的刻度
    public String[] height;      //数据
    public String[] low;      //数据
    public String[] heart;      //数据
    public String Title;    //显示的标题

    public LeftChartView(Context context) {
        super(context, null);
    }

    public LeftChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        XPoint = FrameApp.mApp.ui.DipToPixels(10);
        XPoint = DP2PX.dip2px(context,30);
        YPoint = FrameApp.mApp.ui.DipToPixels(200)+1;
        XLength = FrameApp.mApp.ui.getmScreenWidth() - FrameApp.mApp.ui.DipToPixels(50);
        YLength = FrameApp.mApp.ui.DipToPixels(180);
        YScale = (YLength-10)/6;
        textSize = DP2PX.dip2px(context,12);
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

        //设置Y轴
        canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);   //轴线
        for (int i = 0; i * YScale < YLength; i++) {

            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint - 5, YPoint - i * YScale, paint);  //刻度
            try {
                canvas.drawText(YLabel[i], XPoint -2*textSize, YPoint - i * YScale + 5, paint);  //文字
            } catch (Exception e) {
            }
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(FrameApp.mApp.ui.DipToPixels(30), FrameApp.mApp.ui.DipToPixels(220));//这样就超出了屏幕大小
    }
}
