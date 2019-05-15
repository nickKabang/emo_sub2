package com.gon.emo.control;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GraphicView extends View {
    private float scaleX = 1, scaleY = 1, angle = 0, color = 1, satur = 1;
    private Bitmap pic;


    public GraphicView(Context context, Bitmap img) {

        super(context);

        //Bitmap을 매개변수로 넘김
        pic = img;

    }



    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);


        Log.d(this.getClass().getName(), " on Draw ");


        int picX = (this.getWidth()-pic.getWidth())/2;

        int picY = (this.getHeight()- pic.getHeight())/2;



        int cenX = this.getWidth()/2;

        int cenY = this.getHeight()/2;



        Paint paint = new Paint();

        float[] array = {color, 0, 0, 0, 0,

                0, color, 0, 0, 0,

                0, 0, color, 0, 0,

                0, 0, 0, 1, 0};

        ColorMatrix cm = new ColorMatrix(array);

        if(satur==0) cm.setSaturation(satur);



        paint.setColorFilter(new ColorMatrixColorFilter(cm));



        canvas.scale(scaleX, scaleY, cenX, cenY);

        canvas.rotate(angle, cenX, cenY);

        canvas.drawBitmap(pic, picX, picY, paint);


    }

    public void setScaleX(float scaleX){
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY){
        this.scaleY = scaleY;
    }

    public void setAngle(float angle){
        this.angle = angle;
    }

    public void setColor(float color){
        this.color = color;
    }

    public void setSatur(float satur){
        this.satur = satur;
    }

    public float getScaleX(){
        return scaleX;
    }

    public float getScaleY(){
        return scaleY;
    }

    public float getAngle(){
        return angle;
    }

    public float getColor(){
        return color;
    }

    public float getSatur(){
        return satur;
    }

}



