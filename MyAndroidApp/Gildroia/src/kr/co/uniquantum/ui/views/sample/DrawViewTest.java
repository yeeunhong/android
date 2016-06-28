package kr.co.uniquantum.ui.views.sample;

import java.util.ArrayList;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;

public class DrawViewTest extends UIScreenView {

	public DrawViewTest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		//screen.requestWindowFeature(Window.FEATURE_NO_TITLE);
		layout.addView(new PanelSV(screen));
	}

/*
	private class Panel extends android.view.View
	{
		public Panel(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) 
		{
			Bitmap _scratch = BitmapFactory.decodeResource( getResources(), R.drawable.sym_def_app_icon	);
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap( _scratch, 10, 10, null );
		}
	}
*/
	private class PanelSV extends android.view.SurfaceView implements android.view.SurfaceHolder.Callback
	{
		private PanelSVdrawThread drawThread;
		private ArrayList<GraphicObject> graphObjs = new ArrayList<GraphicObject>(); 
		private GraphicObject currentObj = null;
		
		public PanelSV(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			getHolder().addCallback(this);
			drawThread = new PanelSVdrawThread( getHolder(), this );
			setFocusable( true );
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			synchronized( drawThread.getSurfaceHolder())
			{
				switch( event.getAction())
				{
				case MotionEvent.ACTION_DOWN :
					Bitmap bmp = BitmapFactory.decodeResource( getResources(), R.drawable.sym_def_app_icon	);
					
					GraphicObject obj = new GraphicObject( bmp );
					obj.getCoordinates().setX((int) event.getX() - bmp.getWidth() / 2 );
					obj.getCoordinates().setY((int) event.getY() - bmp.getHeight() / 2 );
					currentObj = obj;		
					break;
					
				case MotionEvent.ACTION_MOVE :
					currentObj.getCoordinates().setX((int) event.getX() - currentObj.getBitmap().getWidth() / 2 );
					currentObj.getCoordinates().setY((int) event.getY() - currentObj.getBitmap().getHeight() / 2 );
					break;
					
				case MotionEvent.ACTION_UP :
					graphObjs.add( currentObj );
					currentObj = null;
					break;
				}
				
				return true;
			}
		}

		@Override
		protected void onDraw(Canvas canvas) 
		{
			canvas.drawColor(Color.BLACK);
					
			for( GraphicObject g_obj : graphObjs )
			{
				canvas.drawBitmap( 
						g_obj.getBitmap(), 
						g_obj.getCoordinates().getX(),
						g_obj.getCoordinates().getY(), null );
			}
			
			if( currentObj != null )
			{
				canvas.drawBitmap( 
						currentObj.getBitmap(), 
						currentObj.getCoordinates().getX(),
						currentObj.getCoordinates().getY(), null );
			}
		}

		public void updatePhysics()
		{
			GraphicObject.Coordinates coord;
			GraphicObject.Speed speed;
			
			for( GraphicObject obj : graphObjs )
			{
				coord = obj.getCoordinates();
				speed = obj.getSpeed();
				
				//Direction
				if( speed.getXDirection() == GraphicObject.Speed.X_DIRECTION_RIGHT )
				{
					coord.setX( coord.getX() + speed.getSpeedX());
				}
				else
				{
					coord.setX( coord.getX() - speed.getSpeedX());
				}
				
				if( speed.getYDirection() == GraphicObject.Speed.Y_DIRECTION_DOWN )
				{
					coord.setY( coord.getY() + speed.getSpeedY());
				}
				else
				{
					coord.setY( coord.getY() - speed.getSpeedY());
				}
				
				// borders for x...
				if( coord.getX() < 0 )
				{
					speed.toggleXDirection();
					coord.setX( -coord.getX());
				}
				else if( coord.getX() + obj.getBitmap().getWidth() > getWidth())
				{
					speed.toggleXDirection();
					coord.setX( coord.getX() + getWidth() - (coord.getX() + obj.getBitmap().getWidth()) );
				}
				
				// borders for y...
				if( coord.getY() < 0 )
				{
					speed.toggleYDirection();
					coord.setY( -coord.getY());
				}
				else if( coord.getY() + obj.getBitmap().getHeight() > getHeight())
				{
					speed.toggleYDirection();
					coord.setY( coord.getY() + getHeight() - (coord.getY() + obj.getBitmap().getHeight()) );
				}
			}
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			drawThread.setRunning( true );
			drawThread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			boolean retry = true;
			drawThread.setRunning( false );
			while( retry )
			{
				try
				{
					drawThread.join();
					retry = false;
				} 
				catch( InterruptedException e )
				{
					
				}
			}
		}
		
		class PanelSVdrawThread extends Thread
		{
			private SurfaceHolder surfaceHolder;
			private PanelSV panel;
			private boolean thread_run = false;
			
			public PanelSVdrawThread( SurfaceHolder _surfaceHolder, PanelSV _panel )
			{
				surfaceHolder = _surfaceHolder;
				panel = _panel;
			}
			
			public SurfaceHolder getSurfaceHolder(){ return surfaceHolder; }
			public void setRunning( boolean _run )
			{
				thread_run = _run;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Canvas canvas;
				
				while( thread_run )
				{
					canvas = null;
					
					try
					{
						canvas = surfaceHolder.lockCanvas(null);
						synchronized( surfaceHolder )
						{
							panel.updatePhysics();
							panel.onDraw(canvas);
						}
					}
					finally
					{
						if( canvas != null )
						{
							surfaceHolder.unlockCanvasAndPost(canvas);
						}
					}
				}
			}
		}
		
		public class GraphicObject
		{
			private Bitmap bitmap;
			private Coordinates coordinates;
			private Speed speed;
			
			private int xpad = 0;
			private int ypad = 0;
			
			public GraphicObject( Bitmap bmp )
			{
				bitmap = bmp;
				xpad = bitmap.getWidth() / 2;
				ypad = bitmap.getHeight() / 2;
				
				coordinates = new Coordinates();
				speed		= new Speed();
			}
			
			public Bitmap getBitmap(){ return bitmap; }
			public Coordinates getCoordinates(){ return coordinates; }
			public Speed getSpeed(){ return speed; }
			
			public class Coordinates
			{
				private int x = 100;
				private int y = 0;
				
				public int getX(){ return x + xpad;	}
				public int getY(){ return y + ypad;	}
				
				public void setX( int _x ){	x = _x - xpad;}
				public void setY( int _y ){	y = _y - ypad; }
			
				public String toString(){ return "Coordinates:( " + x + "," + y + " )"; }
			}
			
			public class Speed
			{
				public static final int X_DIRECTION_RIGHT 	= 1;
				public static final int X_DIRECTION_LEFT 	= -1;
				public static final int Y_DIRECTION_DOWN 	= 1;
				public static final int Y_DIRECTION_UP 		= -1;
				
				private int x_speed = 1;
				private int y_speed = 1;
				
				private int xDirection = X_DIRECTION_RIGHT;
				private int yDirection = Y_DIRECTION_DOWN;
				
				public int getSpeedX(){ return x_speed; }
				public int getSpeedY(){ return y_speed; }
				//public void setSpeedX( int speed ){ x_speed = speed; }
				//public void setSpeedY( int speed ){ y_speed = speed; }
				
				public int getXDirection(){ return xDirection; }
				public int getYDirection(){ return yDirection; }
				//public void setXDirection( int direction ){ xDirection = direction; }
				//public void setYDirection( int direction ){ yDirection = direction; }
				public void toggleXDirection()
				{ 
					xDirection = xDirection == X_DIRECTION_RIGHT ? X_DIRECTION_LEFT : X_DIRECTION_RIGHT; 
				}
				public void toggleYDirection()
				{ 
					yDirection = yDirection == Y_DIRECTION_DOWN ? Y_DIRECTION_UP : Y_DIRECTION_DOWN; 
				}
			}
		}
	}
}
