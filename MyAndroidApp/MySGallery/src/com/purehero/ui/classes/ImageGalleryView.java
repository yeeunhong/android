package com.purehero.ui.classes;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.mysgallery.R;

public class ImageGalleryView extends ViewPager {
	public ImageGalleryView(Context context) {
		super(context);
		this.setAdapter( new PagerAdapterClass( context ));
	}
	
	public ImageGalleryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setAdapter( new PagerAdapterClass( context ));
	}
	
	private class PagerAdapterClass extends PagerAdapter {
		private int [] imgIDs = { R.drawable.img01, R.drawable.img02, R.drawable.img03, R.drawable.img04 };
		
		public PagerAdapterClass( Context context ) {
            super();
        }
		
		@Override
		public int getCount() {
			return imgIDs.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem( ViewGroup container, int position ) {
			ViewPager viewPager = ( ViewPager ) container;
			
			ImageViewEx view = null;
			if( position >= viewPager.getChildCount()) {
				view = new ImageViewEx( getContext() );
				viewPager.addView( view, position );
				
			} else {
				view = ( ImageViewEx ) viewPager.getChildAt( position );
			}
			
			view.setImageResource( imgIDs[position] );
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager viewPager = ( ViewPager ) container;			
			viewPager.removeView( container );
		}		
	};
}
