package com.irisking.scanner.view.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.irisking.irisapp.R;

/**
 * Created by Administrator on 2017/10/19.
 */

public class CustomPromptDialog extends Dialog{
    private Button mConfirmBtn;//确定按钮
//    private TextView titleTv;//标题提示文本
    private TextView messageTv;//消息提示文本
//    private String titleStr;//从外界设置的标题文本
    private String messageStr;//从外界设置的消息文本
    //确定文本和取消文本的显示内容
    private String confirmStr;

    private onConfirmOnclickListener confirmOnclickListener;//确定按钮被点击了的监听器

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param confirmOnclickListener
     */
    public void setConfirmOnclickListener(String str, onConfirmOnclickListener confirmOnclickListener) {
        if (str != null) {
            confirmStr = str;
        }
        this.confirmOnclickListener = confirmOnclickListener;
    }

//    public CustomAlertDialog(@NonNull Context context) {
//        super(context);
//    }

    public CustomPromptDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_prompt_alert);
//        //按空白处不能取消动画
//        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmOnclickListener != null) {
                    confirmOnclickListener.onConfirmClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
//        if (titleTv != null) {
//            titleTv.setText(titleStr);
//        }
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
        //如果设置按钮的文字
        if (confirmStr != null) {
            mConfirmBtn.setText(confirmStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
//        titleTv = findViewById(R.id.title);
        messageTv = (TextView) findViewById(R.id.message);
        mConfirmBtn = (Button) findViewById(R.id.btn_delete);
    }

    /**
     * 从外界Activity为Dialog设置dialog的title
     *
     * @param title
     */
//    public void setTitle(String title) {
//        this.titleStr = title;
//    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        this.messageStr = message;
    }

    /**
     * 设置确定按钮被点击的接口
     */
    public interface onConfirmOnclickListener {
        void onConfirmClick();
    }
}
