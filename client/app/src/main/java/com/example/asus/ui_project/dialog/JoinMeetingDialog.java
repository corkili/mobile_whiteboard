package com.example.asus.ui_project.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.example.asus.ui_project.R;


public class JoinMeetingDialog extends Dialog {
	//退出提示框
    //两个构造方法
	public JoinMeetingDialog(Context context) {
		super(context);
	}

	public JoinMeetingDialog(Context context, int theme) {
		super(context, theme);
	}
    //内部静态方法Builder
	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private EditText meetingId;//会议编号
		private EditText meetingPassword;//会议密码
		private String id="";//会议编号，返回给主界面的String
		private String passwrod="";//会议密码，返回给主界面的String
		private View contentView;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}//构造方法，需要传入一个场景（上下文）

		public String getId(){
			return this.id;
		}//返回会议名称

		public String getPasswrod(){
			return this.passwrod;
		}//返回会议密码


		//设置确认按钮,String
		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		//设置取消按钮,String
		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		//create函数，创建dialog
		public JoinMeetingDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final JoinMeetingDialog dialog = new JoinMeetingDialog(context,R.style.Dialog);
			dialog.setCancelable(false);//不能被关闭,getfocus
			View layout = inflater.inflate(R.layout.dialog_edit2_layout, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			meetingId=(EditText) layout.findViewById(R.id.id_of_meeting);//初始化对话框中的EditText
			meetingPassword=(EditText)layout.findViewById(R.id.password1_of_meeting);//初始化对话框中的EditText
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton_edit2)).setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton_edit2)).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							id=meetingId.getText().toString();//获取框中的值
							passwrod=meetingPassword.getText().toString();//获取框中的值
							positiveButtonClickListener.onClick(dialog,
									DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton_edit2).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton_edit2)).setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton_edit2)).setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton_edit2).setVisibility(View.GONE);
			}
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
