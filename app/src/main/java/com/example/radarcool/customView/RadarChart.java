package com.example.radarcool.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.radarcool.R;
import com.example.radarcool.Utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class RadarChart extends View {
    private int background_line_color;
    private int background_color;
//    private List<Integer> itemColorList=new ArrayList<>();
    private List<String> itemList=new ArrayList<>();
    private List<float[]> itemProportionList=new ArrayList<>();
    private int line_point=10;
    private List<Integer> itemProportionColorList=new ArrayList<>();

    public RadarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.RadarChart);
        background_line_color=a.getColor(R.styleable.RadarChart_background_line_color,Color.WHITE);
        background_color=a.getColor(R.styleable.RadarChart_background_color,Color.GRAY);
        a.recycle();
        itemList.add("语文");
        itemList.add("数学");
        itemList.add("英语");
        itemList.add("化学");
        itemList.add("物理");
        itemList.add("生物");
//        itemColorList.add(Color.RED);
//        itemColorList.add(Color.GREEN);
//        itemColorList.add(Color.WHITE);
//        itemColorList.add(Color.BLUE);
//        itemColorList.add(Color.BLACK);
//        itemColorList.add(Color.YELLOW);
        float[] b1=new float[6];
        b1[0]=0.8f;
        b1[1]=0.9f;
        b1[2]=0.3f;
        b1[3]=0.2f;
        b1[4]=0.1f;
        b1[5]=0.6f;
        float[] b2=new float[6];
        b2[0]=0.2f;
        b2[1]=0.9f;
        b2[2]=0.3f;
        b2[3]=0.3f;
        b2[4]=0.3f;
        b2[5]=0.3f;
        itemProportionList.add(b1);
        itemProportionList.add(b2);
        itemProportionColorList.add(Color.BLUE);
        itemProportionColorList.add(Color.YELLOW);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
        //TODO:handle touch event
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        int real_width;
        int real_height;
        if(widthMode==MeasureSpec.EXACTLY){
            real_width=width;
        }else{
            real_width=400;
        }
        if(heightMode==MeasureSpec.EXACTLY){
            real_height=height;
        }else{
            real_height=400;
        }
        setMeasuredDimension(real_width,real_height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width=getWidth();
        int height=getHeight();
        //字体、布局、线等适配的大小
        int adapter_min=Math.min(width,height);

        //画整个radar的背景
        Paint background_paint=new Paint();
        background_paint.setColor(background_color);
        canvas.drawRoundRect(0,0,width,height,80,80,background_paint);

        //原点的位置
        int origin_width= width/2;
        int origin_height= (int) (height/2*0.8);
        //设置原点
        canvas.translate(origin_width,origin_height);
        //计算雷达图的半径
        int radar_width= (int) (width*0.4);
        int radar_height= (int) (height*0.4);
        int radar_min=Math.min(radar_height,radar_width);

        //背景线的颜色、宽度
        Paint background_line_paint=new Paint();
        background_line_paint.setStrokeWidth(adapter_min/300);
        background_line_paint.setColor(background_line_color);
        //记录竖直线的末端坐标
        List<float[]> endCoordinateList=new ArrayList<>();

        //画竖直的背景线
        for(int i=0;i<itemList.size();i++){
            //旋转的角度
            int sweep_angle=360/itemList.size()*i;
            //新的一条线的末端x,y的坐标点
            float move_width=0F;
            float move_height=0F;
            double angle=Math.toRadians(sweep_angle);
            move_height= (float) (Math.sin(angle)*radar_min);
            move_width= (float) (Math.cos(angle)*radar_min);
            float[] endCoordinateFloat=new float[2];
            endCoordinateFloat[0]=move_width;
            endCoordinateFloat[1]=move_height;
            endCoordinateList.add(endCoordinateFloat);
            //画线
            canvas.drawLine(0f,0f,move_width,move_height,background_line_paint);
        }

        //画环形的背景线时使其首尾相连
        endCoordinateList.add(endCoordinateList.get(0));
        //画环形的背景线
        for(int j=0;j<line_point;j++){
            //每一次环形距离原点的距离
            float last_circle_x=endCoordinateList.get(0)[0]/line_point*(j+1);
            float last_circle_y=endCoordinateList.get(0)[1]/line_point*(j+1);
            for(int k=1;k<endCoordinateList.size();k++){
                float next_circle_x=endCoordinateList.get(k)[0]/line_point*(j+1);
                float next_circle_y=endCoordinateList.get(k)[1]/line_point*(j+1);
                canvas.drawLine(last_circle_x,last_circle_y,next_circle_x,next_circle_y,background_line_paint);
                last_circle_x=next_circle_x;
                last_circle_y=next_circle_y;
            }

        }
        //移除多余的item,方便下面使用
        endCoordinateList.remove(endCoordinateList.size()-1);

        //text label的paint,设置其属性
        Paint end_text_paint=new Paint();
        end_text_paint.setStrokeWidth(5);
        end_text_paint.setTextAlign(Paint.Align.CENTER);
        end_text_paint.setTextSize(adapter_min/30);
        end_text_paint.setAntiAlias(true);
        end_text_paint.setStyle(Paint.Style.FILL);
//        end_text_paint.setFakeBoldText(true);
        //画每一个item的label text
        for(int q=0;q<endCoordinateList.size();q++){
//            end_text_paint.setColor(itemColorList.get(q));
            end_text_paint.setColor(Color.WHITE);
            int end_x= (int) endCoordinateList.get(q)[0];
            int end_y= (int) endCoordinateList.get(q)[1];
            String item_label=itemList.get(q);
            if(end_y==0){
                if(end_x>0){
                    end_x=end_x+item_label.length()/2+adapter_min/30;
                }else{
                    end_x=end_x-item_label.length()/2-adapter_min/30;
                }
            }else if(end_y>0){
                end_y=end_y+adapter_min/30;
            }else{
                end_y=end_y-adapter_min/100;
            }
            canvas.drawText(item_label,end_x,end_y,end_text_paint);
        }

        //画label的占比线的paint
        Paint proportion_line_paint=new Paint();
        proportion_line_paint.setStyle(Paint.Style.FILL);
        proportion_line_paint.setStrokeWidth(adapter_min/200);
        proportion_line_paint.setAntiAlias(true);
        //画label占比线背景的paint
        Paint proportion_line_background_paint=new Paint();
        proportion_line_background_paint.setStyle(Paint.Style.FILL);
        proportion_line_background_paint.setStrokeWidth(1);
        proportion_line_background_paint.setAntiAlias(true);
        for(int i=0;i<itemProportionList.size();i++){
            Path path=new Path();
            proportion_line_paint.setColor(itemProportionColorList.get(i));
            proportion_line_background_paint.setColor(itemProportionColorList.get(i));
            proportion_line_background_paint.setAlpha(50);
            float last_proportion_x=endCoordinateList.get(0)[0]* itemProportionList.get(i)[0];
            float last_proportion_y=endCoordinateList.get(0)[1]* itemProportionList.get(i)[0];
            path.moveTo(last_proportion_x,last_proportion_y);
            for(int j=1;j<itemProportionList.get(i).length;j++){
                float next_proportion_x=endCoordinateList.get(j)[0]* itemProportionList.get(i)[j];
                float next_proportion_y=endCoordinateList.get(j)[1]* itemProportionList.get(i)[j];
                canvas.drawLine(last_proportion_x,last_proportion_y,next_proportion_x,next_proportion_y,proportion_line_paint);
                path.lineTo(next_proportion_x,next_proportion_y);
                last_proportion_x=next_proportion_x;
                last_proportion_y=next_proportion_y;
            }
            //画label占比线的最后一条使其首尾相连
            canvas.drawLine(last_proportion_x,last_proportion_y,endCoordinateList.get(0)[0]* itemProportionList.get(i)[0],endCoordinateList.get(0)[1]* itemProportionList.get(i)[0],proportion_line_paint);
            path.lineTo(endCoordinateList.get(0)[0]* itemProportionList.get(i)[0],endCoordinateList.get(0)[1]* itemProportionList.get(i)[0]);
            canvas.drawPath(path,proportion_line_background_paint);
            path.close();
        }

        //标签栏的背景色
        Paint label_bar_background_paint=new Paint();
        label_bar_background_paint.setStrokeWidth(5);
        label_bar_background_paint.setColor(Color.WHITE);
        canvas.translate(0, (float) (height*0.5));
        canvas.drawRoundRect(-adapter_min/3,0,adapter_min/3,adapter_min/15,50,50,label_bar_background_paint);

        //画标签栏中的标签的第一个圆点
        Paint label_bar_label_paint=new Paint();
        label_bar_label_paint.setStrokeWidth(5);
        label_bar_label_paint.setColor(itemProportionColorList.get(0));
        canvas.drawCircle(-adapter_min/3+adapter_min/15,adapter_min/30,adapter_min/100,label_bar_label_paint);
        //画标签栏中的标签的第一个文本
        Paint label_bar_text_paint=new Paint();
        label_bar_text_paint.setStrokeWidth(3);
//        label_bar_text_paint.setTextAlign(Paint.Align.CENTER);
        label_bar_text_paint.setTextSize(adapter_min/60);
        label_bar_text_paint.setAntiAlias(true);
        label_bar_text_paint.setStyle(Paint.Style.FILL);
        label_bar_text_paint.setFakeBoldText(true);
        label_bar_text_paint.setColor(Color.BLACK);
        String label_text_1="个人正确率";
        canvas.drawText(label_text_1,-adapter_min/3+adapter_min/10,adapter_min/30+adapter_min/150,label_bar_text_paint);
        //画标签栏中的标签的第二个圆点
        Paint label_bar_label_paint2=new Paint();
        label_bar_label_paint2.setStrokeWidth(5);
        label_bar_label_paint2.setColor(itemProportionColorList.get(1));
        canvas.drawCircle(adapter_min/7,adapter_min/30,adapter_min/100,label_bar_label_paint2);
        //画标签栏中的标签的第二个文本
        Paint label_bar_text_paint2=new Paint();
        label_bar_text_paint2.setStrokeWidth(3);
//        label_bar_text_paint.setTextAlign(Paint.Align.CENTER);
        label_bar_text_paint2.setTextSize(adapter_min/60);
        label_bar_text_paint2.setAntiAlias(true);
        label_bar_text_paint2.setStyle(Paint.Style.FILL);
        label_bar_text_paint2.setFakeBoldText(true);
        label_bar_text_paint2.setColor(Color.BLACK);
        String label_text_2="平均正确率";
        canvas.drawText(label_text_2,adapter_min/7-adapter_min/15+adapter_min/10,adapter_min/30+adapter_min/150,label_bar_text_paint2);

        canvas.save();
    }
}
