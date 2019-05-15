package com.gon.emo.control;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LineView extends View {
   //붓에 해당하는 paint 클래스 변수 선언
    private Paint paint;
    private float x, y, r=5;

    //위치 값들을 저장하기 위한 List 생성
    private ArrayList<PathInfo> data = new ArrayList<PathInfo>();
    private PathInfo pathInfo;


    public LineView(Context context){
        super(context);

        //paint 클래스 선언 및 초기값 설정
        paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(5f);
    }

    //색이나 두께가 변할 때마다 독립적으로 Paint와 r를 설정
    public void setPaintInfo(int color, float r){
        paint = new Paint();
        paint.setColor(color);

        //선 설정
        paint.setStyle(Paint.Style.STROKE);

        // 선 두께 설정
        paint.setStrokeWidth(r);

        pathInfo = new PathInfo();
        pathInfo.setPaint(paint);

    }

    @Override
    protected  void onDraw(Canvas canvas){

        // 저장된 포인트 그리기
        for(PathInfo p: data){
            canvas.drawPath(p, p.getPaint());
            //canvas.drawCircle(p.getX(), p.getY(), p.getR(), p.getPaint());
        }

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent evnet){

        switch(evnet.getAction()){
            //View가 눌렸다면
            case MotionEvent.ACTION_DOWN:
                pathInfo.moveTo(evnet.getX(), evnet.getY());
                break;
            //View를 누르고 이동했다면
            case MotionEvent.ACTION_MOVE:
                pathInfo.lineTo(evnet.getX(), evnet.getY());
                break;
            //View에서 터치를 떼었다면
            case MotionEvent.ACTION_UP:
                break;
        }

        data.add(pathInfo);

        //화면을 강제로 그리기 위한 메소드 호출
        invalidate();

        return true;
    }

}



