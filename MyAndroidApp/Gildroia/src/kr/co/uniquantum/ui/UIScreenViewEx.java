package kr.co.uniquantum.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

public class UIScreenViewEx extends View implements OnLongClickListener
{
	public UIControls controls = null;
	public UIScreenViewEx(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		controls = new UIControls( this );
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//super.onDraw(canvas);
		controls.draw(canvas);				
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		return controls.onTouchEvent(event); 
	}

	@Override
	public boolean onLongClick(View v) 
	{
		// TODO Auto-generated method stub
		return controls.onLongClick(v);		
	}
}
