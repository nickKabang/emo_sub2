package com.gon.emo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.content.FileProvider.getUriForFile;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    private static final int FROM_CAMERA = 0;
    private static final int FROM_GALLERY = 1;
    private static final int FROM_CROP = 2;

    private Uri uri;
    private String imageFilePath;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //각 버튼의 객체 생성
        ImageButton btnLoad          = findViewById(R.id.btn_load);
        ImageButton btnCamera          = findViewById(R.id.btn_camera);


        //카메라 호출시
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assume thisActivity is the current activity
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA);

                if(permissionCheck == PackageManager.PERMISSION_DENIED){
                    //권한 없음
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},0);
                }
                else {
                    String state = Environment.getExternalStorageState();

                    //외장메모리 검사
                    if(Environment.MEDIA_MOUNTED.equals(state)) {
                        sendTakePhotoIntent();

                    }


                    Log.d(this.getClass().getName(), "Camera");
                }
            }
        });

        //gallery 호출시
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, FROM_GALLERY);

                Log.d(this.getClass().getName(),"Gallery");


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch(requestCode) {
            // Gallery호출시 결과 처리
            case FROM_GALLERY:
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    try {
                        // 선택한 이미지에서 비트맵 생성
                        //                    InputStream in = getContentResolver().openInputStream(data.getData());
                        //                    img = BitmapFactory.decodeStream(in);
                        //                    in.close();

                        uri = data.getData();

                        Log.d(this.getClass().getName(), uri.toString());
                        Intent intent = new Intent(this, ActionActivity.class);

                        //intent.putExtra("imageUri", uri.toString());
                        intent.putExtra("imageUri", uri);
                        intent.putExtra("code", FROM_GALLERY);

                        startActivity(intent);

                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                //Camera호출시 결과 처리
            case FROM_CAMERA:
                // Make sure the request was successful

                if (resultCode == Activity.RESULT_OK) {
                    try {

                        ExifInterface exif = null;

                        try {
                            exif = new ExifInterface(imageFilePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int exifOrientation;
                        int exifDegree;

                        if (exif != null) {
                            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            exifDegree = exifOrientationToDegrees(exifOrientation);
                        } else {
                            exifDegree = 0;
                        }

                        //((ImageView)findViewById(R.id.photo)).setImageBitmap(rotate(bitmap, exifDegree));

                        Intent intent = new Intent(this, ActionActivity.class);

                        //intent.putExtra("imageUri", uri.toString());
                        intent.putExtra("imageUri", photoUri);
                        intent.putExtra("code", FROM_CAMERA);
                        intent.putExtra("exifDegree", exifDegree);

                        Log.d(this.getClass().getName(),"main result : "+exifDegree);

                        startActivity(intent);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;

                //CROP 처리
            case FROM_CROP:
                Log.d(this.getClass().getName(), "camera 1");
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {

                    final Bundle extras = data.getExtras();

                    //CROP된 이미지 저장 FILE 경로
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Emo/" + System.currentTimeMillis() + ".jpg";

                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");  //CROP된 BITAMP

                        storeCropImage(photo, filePath);

                        Intent intent = new Intent(this, ActionActivity.class);

                        //intent.putExtra("imageUri", uri.toString());
                        intent.putExtra("imageUri", uri);

                        startActivity(intent);

                        //임시파일 삭제
                        File tmpFile = new File(uri.getPath());

                        if (tmpFile.exists()) {
                            tmpFile.delete();
                        }
                    }
                }
        }
    }


    /*
     * Bitmap 저장
     */
    private void storeCropImage(Bitmap bitmap, String filePath){
        //Emo 폴더를 생성하여 이미지를 저장
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Emo";
        File directory_emo = new File(dirPath);

        //Emo 폴더가 없으면 새로 만듬
        if(!directory_emo.exists()){
            directory_emo.mkdir();
        }

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try{
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);

            //sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신.
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* 사진 생성 */
    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, FROM_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    /*
     * 사진 원래 위치로 변경
     */
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader cursorLoader = new CursorLoader(this, contentURI, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();


        Log.d(this.getClass().getName(),cursor.getString(column_index));

        return cursor.getString(column_index);

    }


}
