package code.inka.android.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	public static boolean checkCameraHardware( Context context ) {
		if( context.getPackageManager().hasSystemFeature( PackageManager.FEATURE_CAMERA )) {
			return true;
		} 
		return false; 
	}
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private Camera camera = null;
	private SurfaceHolder sv_holder = null;
	private String pictureName = null;
	private int	camera_id = 0;
		
	public CameraView(Context context) {
		super(context);
		init();			
	}

	@SuppressLint("NewApi")
	private void init() {
		try {
			camera 		= Camera.open();
			/*
			if( Build.VERSION.SDK_INT >	Build.VERSION_CODES.FROYO ) {
				camera 		= Camera.open( camera_id );
			} else {
				camera 		= Camera.open();
			}
			*/
			sv_holder	= getHolder();
			sv_holder.addCallback( this );
			sv_holder.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
		} catch( Exception e) {
			camera = null;
		}	
	}
	public void release() {
		try{
			camera.stopPreview();
			camera.release();
		} catch( Exception e ) {} 
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try{
			camera.setPreviewDisplay( holder );
			camera.startPreview();
		} catch( Exception e ) {
			Log.d( "CamearView", "Error starting camera preview : " + e.getMessage());
		}		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		if( sv_holder.getSurface() == null ) {
			return;
		}
		
		try{
			camera.stopPreview();
		} catch( Exception e ) {} 
		
		surfaceCreated( sv_holder );		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	
	@SuppressLint("NewApi")
	public int getNumberOfCameras() {
		if( Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ) {
			return 1;//Camera.getNumberOfCameras();
		} 
		
		return 1;		
	}
	public void usingOtherCamera() {
		int num = getNumberOfCameras(); 
		if( num <= 1 ) return;
		
		release(); 
		
		camera_id = camera_id + 1 >= num ? 0 : camera_id + 1;
		
		init();
		surfaceCreated( sv_holder );
	}
	
	public void takePicture( String path ) {
		pictureName = path;
		camera.takePicture( null, null, picture_cb );
	}
	private PictureCallback picture_cb = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {			
			File pictureFile = pictureName == null ? getOutputMediaFile( MEDIA_TYPE_IMAGE ) : new File( pictureName );
			
			try{
				FileOutputStream fos = new FileOutputStream( pictureFile );
				fos.write( data );
				fos.close();
			} catch( FileNotFoundException e ) {
				Log.d( "CameraView", "File not found : " + e.getMessage());
			} catch( IOException e ) {
				Log.d( "CameraView", "Error accessing file : " + e.getMessage());
			}
		}
	};
	
	private File getOutputMediaFile( int type ) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_PICTURES), CameraView.this.getContext().getPackageName() + "\\Camera");
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("CameraView", "failed to create directory");
				return null;
			}
		}
		
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		
		File mediaFile;
		if( type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");    
		} else if(type == MEDIA_TYPE_VIDEO) {       
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");    
		} else {        
			return null;    
		}
		        
        return mediaFile;
	}
}
