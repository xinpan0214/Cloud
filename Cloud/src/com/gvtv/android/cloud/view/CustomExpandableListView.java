/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: CustomExpandableListView.java
 * @Prject: MyCloud
 * @Package: com.gvtv.android.cloud.view
 * @Description: TODO
 * @author: yh  
 * @date: 2014-6-10 下午3:02:55
 * @version: V1.0
 */
package com.gvtv.android.cloud.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: CustomExpandableListView.java
 * @Prject: MyCloud
 * @Package: com.gvtv.android.cloud.view
 * @Description: TODO
 * @author: yh
 * @date: 2014-6-10 下午3:02:55
 * @version: V1.0
 */
public class CustomExpandableListView extends ExpandableListView {  
	  
    public CustomExpandableListView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
    }  
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        // TODO Auto-generated method stub  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
  
        MeasureSpec.AT_MOST);  
  
        super.onMeasure(widthMeasureSpec, expandSpec);  
    }  
}  

