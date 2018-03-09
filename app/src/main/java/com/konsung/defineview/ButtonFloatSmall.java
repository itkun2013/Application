package com.konsung.defineview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.konsung.R;
import com.konsung.util.UiUitls;


public class ButtonFloatSmall extends ButtonFloat {

	public ButtonFloatSmall(Context context, AttributeSet attrs) {
		super(context, attrs);
		sizeRadius = 20;
		sizeIcon = 20;
		setDefaultProperties();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				UiUitls.dpToPx(sizeIcon, getResources()),
				UiUitls.dpToPx(sizeIcon, getResources()));
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		icon.setLayoutParams(params);
	}

	protected void setDefaultProperties(){
		rippleSpeed = UiUitls.dpToPx(2, getResources());
		rippleSize = 10;
		// Min size
		setMinimumHeight(UiUitls.dpToPx(sizeRadius*2, getResources()));
		setMinimumWidth(UiUitls.dpToPx(sizeRadius*2, getResources()));
		// Background shape
		setBackgroundResource(R.drawable.background_button_float);
//		setBackgroundColor(backgroundColor);
	}

}
