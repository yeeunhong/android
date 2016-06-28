package kr.co.uniquantum.ui.views.sample;

import kr.co.uniquantum.ui.UIScreenView;
import kr.co.uniquantum.ui.activity.UIScreen;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class UITestView extends UIScreenView implements View.OnClickListener, DialogInterface.OnClickListener
{
	public UITestView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		super.onCreateDialog(id);
		
		switch( id )
		{
		case 100 :
		{
			return new AlertDialog.Builder( this_screen )
				.setIcon( 100 )
				.setTitle( "메세지 박스 테스트" )
				.setMessage("내용도 여기 쓸수 있네요..")
				.setPositiveButton( "예", this )
				.setNegativeButton( "아니요", this )
				.create();
		}
		
		case 200 :
		{
			final String strLongMsg = "포토콜로 예쁜 배경 화면위에 가족의\n사진을 올려서 전화 연결, 문자 메시지 등을 보내세요.\n가족과의 통화가 더욱 즐거워집니다.\n\n설정 방법은 아래와 같습니다.\n1. 포토콜마법사를 실행한다.\n2. 배경이 되는 예쁜 사진을 등록한다.\n3. 연락처 추가 버튼을 눌러 배경사진위에 가족 또는 친구의 연락처를\n등록한다.";
			
			return new AlertDialog.Builder( this_screen )
			.setIcon( 100 )
			.setTitle( "[우리가족 연락처 포토콜]" )
			.setMessage( strLongMsg )
			.setPositiveButton( "예", this )
			.setNegativeButton( "아니요", this )
			.create();
		}
		}
		
		return null;
	}

	@Override
	public void onCreate(UIScreen screen, ViewGroup layout) {
		// TODO Auto-generated method stub
		super.onCreate(screen, layout);
		
		LinearLayout ll = new LinearLayout( screen );
		ll.setOrientation(LinearLayout.VERTICAL);
		
		ScrollView sv = new ScrollView( screen );
		
		sv.addView( ll );
		
		ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams( 
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT );
		
		screen.setTitle("MainMenuView");
		
		addButton( "GPS File 선택", 1300, ll, vl, this );
		addButton( "Custom Ctrl Test", 1200, ll, vl, this );
		addButton( "검색 파일 다운로드", 1100, ll, vl, this );
		addButton( "그리기 연습", 1000, ll, vl, this );
		addButton( "Toast Test", 100, ll, vl, this );
		addButton( "Alert Test", 200, ll, vl, this );
		addButton( "Alert Long Text Test", 300, ll, vl, this );
		addButton( "Image Gallery 보기", 400, ll, vl, this );
		addButton( "이전 화면으로", 500, ll, vl, this );
		addButton( "연락처 리스트", 600, ll, vl, this );
		addButton( "연락처 선택하기", 700, ll, vl, this );
		addButton( "010-8366-9686 전화걸기", 800, ll, vl, this );
		addButton( "이미지 선택하기", 900, ll, vl, this );
		
		
		layout.addView( sv );
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case 100 :
			showToast( "잠깐 나타났다 사라지는 메세지 입니다.", true );
			break;
			
		case 200 :
			showAlert( 100 );
			break;
			
		case 300 :
			showAlert( 200 );
			break;
		
		case 400 :
			changeScreen(new ImageGalleryView( this_screen ));
			break;
			
		case 500 :
			goBack();
			break;
			
		case 600 :
			changeScreen(new ContactNameView( this_screen ));
			break;
			
		case 700 :
			{
				Intent i = new Intent(Intent.ACTION_PICK); 
				i.setData(Uri.parse("content://contacts/phones")); 
				this_screen.startActivityForResult( i, 0 ); 
			}
			break;
			
		case 800 :
			{
				//Intent i = new Intent(Intent.ACTION_CALL); 
				Intent i = new Intent(Intent.ACTION_DIAL);
				i.setData(Uri.parse("tel:01083669686")); 
				this_screen.startActivity( i ); 
			}
			break;
			
		case 900 :
			{
				Intent i = new Intent(Intent.ACTION_GET_CONTENT); 
				i.setType("image/*"); 
				this_screen.startActivityForResult( Intent.createChooser( i, "이미지 파일 선택" ), 0 );
			}
			break;
		case 1000 :
			{
				changeScreen(new DrawViewTest( this_screen ));
			}
			break;
		case 1100 :
			{
				changeScreen(new DownloadFiles( this_screen ));
			}
			break;
			
		case 1200 :
			{
				changeScreen(new DrawCtrlTest( this_screen ));
			}
			break;
		case 1300 :
			{
				changeScreen(new GPSFileList( this_screen ));
			}
			break;
		}
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		showToast( String.format( "dlg_id = %d, which=%d", m_nDialogID, arg1 ), true );
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if( resultCode == Activity.RESULT_OK )
		{
			showToast( data.getData().toString(), true );
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
