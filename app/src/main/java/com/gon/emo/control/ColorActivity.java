package com.gon.emo.control;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gon.emo.BuildConfig;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class ColorActivity extends AppCompatActivity implements ColorPickerDialogListener {
    /**
     * 생성이 될 색상 선택 다이어로그에 고유 ID 관리를 위함
     */
    private static final int DIALOG_DEFAULT_ID = 0;
    private static final int DIALOG_PRESET_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(DIALOG_DEFAULT_ID)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this);
    }


    public void onColorSelected(int dialogId, final int color) {

        final int invertColor = ~color;
        final String hexColor = String.format("%X", color);
        String hexInvertColor = String.format("%X", invertColor);
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "id " + dialogId + " c: " + hexColor + " i:" + hexInvertColor, Toast.LENGTH_SHORT).show();
        }

        // Intent 객체 생성.
        Intent intent = new Intent() ;

        intent.putExtra("color", color) ;

        setResult(RESULT_OK, intent) ;

        finish();

    }


    public void onDialogDismissed(int dialogId) {

    }
}



