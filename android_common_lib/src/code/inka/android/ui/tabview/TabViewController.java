package code.inka.android.ui.tabview;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import code.inka.android.log.Log;

public class TabViewController extends TabActivity {
	TabHost m_tabHost = null;
	int m_nTabIndex = -1;	
	ArrayList<TabButtonView> m_TabButtonViews = new ArrayList<TabButtonView>();
	
	private Animation m_slideInLeft 	= null;
	private Animation m_slideOutRight 	= null;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView( R.layout.tabview_bottom_btn ); 	// 버튼을 하단에 놓을 때 사용
                                                         	// 버튼을 상단에 놓을 때는 필요 없음
	}
		
	public void addTab( int normal_img_id, int pressed_img_id, Class<?> activity, String param, boolean alwaysTop ){
		if( m_tabHost == null ) {
			m_tabHost = getTabHost();
			if( m_tabHost == null ) return;
		}
		
		TabHost.TabSpec newTabSpec = m_tabHost.newTabSpec( param );//String.format( "Tab_%d", m_nTabCount ));
		if( newTabSpec != null ) {
						
			TabButtonView view =  new TabButtonView( this, m_tabHost.getTabWidget().getChildCount(), normal_img_id, pressed_img_id, false );
			if( view != null ) {
				newTabSpec.setIndicator( view );
				m_TabButtonViews.add( view );
			}
			
			Intent newIntent = new Intent( this, activity );
			if( alwaysTop ) newIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ); // <== 이게 있으면 항상 최상의로 와서 Activity가 새로 생성된다. 
			if( param != null ) newIntent.putExtra( "param", param );
			newTabSpec.setContent( newIntent );
			
			m_tabHost.addTab( newTabSpec );
		}
	}
		
	
	public void onTabClicked(int index) {
		Log.message( "onTabClicked %d", index );
		if( m_nTabIndex == index ) {
			
		} else {
			changeTabIndex(index);
		}
	}
	
	public void changeTabIndex( int index ) {
		Animation animation = null;
		
		if( m_nTabIndex == index ) {
			return;
		}		
		if( m_nTabIndex > index ) {
			animation = m_slideOutRight;
		} else {
			animation = m_slideInLeft;
		}
		
		m_tabHost.setCurrentTab(index);
		if( m_nTabIndex >= 0 ) {
			m_TabButtonViews.get( m_nTabIndex ).setTabImage(false);
		}
		
		// 선택된 탭 이미지 바꾸기
		m_nTabIndex = m_tabHost.getCurrentTab();
		m_TabButtonViews.get( m_nTabIndex ).setTabImage(true);
		
		View currentView = m_tabHost.getCurrentView();
		currentView.startAnimation( animation );
	}
	
	private void init_content() {
		m_tabHost = getTabHost();
		
		m_slideInLeft 	= AnimationUtils.loadAnimation( this, android.R.anim.slide_in_left);
		m_slideOutRight	= AnimationUtils.loadAnimation( this, android.R.anim.slide_out_right );
		
		m_tabHost.setOnTabChangedListener( new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				changeTabIndex( m_tabHost.getCurrentTab());
			}
		});
	}
	
	@Override 
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		init_content();
	}
	
	@Override 
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		init_content();
	}

	@Override 
	public void setContentView(View view) {
		super.setContentView(view);
		init_content();
	}
	
	class TabButtonView extends LinearLayout {
		int	imageOn, imageOff, tabIndex;
		Boolean isTabOn;
		ImageView ivButton;

		public TabButtonView(Context c, int index, int drawableOn, int drawableOff, Boolean isOn) {
			super(c);

			imageOn = drawableOn;
			imageOff = drawableOff;
			tabIndex = index;
			isTabOn = isOn;

			ivButton = new ImageView(c);
			setTabImage(isTabOn);

			setOrientation(LinearLayout.VERTICAL);
			addView(ivButton);
			
			ivButton.setOnClickListener( new OnClickListener(){
				public void onClick(View v) {
			        onTabClicked( tabIndex );
				}} );
		}

		public void setTabImage(Boolean isSettingOn) {
			isTabOn = isSettingOn;
			if(isTabOn) {
				ivButton.setImageResource(imageOn);
			} else {
				ivButton.setImageResource(imageOff);
			}

			return;
		}
	}    
}
