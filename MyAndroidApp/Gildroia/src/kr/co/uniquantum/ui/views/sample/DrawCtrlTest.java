package kr.co.uniquantum.ui.views.sample;

import kr.co.uniquantum.R;
import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.UIScreenViewEx;
import kr.co.uniquantum.ui.UIControls.*;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class DrawCtrlTest extends UIScreenView  implements View.OnClickListener
{
	public DrawCtrlTest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		UIScreenViewEx view = new UIScreenViewEx( screen );
		view.setLongClickable( true );
		view.setOnLongClickListener( view );
		layout.addView( view );
		
		view.controls.AddButton( 10, 10, 100, 40, "첫번째 버튼", 100, this ).setType( 0 );
		view.controls.AddButton( 10, 60, 100, 40, "두번째 버튼", 200, this ).setType( 1 );
		view.controls.AddImgButton( 10, 110, R.drawable.bnt_search, 2, null, 300, this );
		view.controls.AddCheckButton( 120, 10, 20, 20, 500 );
		view.controls.AddCheckButton( 120, 50, 30, 30, 600 );
		view.controls.AddCheckButton( 120, 90, 40, 40, 700 );
		view.controls.AddCheckButton( 120, 140, 50, 50, 800 );
		view.controls.AddImgCheckButton( 120, 300, 
				android.R.drawable.checkbox_off_background,
				android.R.drawable.checkbox_on_background, 
				900 );
		
		view.controls.AddImgCloseButton(  180, 0, 1000, this );
		
		UIAniImage aniImage = view.controls.AddAniImage( 50, 300, 1200 );
		aniImage.AddBitmap( R.drawable.emo_im_angel );
		aniImage.AddBitmap( R.drawable.emo_im_cool );
		aniImage.AddBitmap( R.drawable.emo_im_crying );
		aniImage.AddBitmap( R.drawable.emo_im_foot_in_mouth );
		aniImage.AddBitmap( R.drawable.emo_im_happy );
		aniImage.AddBitmap( R.drawable.emo_im_kissing );
		aniImage.AddBitmap( R.drawable.emo_im_laughing );
		aniImage.AddBitmap( R.drawable.emo_im_lips_are_sealed );
		aniImage.AddBitmap( R.drawable.emo_im_money_mouth );
		aniImage.AddBitmap( R.drawable.emo_im_sad );
		aniImage.AddBitmap( R.drawable.emo_im_surprised );
		aniImage.AddBitmap( R.drawable.emo_im_tongue_sticking_out );
		aniImage.AddBitmap( R.drawable.emo_im_undecided );
		aniImage.AddBitmap( R.drawable.emo_im_winking );
		aniImage.AddBitmap( R.drawable.emo_im_wtf );
		aniImage.AddBitmap( R.drawable.emo_im_yelling );
		aniImage.start( 2 );
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.showToast( String.format( "ID[%d] Text[%s]", v.getId(), ( String )v.getTag()), true );
	}
	
	
}
