package com.purehero.mysgallery;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.mysgallery.R;
import com.purehero.ui.classes.ImageViewEx;

/**
 * 단말기의 갤러리에서 이미지를 선택해서 화면에 표시해 주는 Activity
 * 
 * 
 * @author purehero2
 *
 */
public class MainActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btn = ( Button ) this.findViewById( R.id.btnSelImgFromGallery );
        if( btn != null ) {
        	btn.setOnClickListener( this );
        }
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch( requestCode ) {
		case REQ_CODE_PICK_IMAGE :
			onPickImageResult( resultCode, data );
			break;
		}
		
	}
    
	@Override
	public void onClick(View v) {
		switch( v.getId()) {
		case R.id.btnSelImgFromGallery :
			OnBtnSelImgFromGallery( this );
			break;
		}
	}

    private static final int REQ_CODE_PICK_IMAGE = 0x1110;
    
	/**
	 * 단말기의 갤러리 Activity을 호출한다. 
	 * 
	 * @param mainActivity
	 */
	private void OnBtnSelImgFromGallery(Activity mainActivity) {
		Intent intent = new Intent(
                Intent.ACTION_PICK, 
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
		mainActivity.startActivityForResult(intent, REQ_CODE_PICK_IMAGE);
	}

	/**
	 * 갤러리 화면에서 이미지가 선택되면 호출되는 함수, 선택된 이미지를 화면의 ImageView에 표시해 준다. 
	 * 
	 * @param resultCode
	 * @param data
	 */
	private void onPickImageResult(int resultCode, Intent data) {
		if( resultCode != RESULT_OK ) {
			return;
		}
		
		Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
         
        ImageViewEx imageView = (ImageViewEx) findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
	}
}
