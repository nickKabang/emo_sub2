package com.gon.emo.control;


import android.graphics.Paint;
import android.graphics.Path;

public class PathInfo extends Path {
    private Paint paint;

    public PathInfo(){
        paint = new Paint();
    }

    public Paint getPaint(){
        return paint;
    }

    public void setPaint(Paint paint){
        this.paint = paint;
    }

}



