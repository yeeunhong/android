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
				.setTitle( "�޼��� �ڽ� �׽�Ʈ" )
				.setMessage("���뵵 ���� ���� �ֳ׿�..")
				.setPositiveButton( "��", this )
				.setNegativeButton( "�ƴϿ�", this )
				.create();
		}
		
		case 200 :
		{
			final String strLongMsg = "�����ݷ� ���� ��� ȭ������ ������\n������ �÷��� ��ȭ ����, ���� �޽��� ���� ��������.\n�������� ��ȭ�� ���� ��ſ����ϴ�.\n\n���� ����� �Ʒ��� �����ϴ�.\n1. �����ݸ����縦 �����Ѵ�.\n2. ����� �Ǵ� ���� ������ ����Ѵ�.\n3. ����ó �߰� ��ư�� ���� ���������� ���� �Ǵ� ģ���� ����ó��\n����Ѵ�.";
			
			return new AlertDialog.Builder( this_screen )
			.setIcon( 100 )
			.setTitle( "[�츮���� ����ó ������]" )
			.setMessage( strLongMsg )
			.setPositiveButton( "��", this )
			.setNegativeButton( "�ƴϿ�", this )
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
		
		addButton( "GPS File ����", 1300, ll, vl, this );
		addButton( "Custom Ctrl Test", 1200, ll, vl, this );
		addButton( "�˻� ���� �ٿ�ε�", 1100, ll, vl, this );
		addButton( "�׸��� ����", 1000, ll, vl, this );
		addButton( "Toast Test", 100, ll, vl, this );
		addButton( "Alert Test", 200, ll, vl, this );
		addButton( "Alert Long Text Test", 300, ll, vl, this );
		addButton( "Image Gallery ����", 400, ll, vl, this );
		addButton( "���� ȭ������", 500, ll, vl, this );
		addButton( "����ó ����Ʈ", 600, ll, vl, this );
		addButton( "����ó �����ϱ�", 700, ll, vl, this );
		addButton( "010-8366-9686 ��ȭ�ɱ�", 800, ll, vl, this );
		addButton( "�̹��� �����ϱ�", 900, ll, vl, this );
		
		
		layout.addView( sv );
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId())
		{
		case 100 :
			showToast( "��� ��Ÿ���� ������� �޼��� �Դϴ�.", true );
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
				this_screen.startActivityForResult( Intent.createChooser( i, "�̹��� ���� ����" ), 0 );
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
