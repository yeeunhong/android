package com.uniquantum.www;

import kr.co.uniquantum.ui.UIControls;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ChoImageView extends ImageView {

	private EventListener m_listener;
	public UIControls controls = null;
	
	public interface EventListener 
	{
		void onSizeChanged(int w, int h, int oldw, int oldh);
	}
	
	public void setListener (EventListener l) {
		m_listener = l;
	} 
	
	public ChoImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		controls = new UIControls( this );
	}
   
	public ChoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		controls = new UIControls( this );
	}

	public ChoImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		controls = new UIControls( this );
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		if (m_listener != null) {
			m_listener.onSizeChanged(w, h, oldw, oldh);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		controls.draw( canvas );
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return controls.onTouchEvent(event);
		//return false;//super.onTouchEvent(event);
	}
	
	
}
