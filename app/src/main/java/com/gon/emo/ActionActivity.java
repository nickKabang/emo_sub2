package com.gon.emo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gon.emo.control.ColorActivity;
import com.gon.emo.control.GraphicView;
import com.gon.emo.control.LineView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActionActivity extends AppCompatActivity implements View.OnTouchListener {
    static final int GET_DATA_REQUEST = 1;

    private float scaleX = 1, scaleY = 1, angle = 0, color = 1, satur = 1;
    private static final int FROM_CAMERA = 0;
    private static final int FROM_GALLERY = 1;

    //static int paintColor = 0xFFa68b1f;
    private int paintColor = 0xFFa68b1f;
    private float r = 5f;

    private Bitmap pic;
    private Bitmap pic2;
    private int intGb;
    private int exifDegree;
    private int editTextCnt = 0;

    float oldXvalue;
    float oldYvalue;

    private FrameLayout fraLayout;
    private GraphicView gView;
    private LineView lineView;

    private EditText editText_main;

    private Toolbar myToolbar;

    private View bottomMain;
    private View textSub;
    private View penSub;
    private View styleSub;
    private SeekBar sbText;
    private SeekBar sbPen;
    private TextView tvSeek;
    private TextView tvPenSeek;
    LinearLayout llTextSeekBak;
    LinearLayout llPenSeekBak;
    LinearLayout llLineSample;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.action_main);

        // 추가된 소스, Toolbar를 생성한다.
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ImageView ibZoomIn = findViewById(R.id.ibZoomin);
        ImageView ibZoomOut = findViewById(R.id.ibZoomout);
        ImageView ibBright = findViewById(R.id.ibBright);
        ImageView ibDark = findViewById(R.id.ibDark);
        ImageView ibGray = findViewById(R.id.ibGray);
        bottomMain = findViewById(R.id.icBottom);
        textSub = findViewById(R.id.icTextSub);
        penSub = findViewById(R.id.icPenSub);
        styleSub = findViewById(R.id.icStyleSub);

        ImageView ibEraser = findViewById(R.id.ivEraser);
        ImageView ivText = findViewById(R.id.ivText);
        ImageView ivPen = findViewById(R.id.ivPen);
        ImageView ivStyle = findViewById(R.id.ivStyle);


        ImageView ivTextColor = findViewById(R.id.ivTextColor);
        ImageView ivTextBack = findViewById(R.id.ivTextBack);
        ImageView ivTextThickness = findViewById(R.id.ivTextThickness);
        ImageView ivTextPlus = findViewById(R.id.ivTextPlus);
        ImageView ivTextDelete = findViewById(R.id.ivTextDelete);
        llTextSeekBak = findViewById(R.id.llTextSeekBak);
        sbText = findViewById(R.id.sbText);
        tvSeek = findViewById(R.id.tvSeek);

        ImageView ivPenColor = findViewById(R.id.ivPenColor);
        ImageView ivPenThickness = findViewById(R.id.ivPenThickness);
        ImageView ivPenBack = findViewById(R.id.ivPenBack);
        llPenSeekBak = findViewById(R.id.llPenSeekBak);
        sbPen = findViewById(R.id.sbPen);
        tvPenSeek = findViewById(R.id.tvPenSeek);
        llLineSample = findViewById(R.id.llLineSample);

        ImageView ivStyleBack = findViewById(R.id.ivStyleBack);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("imageUri");
        intGb = intent.getIntExtra("code", intGb);
        exifDegree = intent.getIntExtra("exifDegree", exifDegree);

        fraLayout = findViewById(R.id.mainLayout);

        try {
            pic = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (intGb == FROM_CAMERA) {
            pic2 = rotate(pic, exifDegree);
            pic2 = Bitmap.createBitmap(rotate(pic, exifDegree)).copy(Bitmap.Config.ARGB_8888, true);
        } else {
            pic2 = Bitmap.createBitmap(pic).copy(Bitmap.Config.ARGB_8888, true);
        }

        //Canvas를 사용하기 위한 View 생성
        //gView = (MyGraphicView) new MyGraphicView(this, pic2);
        gView = (GraphicView) new GraphicView(this, pic2);

        //Layout에 맞게 설정
        gView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        /*
         * LineView 생성 및 설정
         */
        //화면에 선을 그리기 위한 LineView 설정
        lineView = new LineView(this);
        //Layout에 맞게 설정
        lineView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        lineView.setPaintInfo(paintColor, r);

        //Canvas View를 Layout에 넣는다.
        fraLayout.addView(gView);
        fraLayout.addView(lineView);


        /**
         * 각 버튼의 이벤트 처리
         */
        //8.지우개
        /**@@ TO-DO 정상동작 안함... 봐야 함 */
        ibEraser.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                paintColor = 000000;
                lineView.setPaintInfo(paintColor, r);

            }

        });


        /** 텍스트 ************************/
        ivText.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(this.getClass().getName(), "******** Text Change  ");
                bottomMain.setVisibility(View.GONE);
                textSub.setVisibility(View.VISIBLE);

            }

        });

        /** 텍스트 Sub ************************/
        //텍스트 Sub - 컬러
        ivTextColor.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d(this.getClass().getName(), "******** TextSub Color  ");
                llTextSeekBak.setVisibility(View.GONE);

            }

        });

        //텍스트 Sub - 두께
        ivTextThickness.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d(this.getClass().getName(), "******** TextSub 두께  ");
                llTextSeekBak.setVisibility(View.VISIBLE);
            }

        });

        //텍스트 Sub - 두께 슬라이드
        sbText.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                //tv.setText("onStop TrackingTouch");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //tv.setText("onStart TrackingTouch");
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                //tvSeek.setText(progress);
//                if (progress < 10) {
//                    progress = 10;
//                    sbText.setProgress(progress);
//                }

                tvSeek.setText(String.valueOf(progress));
                editText_main.setTextSize((float)progress);

                // WindowManager.LayoutParams params = getWindow().getAttributes();
                //params.screenBrightness = (float) progress / 100;
                //getWindow().setAttributes(params);
            }
        });


        //텍스트 Sub - 추가
        ivTextPlus.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                editText_main = new EditText(ActionActivity.this);

                editText_main.setId(editTextCnt);
                editText_main.setText("message");
                editText_main.setTextSize(30);

                editText_main.setBackgroundResource(R.drawable.dot);

                editText_main.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                editText_main.setOnTouchListener(ActionActivity.this);

                editTextCnt = editTextCnt + 1;

                fraLayout.addView(editText_main);

            }

        });

        //텍스트 Sub - 삭제
        ivTextDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d(this.getClass().getName(), "******** TextSub 삭제  ");

            }

        });


        //텍스트 Back
        ivTextBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                bottomMain.setVisibility(View.VISIBLE);
                textSub.setVisibility(View.GONE);
                llTextSeekBak.setVisibility(View.GONE);
            }

        });


        /** 펜 ************************/
        ivPen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                bottomMain.setVisibility(View.GONE);
                penSub.setVisibility(View.VISIBLE);
            }

        });

        /** 펜 Sub ************************/
        //펜 Sub - 컬러
        ivPenColor.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d(this.getClass().getName(), "******** PenSub Color  ");
                Intent intent = new Intent(ActionActivity.this, ColorActivity.class);
                startActivityForResult(intent, GET_DATA_REQUEST);
                llPenSeekBak.setVisibility(View.GONE);
            }

        });

        //펜 Sub - 두께
        ivPenThickness.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d(this.getClass().getName(), "******** PenSub Thickness  ");
                llPenSeekBak.setVisibility(View.VISIBLE);
            }

        });

        //펜 Sub - 두께 슬라이드
        sbPen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                //tv.setText("onStop TrackingTouch");
                Log.d(this.getClass().getName(), "********  Thickness 111 ");
                llLineSample.setVisibility(View.GONE);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //tv.setText("onStart TrackingTouch");
                Log.d(this.getClass().getName(), "********  Thickness 222 ");
                llLineSample.bringToFront();
                llLineSample.setVisibility(View.VISIBLE);
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                //tvSeek.setText(progress);
//                if (progress < 10) {
//                    progress = 10;
//                    sbPen.setProgress(progress);
//                }

                tvPenSeek.setText(String.valueOf(progress));


                llLineSample.getLayoutParams().height = progress;
                Log.d(this.getClass().getName(), "********  height : " + llLineSample.getLayoutParams().height);
                lineView.setPaintInfo(paintColor, progress);

                // WindowManager.LayoutParams params = getWindow().getAttributes();
                //params.screenBrightness = (float) progress / 100;
                //getWindow().setAttributes(params);
            }
        });


        //펜 Back
        ivPenBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                bottomMain.setVisibility(View.VISIBLE);
                penSub.setVisibility(View.GONE);
                llPenSeekBak.setVisibility(View.GONE);
            }

        });


        /** 스타일 ************************/
        ivStyle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                bottomMain.setVisibility(View.GONE);
                styleSub.setVisibility(View.VISIBLE);
            }

        });

        /** 스타일 Sub ************************/
        //1. 사진 확대
        ibZoomIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                scaleX = gView.getScaleX();
                scaleY = gView.getScaleY();

                gView.setScaleX(scaleX + 0.1f);
                gView.setScaleY(scaleY + 0.1f);

                gView.invalidate();

            }

        });

        //2. 사진 축소
        ibZoomOut.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                scaleX = gView.getScaleX();
                scaleY = gView.getScaleY();

                gView.setScaleX(scaleX - 0.1f);
                gView.setScaleY(scaleY - 0.1f);

                gView.invalidate();

            }

        });


        //4. 밝게
        ibBright.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                color = gView.getColor();

                gView.setColor(color + 0.1f);

                gView.invalidate();

            }

        });

        //5. 어둡게
        ibDark.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                color = gView.getColor();

                gView.setColor(color - 0.1f);

                gView.invalidate();


            }

        });

        //6. 흑백
        ibGray.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                satur = gView.getSatur();

                if (satur == 0) {
                    gView.setSatur(1);
                } else {
                    gView.setSatur(0);
                }
                gView.invalidate();

            }

        });


        //스타일\ Back
        ivStyleBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                bottomMain.setVisibility(View.VISIBLE);
                styleSub.setVisibility(View.GONE);
            }

        });
        /** 버튼 이벤트 처리 완료 */
    }

    /**
     *
     * 상단바를 이용한 처리
     * 1. Save 처리
     * @return
     */
    //상단바 액션 사용
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //상단바 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            //저장 이벤트 처리
            case R.id.menu_save:

                //전체화면
                View rootView = getWindow().getDecorView();

                File screenShot = ScreenShot(rootView);
                if (screenShot != null) {
                    //갤러리에 추가
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                }

                Log.d(this.getClass().getName(), "Toolbar Save");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //화면 캡쳐하기
    public File ScreenShot(View view) {


        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

        String filename = "emo_" + System.currentTimeMillis() + ".png";
        File file = new File(Environment.getExternalStorageDirectory() + "/Pictures", filename);  //Pictures폴더 screenshot.png 파일
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            screenBitmap = resizeBitmapImageFn(screenBitmap, 360);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
            os.close();
            Toast.makeText(this, "이모티콘 저장됨", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }


    public Bitmap resizeBitmapImageFn(

            Bitmap bmpSource, int maxResolution){

        int iWidth = bmpSource.getWidth();      //비트맵이미지의 넓이

        int iHeight = bmpSource.getHeight();     //비트맵이미지의 높이

        int newWidth = iWidth ;

        int newHeight = iHeight ;

        float rate = 0.0f;



        //이미지의 가로 세로 비율에 맞게 조절

        if(iWidth > iHeight ){

            if(maxResolution < iWidth ){

                rate = maxResolution / (float) iWidth ;

                newHeight = (int) (iHeight * rate);

                newWidth = maxResolution;

            }

        }else{

            if(maxResolution < iHeight ){

                rate = maxResolution / (float) iHeight ;

                newWidth = (int) (iWidth * rate);

                newHeight = maxResolution;

            }

        }



        return Bitmap.createScaledBitmap(

                bmpSource, newWidth, newHeight, true);

    }



    /** 상단바 처리 완료 */

    /**
     * 이미지가 회전 하였을 경우
     * 원래 이미지 위치로 바꿔주기 위한 처리
     * @param bitmap
     * @param degree
     * @return
     */
    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //Activity에 대한 결과값 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        //color에 대한 처리결과
        if (requestCode == GET_DATA_REQUEST) {
            if (resultCode == RESULT_OK) {

                paintColor = intent.getIntExtra("color", paintColor);
                lineView.setPaintInfo(paintColor, r);
            }
        }
    }


    @Override
    /*
     * Text 이벤트
     */
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(this.getClass().getName(), " EditText On Touch ");
        Log.d(this.getClass().getName(), " ID : " + v.getId());

        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            oldXvalue = event.getX();
            oldYvalue = event.getY();
            //  Log.i("Tag1", "Action Down X" + event.getX() + "," + event.getY());

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            v.setX(event.getRawX() - oldXvalue);
            v.setY(event.getRawY() - (oldYvalue + v.getHeight()));
            //  Log.i("Tag2", "Action Down " + me.getRawX() + "," + me.getRawY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            if (v.getX() > width && v.getY() > height) {
                v.setX(width);
                v.setY(height);
            } else if (v.getX() < 0 && v.getY() > height) {
                v.setX(0);
                v.setY(height);
            } else if (v.getX() > width && v.getY() < 0) {
                v.setX(width);
                v.setY(0);
            } else if (v.getX() < 0 && v.getY() < 0) {
                v.setX(0);
                v.setY(0);
            } else if (v.getX() < 0 || v.getX() > width) {
                if (v.getX() < 0) {
                    v.setX(0);
                    v.setY(event.getRawY() - oldYvalue - v.getHeight());
                } else {
                    v.setX(width);
                    v.setY(event.getRawY() - oldYvalue - v.getHeight());
                }
            } else if (v.getY() < 0 || v.getY() > height) {
                if (v.getY() < 0) {
                    v.setX(event.getRawX() - oldXvalue);
                    v.setY(0);
                } else {
                    v.setX(event.getRawX() - oldXvalue);
                    v.setY(height);
                }
            }


        }
        return true;
    }
}

