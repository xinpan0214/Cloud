package com.gvtv.android.cloud.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.util.StringUtils;
import com.gvtv.android.cloud.util.ToastUtil;

public class InputDialog  extends Dialog implements android.view.View.OnClickListener{
	private TextView tv_name, tv_cancel, tv_unbind;
	private Context mContext;
	/**
	 * @return the tv_name
	 */
	public TextView getTv_name() {
		return tv_name;
	}

	/**
	 * @return the tv_cancel
	 */
	public TextView getTv_cancel() {
		return tv_cancel;
	}

	/**
	 * @return the tv_unbind
	 */
	public TextView getTv_unbind() {
		return tv_unbind;
	}

	/**
	 * @return the et
	 */
	public EditText getEt() {
		return et;
	}

	private EditText et;
	private DialogListener listener;
	private String dev_name;
	
	public static InputDialog mInputDialog;
	
	public static final int TYPE_UNBIND = 0;
	public static final int TYPE_MODNAME = 1;
	
	private int type;
	/**
	 * @param listener the listener to set
	 */
	public void setListener(DialogListener listener) {
		this.listener = listener;
	}

	public interface DialogListener {
		public abstract void onDevnameChange(boolean isChanged, String dev_name);
	}
	
	public static InputDialog getInstance(Context context,int type){
		if(mInputDialog == null || context != mInputDialog.mContext){
			mInputDialog = new InputDialog(context);
		}
		mInputDialog.type = type;
		return mInputDialog;
	}
	
	private InputDialog(Context context) {
		super(context,R.style.inputDialogTheme);
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setContentView(R.layout.dialog_view_input);
		this.init();
	}

	private void init(){
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_cancel = (TextView) findViewById(R.id.cancel);
		tv_unbind = (TextView) findViewById(R.id.unbind);
		et = (EditText) findViewById(R.id.et);
		tv_cancel.setOnClickListener(this);
		tv_unbind.setOnClickListener(this);
	}
	
	public void setName(String name){
		tv_name.setText(name);
	}
	
	public void setNameInvisible(){
		tv_name.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		try {
			((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (v.getId()) {
		case R.id.cancel:
			dev_name = et.getText().toString().trim();
			if(listener != null){
				listener.onDevnameChange(false, dev_name);
			}
			break;
			
		case R.id.unbind:
			dev_name = et.getText().toString().trim();
			if(StringUtils.isBlank(dev_name)){
				if(type == TYPE_UNBIND){
					ToastUtil.getToastUtils().showToast(mContext, mContext.getResources().getString(R.string.please_input_pwd));
				}else if(type == TYPE_MODNAME){
					ToastUtil.getToastUtils().showToast(mContext, mContext.getResources().getString(R.string.please_input_device_name));
				}
			}else if(listener != null){
				listener.onDevnameChange(true, dev_name);
			}
			break;

		default:
			break;
		}
	}
	
}
