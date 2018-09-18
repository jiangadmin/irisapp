package com.irisking.scanner.view.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.irisking.irisapp.R;

/**
 * Created by Administrator on 2017/10/17.
 */

public class CustomAlertDialog extends Dialog {

    private Button mCancelBtn;//取消按钮
    private Button mDeletelBtn;//删除按钮
    private TextView messageTv;//消息提示文本
    private String messageStr;//从外界设置的消息文本
    //确定文本和取消文本的显示内容
    private String cancelStr, deleteStr;

    private onCancelOnclickListener cancelOnclickListener;//取消按钮被点击了的监听器
    private onDeleteOnclickListener deleteOnclickListener;//删除按钮被点击了的监听器

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param cancelOnclickListener
     */
    public void setCancelOnclickListener(String str, onCancelOnclickListener cancelOnclickListener) {
        if (str != null) {
            cancelStr = str;
        }
        this.cancelOnclickListener = cancelOnclickListener;
    }

    /**
     * 设置删除按钮的显示内容和监听
     *
     * @param str
     * @param deleteOnclickListener
     */
    public void setDeleteOnclickListener(String str, onDeleteOnclickListener deleteOnclickListener) {
        if (str != null) {
            deleteStr = str;
        }
        this.deleteOnclickListener = deleteOnclickListener;
    }

//    public CustomAlertDialog(@NonNull Context context) {
//        super(context);
//    }

    public CustomAlertDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message_alert);
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
        //设置删除按钮被点击后，向外界提供监听
        mDeletelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteOnclickListener != null) {
                    deleteOnclickListener.onDeleteClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelOnclickListener != null) {
                    cancelOnclickListener.onCancelClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
        //如果设置按钮的文字
        if (deleteStr != null) {
            mDeletelBtn.setText(deleteStr);
        }
        if (cancelStr != null) {
            mCancelBtn.setText(cancelStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        mDeletelBtn = (Button) findViewById(R.id.btn_delete);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel);
        messageTv = (TextView) findViewById(R.id.message);
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onDeleteOnclickListener {
        void onDeleteClick();
    }

    public interface onCancelOnclickListener {
        void onCancelClick();
    }
}
