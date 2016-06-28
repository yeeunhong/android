package kr.co.uniquantum.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.uniquantum.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class UIControls 
{
	protected View view = null;
	protected HashMap<Integer,UIButton> buttons = new HashMap<Integer,UIButton>();
	protected LinkedList<UIButton> 		buttons_draw = new LinkedList<UIButton>();
	protected LinkedList<UIButton> 		buttons_event = new LinkedList<UIButton>();
	
	protected Point touch_point 	 = new Point();
	protected Point touch_down_point = new Point();
	protected UIButton current_button = null;
	protected View clickView = null;

	protected Rect backRect = new Rect();
	
	public UIControls( View view )
	{
		this.view = view;
	}
	
	public UIButton GetButton( int id ){ return buttons.get( Integer.valueOf(id)); }
	public UIButton AddButton( int x, int y, int width, int height, String text, int id,
			View.OnClickListener clickListener )
	{
		regist_button( new UIButton( x, y, width, height, text, id ).SetOnClickListener( clickListener ), id );
		return GetButton( id );
	}
	
	public UIPanel GetPanel( int id ){ return ( UIPanel ) buttons.get( Integer.valueOf(id)); }
	public UIPanel AddPanel( int x, int y, int width, int height, String text, int id )
	{
		regist_button( new UIPanel( x, y, width, height, text, id ), id );	
		return GetPanel( id );
	}
	
	public UIImgButton GetImgButton( int id ){ return ( UIImgButton ) buttons.get( Integer.valueOf(id)); }
	public UIImgButton AddImgButton( int x, int y, int res_id, int img_cnt, String text, int id,
			View.OnClickListener clickListener )
	{
		regist_button( new UIImgButton( x, y, res_id, img_cnt, text, id ).SetOnClickListener( clickListener ), id );
		return GetImgButton( id );
	}
	public UIImgButton AddImgButton( int x, int y, int res_id, int img_tot, int img_begin_idx, int img_cnt, String text, int id,
			View.OnClickListener clickListener )
	{
		regist_button( new UIImgButton( x, y, res_id, img_tot, img_begin_idx, img_cnt, text, id ).SetOnClickListener( clickListener ), id );
		return GetImgButton( id );
	}
		
	public void AddImgCloseButton( int x, int y, int id, View.OnClickListener clickListener )
	{
		regist_button( new UIImgCloseButton( x, y, id ).SetOnClickListener( clickListener ), id );
	}
		
	public UICheckButton GetCheckButton( int id ){ return ( UICheckButton ) buttons.get( Integer.valueOf(id)); }
	public UICheckButton AddCheckButton( int x, int y, int width, int height, int id )
	{
		regist_button( new UICheckButton( x, y, width, height, id ), id );
		return GetCheckButton( id );
	}
	
	public UIImgCheckButton GetImgCheckButton( int id ){ return ( UIImgCheckButton ) buttons.get( Integer.valueOf(id)); }
	public UIImgCheckButton AddImgCheckButton( int x, int y, int res_nor_id, int res_chk_id, int id )
	{
		regist_button( new UIImgCheckButton( x, y, res_nor_id, res_chk_id, id ), id );
		return GetImgCheckButton( id );
	}
	
	public UIAniImage GetAniImage( int id ){ return ( UIAniImage ) buttons.get( Integer.valueOf(id)); }
	public UIAniImage AddAniImage( int x, int y, int id )
	{
		regist_button( new UIAniImage( x, y, id ), id );
		return GetAniImage( id ); 
	}
	
	public void draw( Canvas canvas ) 
	{
		// TODO Auto-generated method stub
		//canvas.drawColor(Color.WHITE);
		Iterator<UIButton> it = buttons_draw.iterator();
		while( it.hasNext())
		{
			UIButton button = it.next();
			button.OnDraw(canvas);
		}	
	}
	
	public boolean onTouchEvent( MotionEvent e ) 
	{
		// TODO Auto-generated method stub
		touch_point.x = (int) e.getX();
		touch_point.y = (int) e.getY();
		
		switch( e.getAction() )
		{
		case MotionEvent.ACTION_DOWN :
			touch_down_point.x = touch_point.x;
			touch_down_point.y = touch_point.y;
			
			current_button = null;
				
			Iterator<UIButton> it = buttons_event.iterator();
			while( it.hasNext())
			{
				UIButton button = ( UIButton )it.next();
					
				if( button.checkInPoint( touch_point )) 
				{
					current_button = button;
					current_button.setStatusDonw();
					invalidateButton( current_button, false );
					return true;
					//break;
				}
			}
			break;
			
		case MotionEvent.ACTION_UP :
			if( current_button != null )
			{
				if( current_button.checkInPoint( touch_point ) )
					current_button.sendClickedAction();
				current_button.setStatusNormal();
				invalidateButton( current_button, false );
				return true;
			}
			break;
			
		case MotionEvent.ACTION_MOVE :
			if( current_button != null )
			{
				if( current_button.getMoveMode() )
				{
					Rect rect = current_button.getRect();
					rect.left 	-= 10;
					rect.top 	-= 10;
					rect.right	+= 10;
					rect.bottom	+= 10;
					
					invalidateButton( current_button, true );
					current_button.Move( 
							touch_point.x - touch_down_point.x, 
							touch_point.y - touch_down_point.y );
					//invalidate( rect );
					invalidateButton( current_button, false );
									
					touch_down_point.x = touch_point.x;
					touch_down_point.y = touch_point.y;
					
				}
				else
				{
					if( !current_button.checkInPoint( touch_point ) )
					{
						current_button.setStatusNormal();
						invalidateButton( current_button, false );
						current_button = null;
					}
				}
				
				return true;
			}
			break;
		}
		
		return false;
	}
	
	public boolean onLongClick(View v) 
	{
		if( current_button != null ) 
		{
			if( current_button.checkInPoint( touch_point ) )
			{
				current_button.setStatusMoveMode();
				invalidateButton( current_button, false );
				return true;
			}
		}
		return false;
	}
	
	private void regist_button( UIButton button, int id )
	{
		buttons.put( id, button );
		buttons_draw.add( button );
		buttons_event.add( 0, button );
	}
	
	protected void invalidateButton( UIButton button, boolean back_invalidate )
	{
		Rect rect = button.getRect();
		rect.left 	-= 10;
		rect.top 	-= 10;
		rect.right	+= 10;
		rect.bottom	+= 10;
		
		if( back_invalidate ) backRect = rect;
		else
		{
			view.invalidate( rect );
			if( !backRect.isEmpty()) view.invalidate( backRect );
			backRect.setEmpty();
		}
	}
	
	protected Handler invalidate_event = new Handler(){
		public void handleMessage( Message msg )
		{
			invalidateButton( GetButton( msg.what ), false );	
		}
	};
	
	
	public class UIRect
	{
		protected Point start;
		protected Point end;
		protected int width;
		protected int height;
		
		public UIRect(){ start = new Point(); end = new Point(); width = 0; height = 0; }
		public UIRect( int x, int y, int width, int height )
		{
			start = new Point( x, y );
			end   = new Point( x + width, y + height );
			this.width = width;
			this.height = height;
		}
		public UIRect( UIRect rect )
		{  
			start 	= new Point( rect.getStart() );
			end   	= new Point( rect.getEnd() );
			width 	= rect.getWidth();
			height 	= rect.getHeight();
		}
		public Point getStart()	{ return start; }
		public Point getEnd()	{ return end; }
		public int   getWidth()	{ return width; }
		public int   getHeight(){ return height; }
		
		public boolean checkInPoint( Point pt )
		{
			if( pt.y < start.y || pt.y > end.y ) return false;
			if( pt.x < start.x || pt.x > end.x ) return false;
			return true;			
		}
		public Rect getRect()
		{
			Rect rect = new Rect();
			rect.set( start.x, start.y, end.x, end.y );
			return rect;
		}
		
		public void Move( int x, int y )
		{
			start.x += x; end.x += x;
			start.y += y; end.y += y;
		}
	}
		
	public class UIButton extends UIRect
	{
		final int BUTTON_NORMAL 	= 0;
		final int BUTTON_DOWN 		= 1;
		final int BUTTON_DISENABLE	= 2;
				
		protected int id;
		protected String text = null;
		protected int status = BUTTON_NORMAL;
		protected View.OnClickListener clickListener = null;
		protected boolean moveMode = false;
		protected int type = 0;
		protected boolean checked = false;
		protected boolean visible = true;
		
		protected Paint textPaint = null;
		
		public UIButton( int x, int y, int width, int height, int id )
		{
			start = new Point( x, y );
			end   = new Point( x + width, y + height );
			this.id 	= id;
			this.width 	= width;
			this.height = height;
		}
		public UIButton( int x, int y, int width, int height, String text, int id )
		{
			start = new Point( x, y );
			end   = new Point( x + width, y + height );
			this.id 	= id;
			this.text 	= text;
			this.width 	= width;
			this.height = height;
		}
		
		public UIButton SetOnClickListener( OnClickListener clickListener )
		{
			this.clickListener = clickListener;
			return this;
		}
		
		public boolean checkInPoint( Point pt )
		{
			if( !visible ) return false;
			return super.checkInPoint(pt);						
		}
		
		public boolean getMoveMode(){ return moveMode; }
		
		public void setText( String text ){ this.text = text; }
		public void setTextPaint( Paint paint ){ textPaint = paint; }
		
		public void setVisible( boolean _visible ){ visible = _visible; }
		public void setType( int type ){ this.type = type; }
		public void setStatusDonw()    { status = BUTTON_DOWN; moveMode = false; }
		public void setStatusNormal()  { status = BUTTON_NORMAL; moveMode = false; }
		public void setStatusDisable() { status = BUTTON_DISENABLE; moveMode = false; }
		public void setStatusMoveMode(){ moveMode = true; }
		public void sendClickedAction()
		{ 
			if( status == BUTTON_DISENABLE || moveMode ) return;
			if( clickListener != null ) 
			{
				view.setTag( text );
				view.setId(id);
				clickListener.onClick(view);
			}
		}
		
		public void OnDraw( Canvas canvas )
		{
			if( !visible ) return;
			
			Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
			Rect rect = getRect();
			
			paint.setColor( Color.GRAY );
			canvas.drawRect( rect, paint );
			
			if( type == 0 )
			{
				for( int i = 0; i < 2; ++i )
				{
					if( status == BUTTON_NORMAL )
						paint.setColor( Color.LTGRAY );
					else
						paint.setColor( Color.DKGRAY );
					canvas.drawLine( rect.left, rect.top, rect.right, rect.top, paint );
					canvas.drawLine( rect.left, rect.top, rect.left,  rect.bottom, paint );
					
					if( status == BUTTON_NORMAL )
						paint.setColor( Color.DKGRAY );
					else
						paint.setColor( Color.LTGRAY );
					canvas.drawLine( rect.left,  rect.bottom, rect.right, rect.bottom, paint );
					canvas.drawLine( rect.right, rect.top,    rect.right, rect.bottom, paint );
					
					++rect.left;   ++rect.top;
					--rect.right;  --rect.bottom;
				}
			}
			else
			{
				if( status == BUTTON_NORMAL )
					paint.setColor( Color.DKGRAY );
				else
					paint.setColor( Color.LTGRAY );
				canvas.drawLine( rect.left, rect.top, rect.right, rect.top, paint );
				canvas.drawLine( rect.left, rect.top, rect.left,  rect.bottom, paint );
				canvas.drawLine( rect.left,  rect.bottom, rect.right, rect.bottom, paint );
				canvas.drawLine( rect.right, rect.top,    rect.right, rect.bottom, paint );
				
				++rect.left;   ++rect.top;
				--rect.right;  --rect.bottom;
				if( status == BUTTON_NORMAL )
					paint.setColor( Color.LTGRAY );
				else
					paint.setColor( Color.DKGRAY );
				canvas.drawLine( rect.left, rect.top, rect.right, rect.top, paint );
				canvas.drawLine( rect.left, rect.top, rect.left,  rect.bottom, paint );
				canvas.drawLine( rect.left,  rect.bottom, rect.right, rect.bottom, paint );
				canvas.drawLine( rect.right, rect.top,    rect.right, rect.bottom, paint );
				
				++rect.left;   ++rect.top;
				--rect.right;  --rect.bottom;
				if( status == BUTTON_NORMAL )
					paint.setColor( Color.DKGRAY );
				else
					paint.setColor( Color.LTGRAY );
				canvas.drawLine( rect.left, rect.top, rect.right, rect.top, paint );
				canvas.drawLine( rect.left, rect.top, rect.left,  rect.bottom, paint );
				canvas.drawLine( rect.left,  rect.bottom, rect.right, rect.bottom, paint );
				canvas.drawLine( rect.right, rect.top,    rect.right, rect.bottom, paint );
			}
						
			
			DrawText( canvas, paint );
			DrawMoveMarker( canvas, paint, rect );
		}
		
		public void OnDestory() {}
		
		public void Move( int x, int y )
		{
			if( moveMode ) super.Move( x, y );			
		}
		
		protected void DrawText( Canvas canvas, Paint paint )
		{
			if( text != null )
			{
				if( textPaint != null ) paint = textPaint;
				else paint.setColor( Color.BLACK ); 
					
				float TextWidth  = paint.measureText(text);
				float TextHeight = paint.descent() - paint.ascent();
								
				canvas.drawText( text, start.x+(getWidth()-TextWidth)/2, start.y+(getHeight()-TextHeight)/2-paint.ascent(), paint );
			}
		}
		public void DrawMoveMarker( Canvas canvas, Paint paint, Rect rect )
		{
			if( moveMode )
			{
				paint.setStyle( Paint.Style.STROKE );
				paint.setColor( Color.RED );
				
				rect.left  -= 5; 	rect.top 	-= 5;
				rect.right += 5;    rect.bottom += 5;
				
				canvas.drawLine( rect.left, rect.top, rect.right, rect.top, paint );
				canvas.drawLine( rect.left, rect.top, rect.left,  rect.bottom, paint );
				canvas.drawLine( rect.left,  rect.bottom, rect.right, rect.bottom, paint );
				canvas.drawLine( rect.right, rect.top,    rect.right, rect.bottom, paint );
			}
		}
		
		public boolean getChecked(){ return checked; }
	}
	
	public class UIPanel extends UIButton
	{
		public UIPanel( int x, int y, int width, int height, String text, int id )
		{
			super( x, y, width, height, text, id );			
		}
		
		public void OnDraw( Canvas canvas )
		{
			if( !visible ) return;
			Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
			Rect rect = getRect();
			
			DrawText( canvas, paint );
			DrawMoveMarker( canvas, paint, rect );
		}
	}
	
	public class UIImgButton extends UIButton
	{
		protected Bitmap bmp[] = new Bitmap[3];
		public UIImgButton( int x, int y, int id ){	super( x, y, 0, 0, id ); }
		public UIImgButton( int x, int y, int res_id, int img_cnt, String text, int id )		
		{
			super( x, y, 0, 0, text, id );
			// TODO Auto-generated constructor stub
			
			Bitmap _bmp = BitmapFactory.decodeResource( view.getResources(), res_id	);
			
			width 	= _bmp.getWidth() / img_cnt;
			height 	= _bmp.getHeight();
			
			end.x = x + width;
			end.y = y + height;
						
			this.bmp[BUTTON_NORMAL] = Bitmap.createBitmap( _bmp, 0, 0, getWidth(), getHeight() );
			if( img_cnt > 1 ) 
				this.bmp[BUTTON_DOWN] = Bitmap.createBitmap( _bmp, getWidth(), 0, getWidth(), getHeight() );
			if( img_cnt > 2 )
				this.bmp[BUTTON_DISENABLE] = Bitmap.createBitmap( _bmp, getWidth()*2, 0, getWidth(), getHeight() );
			_bmp = null;
		}
		public UIImgButton( int x, int y, int res_nor_id, int res_sel_id, int res_dis_id, int id )
		{
			super( x, y, 0, 0, id );
			
			if( res_nor_id != 0 ) bmp[BUTTON_NORMAL]    = BitmapFactory.decodeResource( view.getResources(), res_nor_id );
			if( res_sel_id != 0 ) bmp[BUTTON_DOWN] 	  	= BitmapFactory.decodeResource( view.getResources(), res_sel_id );
			if( res_dis_id != 0 ) bmp[BUTTON_DISENABLE] = BitmapFactory.decodeResource( view.getResources(), res_dis_id );
			
			width 	= bmp[0].getWidth();
			height 	= bmp[0].getHeight();
			
			end.x = x + width;
			end.y = y + height;
		}
		
		public UIImgButton( int x, int y, int res_id, int img_total, int img_start_idx, int img_cnt, String text, int id )
		{
			super( x, y, 0, 0, text, id );
			// TODO Auto-generated constructor stub
			
			Bitmap _bmp = BitmapFactory.decodeResource( view.getResources(), res_id	);
			
			width 	= _bmp.getWidth() / img_total;
			height 	= _bmp.getHeight();
			
			end.x = x + width;
			end.y = y + height;
						
			this.bmp[BUTTON_NORMAL] = Bitmap.createBitmap( _bmp, img_start_idx*getWidth(), 0, getWidth(), getHeight() );
			if( img_cnt > 1 ) 
				this.bmp[BUTTON_DOWN] = Bitmap.createBitmap( _bmp, (img_start_idx+1)*getWidth(), 0, getWidth(), getHeight() );
			if( img_cnt > 2 )
				this.bmp[BUTTON_DISENABLE] = Bitmap.createBitmap( _bmp, (img_start_idx+2)*getWidth(), 0, getWidth(), getHeight() );
			_bmp = null;
		}
		
		public void OnDestory() 
		{
			bmp[BUTTON_NORMAL]    = null;
			bmp[BUTTON_DOWN] 	  = null;
			bmp[BUTTON_DISENABLE] = null;
			bmp = null;
		}
		
		public void OnDraw( Canvas canvas )
		{
			if( !visible ) return;
			if( bmp == null ) return;
			
			Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
			Rect rect = getRect();
			
			canvas.drawBitmap( bmp[status], rect.left, rect.top, paint );
			
			DrawText( canvas, paint );
			DrawMoveMarker( canvas, paint, rect );			
		}
	}
	
	public class UIImgCloseButton extends UIImgButton
	{
		public UIImgCloseButton( int x, int y, int id ) 
		{
			super( x, y, R.drawable.btn_close_normal, R.drawable.btn_close_pressed, 0, id );
		}
	}
	
	public class UICheckButton extends UIButton
	{
		public UICheckButton( int x, int y, int width, int height, int id )
		{
			super( x, y, width, height, id );
		}
		
		public void OnDraw( Canvas canvas )
		{
			if( !visible ) return;
			Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
			Rect rect = getRect();
			
			paint.setColor( Color.GRAY );
			canvas.drawRect( rect, paint );
			
			int colors[] = new int[3];
			colors[0] = Color.DKGRAY;
			colors[1] = Color.LTGRAY;
			colors[2] = Color.DKGRAY;
			
			for( int i = 0; i < 3; ++i )
			{
				paint.setColor( colors[i] );
				
				canvas.drawLine( rect.left, rect.top, rect.right, rect.top, paint );
				canvas.drawLine( rect.left, rect.top, rect.left,  rect.bottom, paint );
				canvas.drawLine( rect.left,  rect.bottom, rect.right, rect.bottom, paint );
				canvas.drawLine( rect.right, rect.top,    rect.right, rect.bottom, paint );
				
				++rect.left;   ++rect.top;
				--rect.right;  --rect.bottom;
			}
			
			int tenPercent = getWidth() / 10;
			rect.left  += tenPercent; 	rect.top  += tenPercent;
			rect.right -= tenPercent;  rect.bottom -= tenPercent;
			
			if( checked )
			{
				paint.setColor( Color.RED );
				paint.setStrokeWidth( tenPercent );
				
				canvas.drawLine( rect.left, rect.top, rect.left + tenPercent * 5, rect.bottom, paint );
				canvas.drawLine( rect.left + tenPercent * 5, rect.bottom, rect.right, rect.top, paint );
			}
			
			DrawMoveMarker( canvas, paint, rect );			
		}
		
		public void sendClickedAction()
		{ 
			if( status == BUTTON_DISENABLE || moveMode ) return;
			checked = !checked;
		}
	}
	
	public class UIImgCheckButton extends UIImgButton
	{
		public UIImgCheckButton(int x, int y, int res_nor_id, int res_chk_id, int id) {
			super(x, y, res_nor_id, res_chk_id, 0, id );
			// TODO Auto-generated constructor stub
		}
		
		public void OnDraw( Canvas canvas )
		{
			if( !visible ) return;
			if( bmp == null ) return;
			
			Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
			Rect rect = getRect();
			
			int nIndex = checked ? 1 : 0;
			
			canvas.drawBitmap( bmp[nIndex], rect.left, rect.top, paint );
			
			DrawMoveMarker( canvas, paint, rect );
		}
		
		public void sendClickedAction()
		{ 
			if( status == BUTTON_DISENABLE || moveMode ) return;
			checked = !checked;
		}
	}
	
	public class UIAniImage extends UIButton
	{
		protected ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
		protected Iterator<Bitmap> it = null;
		protected Bitmap bmp = null;
		protected Timer timer = null;
				
		public UIAniImage( int x, int y, int id )
		{
			super( x, y, 0, 0, null, id );
		}
		public void AddBitmap( Bitmap bmp )
		{ 
			int _width 	= bmp.getWidth();
			int _height = bmp.getHeight();
						
			if( width  < _width  ){ end.x = start.x + _width;  width  = _width; }
			if( height < _height ){ end.y = start.y + _height; height = _height; }
			
			bmps.add( bmp ); 
		}
		public void AddBitmap( int res_id )
		{
			Bitmap bmp = BitmapFactory.decodeResource( view.getResources(), res_id );
			if( bmp != null ) AddBitmap( bmp );
		}
		
		public void OnDestory() 
		{
			stop();
			bmps.clear();
			
			it = null;
			bmp = null;
			timer = null;
		}
		public void OnDraw( Canvas canvas )
		{
			if( !visible ) return;
			Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
			Rect rect = getRect();
			
			if( bmp != null )
				canvas.drawBitmap( bmp, start.x, start.y, null );
			DrawMoveMarker(canvas, paint, rect );
		}
		
		public void start( int fps )
		{
			if( timer == null )
			{
				timer = new Timer();
				timer.scheduleAtFixedRate( doUpdateTimer, 0, 1000/fps );
			}
		}
		public void stop()
		{
			if( timer != null ) timer.cancel();				
			timer = null;
		}
		protected final TimerTask doUpdateTimer = new TimerTask()
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				if( it == null ) it = bmps.iterator();
				if( it.hasNext() )
				{
					bmp = ( Bitmap ) it.next();
				}
				else 
				{
					it = bmps.iterator();
					if( it.hasNext() )
					{
						bmp = ( Bitmap ) it.next();
					} 
					else bmp = null;
				}
				
				if( bmp != null )
					invalidate_event.sendEmptyMessage( id );
			}
		};
	}
}
