package com.gvtv.android.cloud.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

import com.gvtv.android.cloud.R;

public class AgreementDialog  extends Dialog{
	public static AgreementDialog mInputDialog;
	private Context mContext;
	public static AgreementDialog getInstance(Context context){
		if(mInputDialog == null || context != mInputDialog.mContext){
			mInputDialog = new AgreementDialog(context);
		}
		return mInputDialog;
	}
	
	private AgreementDialog(Context context) {
		super(context,R.style.inputDialogTheme);
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setContentView(R.layout.dialog_view_agreement);
		this.setCancelable(true);
		this.setCanceledOnTouchOutside(true);
		TextView tv = (TextView) findViewById(R.id.tv_agreement_content);
		//tv.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动  
        tv.setText(Html.fromHtml(getAgreementText(mContext)));
	}
	public String getAgreementText(Context context) {
		InputStream in = null;
		BufferedReader br;
		StringBuilder sb= new StringBuilder();
		Configuration con = context.getResources().getConfiguration();
		String country = con.locale.getCountry();
		String language = con.locale.getLanguage();
		String filename;
		if(language.equals("en")){
			filename = "agreement_en.html";
		}else if(language.equals("zh") && country.equals("CN")){
			filename = "agreement.html";
		}else if(language.equals("zh") && country.equals("TW")){
			filename = "agreement_tw.html";
		}else{
			filename = "agreement.html";
		}
		try {
			in = context.getAssets().open(filename);
			br= new BufferedReader(new InputStreamReader(in));
			String readLine= null;
			while((readLine= br.readLine())!=null){
				sb.append(readLine.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
