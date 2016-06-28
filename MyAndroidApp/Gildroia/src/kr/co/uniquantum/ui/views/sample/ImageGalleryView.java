package kr.co.uniquantum.ui.views.sample;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

class ImageGalleryView extends UIScreenView {

	public ImageGalleryView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams( 
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT );
		
		Gallery gallery = new Gallery( this_screen );
		gallery.setGravity( Gravity.CENTER_VERTICAL );
		gallery.setSpacing(16);
				
		layout.addView( gallery, vl );
		
		Cursor c = screen.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null );
		ImageCursorAdapter adapter = new ImageCursorAdapter( screen, c);
		gallery.setAdapter( adapter );
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	class ImageCursorAdapter extends CursorAdapter { 
   
		public ImageCursorAdapter(Context context, Cursor c) { 

		super(context, c); 
    
		} 
     
		@Override
     
		public void bindView(View view, Context context, Cursor cursor) { 
      
		ImageView img = (ImageView)view; 
     
		 
     
		long id = cursor.getLong(cursor.getColumnIndexOrThrow(Images.Media._ID));   
      
		Uri uri = ContentUris.withAppendedId(Images.Media.EXTERNAL_CONTENT_URI, id); //개별 이미지에 대한 URI 생성 
      
		try { 
          
		Bitmap bm = Images.Media.getBitmap(this_screen.getContentResolver(), uri); //Bitmap 로드 
          
		img.setImageBitmap(bm); 
      
		} catch(Exception e) {}  
     
		} 
     
		@Override
     
		public View newView(Context context, Cursor cursor, ViewGroup parent) { 
      
		ImageView v = new ImageView(context); 
      
		v.setLayoutParams(new Gallery.LayoutParams(80, 80));  
      
		v.setScaleType(ImageView.ScaleType.FIT_CENTER); 
      
		return v; 
     
		} 
    
		}  

}
