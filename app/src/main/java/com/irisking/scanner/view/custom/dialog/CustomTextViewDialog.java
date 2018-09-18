package com.irisking.scanner.view.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.irisking.irisapp.R;

/**
 * Created by Administrator on 2017/10/17.
 */

public class CustomTextViewDialog extends Dialog {

    private TextView messageTv;//消息提示文本
    private String messageStr;//从外界设置的消息文本

    public CustomTextViewDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message_default);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();

    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        messageTv = (TextView) findViewById(R.id.message);
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }
}
