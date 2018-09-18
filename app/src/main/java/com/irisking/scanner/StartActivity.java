package com.irisking.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.irisking.irisapp.R;


//主文件，完成界面显示，UI控件控制等逻辑
public class StartActivity extends Activity {

    private int mCameraId;

    private RadioButton frontRadioBtn;
    private RadioButton rearRadioBtn;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iris_start);

        frontRadioBtn = findViewById(R.id.front_camera_id);
        rearRadioBtn = findViewById(R.id.rear_camera_id);
        confirmBtn = findViewById(R.id.btn_ok);

        frontRadioBtn.setChecked(true);
        mCameraId = 3;

        frontRadioBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCameraId = 3;
            }
        });

        rearRadioBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCameraId = 2;
            }
        });

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				Log.e("tony", "选中的摄像头ID: " + mCameraId);

                Camera camera;
                try {
                        camera = Camera.open(mCameraId);
                } catch (Exception e) {
                    Toast.makeText(StartActivity.this, "无法找到CameraID " + mCameraId + " 对应的设备，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                } finally {
                }

                if (camera != null) {
                    camera.release();
                }

                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("cameraid", mCameraId);
                intent.putExtra("width", 1920);
                intent.putExtra("height", 1080);
                startActivity(intent);


            }
        });
    }

    /**
     * 当EditText输入完毕后，点击空白区域隐藏软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideKeyboard(view, ev)) {
                hideKeyboard(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideKeyboard(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] location = {0, 0};
            view.getLocationInWindow(location);
            int left = location[0],
                    top = location[1],
                    bottom = top + view.getHeight(),
                    right = left + view.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
