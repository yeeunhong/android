package code.inka.android.ui.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import code.inka.android.libs.R;

public class SimpleImageViewActivity extends Activity {
	private ImageViewEx imageView = null;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.layout_simple_image_view );
		
		imageView = ( ImageViewEx ) findViewById( R.id.ImageView );
		
		Intent intent = getIntent();
		if( intent != null ) {
			setImageView( intent.getStringExtra( "filename" ), imageView );
		}
	}

	@Override protected void onDestroy() {
		super.onDestroy();
	}
	
	private void setImageView( String filename, ImageView imageView ) {
		if( filename == null || imageView == null ) return;
			
		try {
			RandomAccessFile file = new RandomAccessFile( filename, "r" );
				
			Bitmap orgImage = loadBitmap( file );
			if( orgImage != null ) {
				imageView.setImageBitmap(orgImage);
				//imageView.setScaleType(ScaleType.CENTER_CROP);
			}
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}					
	}
	
	Bitmap loadBitmap( RandomAccessFile file ) {
		try {
			byte imgBytes[] = new byte[ 102400 ];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			int nRead = file.read( imgBytes );
			while( nRead > 0 ) {
				baos.write( imgBytes );
				nRead = file.read( imgBytes );
			}
			file.close();
				
			byte [] imgBuff = baos.toByteArray();
			Bitmap orgImage = BitmapFactory.decodeByteArray( imgBuff, 0, imgBuff.length ); 
			baos.close();
			
			return orgImage;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}
}
