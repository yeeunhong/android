package kr.co.uniquantum.ui.views;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenView extends UIScreenView implements View.OnClickListener, Runnable {

	public MainScreenView(Context context, int orientation ) {
		super(context, orientation );
		// TODO Auto-generated constructor stub
	}

	boolean isRunning = false;
	Thread thread_handle;
	
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams( 
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT );
		
		screen.setTitle("MainScreenView");
		
		TextView textView = new TextView(screen);
		textView.setId( 100 );
		textView.setText( "텍스트 뷰" );
		textView.setBackgroundColor( 0xFF0000FF );
		layout.addView( textView, vl );
		
		Button button = new Button(screen);
		button.setId( 100 );
		button.setText( "MainMenuView로 이동" );
		//button.setBackgroundColor(0x88CCCCCC);
		layout.addView( button, vl );
		button.setOnClickListener( this );
		
		Button button1 = new Button(screen);
		button1.setId( 200 );
		button1.setText( "Thread Start" );
		//button.setBackgroundColor(0x88CCCCCC);
		layout.addView( button1, vl );
		button1.setOnClickListener( this );
		
		Button button2 = new Button(screen);
		button2.setId( 300 );
		button2.setText( "Thread End" );
		//button.setBackgroundColor(0x88CCCCCC);
		layout.addView( button2, vl );
		button2.setOnClickListener( this );
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		if( isRunning )
		{
			isRunning = false;
			try 
			{
				thread_handle.wait();
				thread_handle.notify();
			}
			catch( Throwable t ){}
		}
				
		super.onStop();		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case 100 :
			changeScreen( new MainMenuView( this_screen ) );
			break;
			
		case 200 :
			thread_handle = new Thread( this );
			isRunning = true;
			thread_handle.start();
			break;
			
		case 300 :
			try
			{
				thread_handle.wait();
				isRunning = false;
				thread_handle.notify();
			}
			catch( Throwable t )
			{
				isRunning = false;
			}
			break;
		}
		
	}
	
	int nCount = 0;
	Handler handler = new Handler()
	{
		public void handleMessage( Message msg )
		{
			showToast( String.format( "Thread에서 띄운 Toast 입니다. %d", nCount++ ), true );
		}
	};
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			while( isRunning )
			{
				handler.sendMessage( handler.obtainMessage() );
				Thread.sleep(2000);				
			}
		}
		catch( Throwable t )
		{
		}
	}
}
