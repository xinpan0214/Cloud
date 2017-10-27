package com.gvtv.android.cloud.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.activity.FileActivity;
import com.gvtv.android.cloud.bean.VideoInfo;
import com.gvtv.android.cloud.util.TimeUtils;
public class FileAdapter extends BaseAdapter {

	private FileActivity mContext;
	private List<VideoInfo> mVideoInfos;
	private LayoutInflater mInflater;
	//private float TempX;
	//private float DownX;
	private int touchPoi = -1;

	public FileAdapter(FileActivity mContext,List<VideoInfo> mVideoInfos) {
		this.mContext = mContext;
		this.mVideoInfos = mVideoInfos;
		mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		return mVideoInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mVideoInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.file_listview_item, null);
			holder.btn = (Button) convertView.findViewById(R.id.btn_del);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.cBox = (CheckBox) convertView.findViewById(R.id.childCheckBox);
			convertView.setTag(holder);
		} else {
			if(convertView.getId() != touchPoi){
				convertView.setScrollX(0);
			}
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setId(position);
		
		convertView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())//根据动作来执行代码     
                {    
                case MotionEvent.ACTION_MOVE://滑动     
                    break;    
                case MotionEvent.ACTION_DOWN://按下 
                	touchPoi = v.getId();
                    break;    
                case MotionEvent.ACTION_UP://松开     
                	notifyDataSetChanged();
                    break;    
                default:    
                }    
				return false;
			}
		});
		
		holder.tv_name.setText(mVideoInfos.get(position).getFilename());
		if(mVideoInfos.get(position).getIsVisible() == 1){
			holder.cBox.setVisibility(View.VISIBLE);
		}else{
			holder.cBox.setVisibility(View.GONE);
		}
		holder.tv_time.setText(TimeUtils.formatTimeToStr(mVideoInfos.get(position).getFinish_time()));
		holder.btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mContext.delATask(((View)v.getParent().getParent()).getId());
				touchPoi = -1;
			}
		});
		
		if(mVideoInfos.get(position).getChecked() == 1){
			holder.cBox.setChecked(true);
		}else{
			holder.cBox.setChecked(false);
		}
		
		holder.cBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mVideoInfos.get(((View)v.getParent().getParent()).getId()).getChecked() == 1){
					mVideoInfos.get(((View)v.getParent().getParent()).getId()).setChecked(0);
				}else{
					mVideoInfos.get(((View)v.getParent().getParent()).getId()).setChecked(1);;
				}
				notifyDataSetChanged();
			}
		});
		
		
		//share_status，1：正常，文件、记录都存在；4：删掉了文件，记录存在
		if(mVideoInfos.get(position).getShare_status() == 1){
			holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.tab_text_checked_color));
			holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.tab_text_checked_color));
		}else if(mVideoInfos.get(position).getShare_status() == 4){
			holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.tab_text_checked_color));
			holder.tv_name.setTextColor(mContext.getResources().getColor(R.color.unable_color));
		}
		
		return convertView;
	}

	static class ViewHolder {
		private TextView tv_name;
		private TextView tv_time;
		private CheckBox  cBox;
		private Button btn;
	}
	
}
