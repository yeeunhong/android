package app.inka.android;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import code.inka.android.camera.CameraView;
import code.inka.android.ui.popup.OnDoModalListener;
import code.inka.android.ui.popup.popupActivity;
import code.inka.android.ui.popup.popupHostActivity;

public class CameraPreviewActivity extends popupHostActivity implements OnClickListener, OnDoModalListener {
	private CameraView camera_view = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.layout_camera_preview );
		
		if( !CameraView.checkCameraHardware(this)) {
			this.doModal( 0, "오류", "카메라 장치를 사용할 수 없습니다.", popupActivity.DIALOG_BTN_TYPE_OK );
			return;
		}
		camera_view = new CameraView( this );
		
		ViewGroup camera_container = ( ViewGroup ) findViewById( R.id.camera_container );
		if( camera_container != null ) {
			camera_container.addView( camera_view );
		}
				
		int btn_ids[] = { R.id.btnTakePicture, R.id.btnChangeCamera };
        for( int i = 0; i < btn_ids.length; i++ ) {
        	Button btn = ( Button ) findViewById( btn_ids[i] );
            if( btn != null ) {
            	btn.setOnClickListener( this );
            }
        }
        
        if( camera_view.getNumberOfCameras() < 2 ) {
        	Button btn = ( Button ) findViewById( R.id.btnChangeCamera );
        	if( btn != null ) {
        		btn.setVisibility( View.GONE );
        	}
        }
	}

	@Override
	protected void onDestroy() {
		camera_view.release();
		camera_view = null;
		
		super.onDestroy();
	}



	@Override
	public void onClick(View v) {
		switch( v.getId()) {
		case R.id.btnTakePicture :
			camera_view.takePicture( "/mnt/sdcard/a.jpg" );
			break;
			
		case R.id.btnChangeCamera :
			camera_view.usingOtherCamera();
			break;
		}
		
	}

	@Override
	public void onDoModalResult(int dlgID, int resultCode) {
		this.finish();		
	}
}
