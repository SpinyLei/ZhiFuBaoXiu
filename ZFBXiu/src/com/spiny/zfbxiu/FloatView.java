package com.spiny.zfbxiu;

import java.io.DataOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class FloatView extends LinearLayout {

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private float stateBarHeight = 0;
    private Button onekeyBtn;
    private WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService(
            Context.WINDOW_SERVICE);
    private WindowManager.LayoutParams wmParams = ((MyApplication) getContext().getApplicationContext())
            .getMywmParams();

    public FloatView(Context context) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
        stateBarHeight = ScreenUtil.getStatusBarHeight(context);
        onekeyBtn = new Button(context);
        onekeyBtn.setText("开始咻");
        onekeyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String txt = onekeyBtn.getText().toString();
                if ("开始咻".equals(txt)) {
                    startTap();
                } else if ("结束咻".equals(txt)) {
                    endTap();
                }
            }
        });
        onekeyBtn.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                String txt = onekeyBtn.getText().toString();
                if(null!=MainActivity.instance&&"开始咻".equals(txt)){
                    MainActivity.instance.showOrCancelFloatingView();
                }
                return true;
            }
        });
        addView(onekeyBtn);
    }

    boolean isTapping = false;
    boolean goonTapping = false;
    Handler endHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            onekeyBtn.setText("开始咻");
            isTapping = false;
            goonTapping=false;
            onekeyBtn.setEnabled(true);
        };
    };
    public void startTap() {
        if(null==MainActivity.instance){
            onekeyBtn.setText("请重启程序(杀掉后再打开窗口)，Activity==NULL");
            onekeyBtn.setEnabled(false);
            return;
        }
        if (!isTapping) {
            isTapping = true;
            goonTapping = true;
            onekeyBtn.setText("结束咻");
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (goonTapping) {
                        if(null!=MainActivity.instance){
                            adbClick(MainActivity.instance.getX(), MainActivity.instance.getY(),250);
                        }
                    }
                    endHandler.sendEmptyMessage(0);
                }
            }).start();
        }
    }

    public void endTap() {
        goonTapping=false;
        onekeyBtn.setEnabled(false);
    }

    public void adbClick(String x, String y) {
        exec("input tap " + x + " " + y);
    }

    public void adbClick(String x, String y, long sleepTime) {
        sleep(sleepTime);
        adbClick(x, y);
        sleep(sleepTime);
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void exec(String cmd) {
        Process process = null;
        OutputStream out = null;
        try {
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            out.close();
            int value = process.waitFor();
            if (0 == value) {
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    int width, height;
    boolean isIntercept = false;
    float interceptX = 0;
    float interceptY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            interceptX = ev.getRawX();
            interceptY = ev.getRawY();
            mTouchStartX = ev.getX();
            mTouchStartY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            if (Math.abs(ev.getRawX() - interceptX) > 10 || Math.abs(ev.getRawY() - interceptY) > 10) {
                isIntercept = true;
            }
            break;
        case MotionEvent.ACTION_UP:
            mTouchStartX = mTouchStartY = 0;
            interceptX = interceptY = 0;
            isIntercept = false;
            break;
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getRawX();
        y = event.getRawY() - stateBarHeight;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mTouchStartX = event.getX();
            mTouchStartY = event.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            updateViewPosition();
            break;
        case MotionEvent.ACTION_UP:
            updateViewPosition();
            mTouchStartX = mTouchStartY = 0;
            interceptX = interceptY = 0;
            isIntercept = false;
            break;
        }
        return true;
    }

    private void updateViewPosition() {
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        wm.updateViewLayout(this, wmParams);
    }
}
