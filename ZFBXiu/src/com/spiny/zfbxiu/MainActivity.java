package com.spiny.zfbxiu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout.LayoutParams;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams param;
    private static FloatView mLayout;
    private Button startBtn;
    private EditText xEt, yEt;
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        instance = this;
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        findView();
        init();
    }

    private void findView() {
        startBtn = (Button) findViewById(R.id.start_btn);
        xEt = (EditText) findViewById(R.id.x_et);
        yEt = (EditText) findViewById(R.id.y_et);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelFloatingView();
    }

    private void init() {
        int x = ScreenUtil.getScreenWidth(this) / 2;
        int y = ScreenUtil.getScreenTotalHeight(this) / 2;
        xEt.setText(x + "");
        yEt.setText(y + "");
        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showOrCancelFloatingView();
                Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
                mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(mHomeIntent);
            }
        });
    }

    public String getX() {
        String result = "0";
        if (null != xEt) {
            String txt = xEt.getText().toString().trim();
            if (!TextUtils.isEmpty(txt)) {
                try {
                    result = Integer.valueOf(txt).intValue() + "";
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "0";
                }
            }
        }
        return result;
    }

    public String getY() {
        String result = "0";
        if (null != yEt) {
            String txt = yEt.getText().toString().trim();
            if (!TextUtils.isEmpty(txt)) {
                try {
                    result = Integer.valueOf(txt).intValue() + "";
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "0";
                }
            }
        }
        return result;
    }

    public void showOrCancelFloatingView() {
        if (null == mLayout) {
            mLayout = new FloatView(getApplicationContext());
            if (null == mLayout.getParent()) {
                param = ((MyApplication) getApplication()).getMywmParams();
                param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                param.format = 1;
                param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                param.alpha = 1.0f;
                param.gravity = Gravity.LEFT | Gravity.TOP;
                param.x = 0;
                param.y = 0;
                param.width = LayoutParams.WRAP_CONTENT;
                param.height = LayoutParams.WRAP_CONTENT;
                mWindowManager.addView(mLayout, param);
            } else {
                mWindowManager.removeView(mLayout);
            }
        } else {
            if (null == mLayout.getParent()) {
                param = ((MyApplication) getApplication()).getMywmParams();
                param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                param.format = 1;
                param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                param.alpha = 1.0f;
                param.gravity = Gravity.LEFT | Gravity.TOP;
                param.x = 0;
                param.y = 0;
                param.width = LayoutParams.WRAP_CONTENT;
                param.height = LayoutParams.WRAP_CONTENT;
                mWindowManager.addView(mLayout, param);
            } else {
                mWindowManager.removeView(mLayout);
            }
        }
    }

    public void cancelFloatingView() {
        if (null != mLayout && null != mLayout.getParent()) {
            mWindowManager.removeView(mLayout);
        }
    }
}
