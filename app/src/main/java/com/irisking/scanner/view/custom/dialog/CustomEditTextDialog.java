package com.irisking.scanner.view.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.irisking.irisapp.R;

/**
 * Created by Administrator on 2017/10/17.
 */

public class CustomEditTextDialog extends Dialog {
	
	private Button mCancelBtn;//取消按钮
	private Button mConfirmBtn;//确定按钮
	private String messageStr;//从外界设置的消息文本
	private EditText messageEd;
	private String getInptuText;
	//确定文本和取消文本的显示内容
	private String cancelStr, confirmStr;
	
	private onCancelOnclickListener cancelOnclickListener;//取消按钮被点击了的监听器
	private onConfirmOnclickListener confirmOnclickListener;//删除按钮被点击了的监听器
	
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
	
	public CustomEditTextDialog(Context context) {
		super(context, R.style.MyDialog);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_message_edit);
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
                hideInput(getContext(), messageEd);
				if (confirmOnclickListener != null) {
					confirmOnclickListener.onConfirmClick();
				}
			}
		});
		//设置取消按钮被点击后，向外界提供监听
		mCancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideInput(getContext(), messageEd);
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
			messageEd.setText(messageStr);
			messageEd.setSelection(messageEd.getText().length());
		}
		//如果设置按钮的文字
		if (confirmStr != null) {
			mConfirmBtn.setText(confirmStr);
		}
		if (cancelStr != null) {
			mCancelBtn.setText(cancelStr);
		}
	}
	
	/**
	 * 初始化界面控件
	 */
	private void initView() {
		mConfirmBtn = (Button) findViewById(R.id.btn_confirm);
		mCancelBtn = (Button) findViewById(R.id.btn_cancel);
		messageEd = (EditText) findViewById(R.id.ed_userName);
	}
	
	/**
	 * 从外界Activity为Dialog设置dialog的message
	 *
	 * @param message
	 */
	public void setMessage(String message) {
		messageStr = message;
	}
	
	public String getMessage() {
		
		if (confirmOnclickListener != null) {
			getInptuText = messageEd.getText().toString().trim();
			
//			if (getInptuText.equals(messageStr)) {
//				Toast.makeText(getContext(), "两次用户名相同，请重新输入！", Toast.LENGTH_SHORT).show();
//			}
		}
		return getInptuText;
	}
	
	/**
	 * 设置确定按钮和取消被点击的接口
	 */
	public interface onConfirmOnclickListener {
		void onConfirmClick();
	}
	
	public interface onCancelOnclickListener {
		void onCancelClick();
	}

	/**
	 * 切换软键盘的状态
	 * 如当前为收起变为弹出,若当前为弹出变为收起
	 */
	public void toggleInput(Context context){
		InputMethodManager inputMethodManager =
				(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 强制隐藏输入法键盘
	 */
	public void hideInput(Context context,View view){
		InputMethodManager inputMethodManager =
				(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
}
