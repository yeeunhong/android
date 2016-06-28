package code.inka.android.ui.listview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import code.inka.android.libs.R;
import code.inka.android.ui.util.UTIL;



public class SectionListViewController extends ListView {
	public static final int PINNED_HEADER_VISIBLE = 0;
    public static final int PINNED_HEADER_HIDDEN = 1;
    public static final int PINNED_HEADER_PUSHED_UP = 2;
    
	protected Context context;
	protected OnListViewListener 	listViewListener = null;
	
	protected int cell_res_layout 		= -1;
	protected int section_res_layout 	= -1;
	
	private int mHeaderViewWidth		= 0;
    private int mHeaderViewHeight		= 0;
	
	protected ArrayList<Object> 	items = null; 
	protected ArrayAdapter<Object> 	adapter = null;
	
	public SectionListViewController(Context context) {
		super(context);
		
		this.context = context;
	}

	/**
	 * OnListViewListener을 설정 함
	 * @param listener OnListViewListener 객체
	 * @see OnListViewListener
	 */
	public void setOnListViewListener( OnListViewListener listener ) {
		listViewListener = listener;
	}
	
	private Object currentSectionObject = null;
	protected View getListCellView( int position, View convertView, ViewGroup parent) {
		Object object = items.get(position);
		
		if( object instanceof SectionItem ) {
			currentSectionObject = object;
			
			if( section_res_layout != -1 ) {
				if( convertView == null ) {
					convertView = UTIL.GetExternalView( context, section_res_layout );
				} else if( convertView.getId() != section_res_layout ) {
					convertView = UTIL.GetExternalView( context, section_res_layout );	
				} 
				
				if( convertView != null ) {
					headerView = ( TextView ) convertView.findViewById( R.id.sectionText );
					if( headerView != null ) {
						String name = ((SectionItem) object ).name;
						headerView.setText( name );
					}					
				}
			}			
		} else {
		
			if( listViewListener != null ) {
				if( convertView == null ) {
					convertView = UTIL.GetExternalView( context, cell_res_layout );				
				} else if( convertView.getId() != cell_res_layout ) {
					convertView = UTIL.GetExternalView( context, cell_res_layout );
				}
				
				convertView = listViewListener.OnGetListCellView( this, position, items.get( position ), convertView );
			}
		}
		
		if( currentSectionObject != null && headerView != null ) {
			String name = ((SectionItem) currentSectionObject ).name;
			headerView.setText( name );
		}
		
		return convertView;
	}
	
	class SectionItem {
		public String name; 
		public SectionItem(String sectionName) {
			name = sectionName;
		}
	};
	class SectionListViewAdapter extends ArrayAdapter<Object> {
		public SectionListViewAdapter(Context context, ArrayList<Object> items) {
			super(context, -1, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getListCellView(position, convertView, parent);
		}
	}
	
	/**
	 * SectionListViewController 초기화 합니다. 
	 * @param container customListView가 표시된 ViewGroup 객체, 입력된 container안에 fill_parent 값으로 customListView가 add 됨
	 * @param cell_res_layout customListView의 홀수 컬럼에 사용될 LayoutID
	 * @param section_res_layout customListView의 짝수 컬럼에 사용될 LayoutID, -1 을 입력하면 cell_res_layout1 만을 사용함
	 */
	public void init( ViewGroup container, int cell_res_layout, int section_res_layout ) {
		items = new ArrayList<Object>(); 
		adapter = new SectionListViewAdapter(context, items);
		
		this.cell_res_layout 		= cell_res_layout;
		this.section_res_layout 	= section_res_layout;
		
		if( container != null ) {
			ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT );
	 
			container.addView( this, vl );
		}
		
		setAdapter(adapter);
				
		//this.setCacheColorHint( 0x00000000 );
	}
	
	/**
	 * ListView의 항목중 수정된 내용을 갱신합니다. 
	 */
	public void UpdateList() {
		items.clear();
		ArrayList<Object> sectionItems = new ArrayList<Object>(); 		 
		
		if( listViewListener != null ) {
			listViewListener.OnSetListData( this, sectionItems );
		}
		
		for( Object section_item : sectionItems ) {
			if( section_item instanceof SectionListData ) {
				SectionListData listData = ( SectionListData ) section_item;
				
				String sectionName = listData.getSectionName();
				items.add( new SectionItem( sectionName ));
				items.addAll( listData );
			}
		}
		
		adapter.notifyDataSetChanged();
	}

	protected boolean isSectionHeaderView(int position) {
		Object object = items.get(position);
		
		if( object instanceof SectionItem ) {
			return true;
		}
		return false;
	}
	
	protected int getPinnedHeaderState(int position) {
		if (isSectionHeaderView(position+1)) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}
	
	private TextView headerView = null;
	public View getPinnedHeaderView(int position) {
		
		if( section_res_layout != -1 ) {
			if( headerView == null ) {
				View v = UTIL.GetExternalView( context, section_res_layout );
				headerView = ( TextView ) v.findViewById( R.id.sectionText );				
			} 
						
			//headerView.setBackgroundColor(Color.GREEN);
		}
		
		if( headerView != null ) {
			Object object = items.get(position);
			if( object instanceof SectionItem ) {
				headerView.setText( ((SectionItem) object).name );
			}
		}
		
		return headerView;
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        View headerView = getPinnedHeaderView(getFirstVisiblePosition());
        if (headerView != null) {
            measureChild(headerView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = headerView.getMeasuredWidth();
            mHeaderViewHeight = headerView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        
    	View headerView = getPinnedHeaderView(getFirstVisiblePosition());
    	if (headerView != null) {
    		headerView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
    	}
        
    }
    
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
		
    	int position = getFirstVisiblePosition();
    	View headerView = getPinnedHeaderView(position);

    	if (headerView != null) {
    		ColorDrawable backgroundDrawable = (ColorDrawable) headerView.getBackground();
    		int state = getPinnedHeaderState(position);
    		switch(state) {
    		case PINNED_HEADER_PUSHED_UP:
    			headerView.setVisibility(View.VISIBLE);
    			View firstView = getChildAt(0);
    			int gap = firstView.getTop() + firstView.getHeight();
    			if (gap <= headerView.getHeight()) {
    				int pushGap = gap-headerView.getHeight();
    				
    				if (backgroundDrawable != null) {
        				//int alpha = this.alpha - (int) ((float)(-1*pushGap)/headerView.getHeight() * this.alpha);
    					int alpha = 255 - (int) ((float)(-1*pushGap)/headerView.getHeight() * 255);
        				backgroundDrawable.setAlpha(alpha);
    				}
    				headerView.layout(0,pushGap,  mHeaderViewWidth, mHeaderViewHeight+pushGap);
    			}
    			else {
        			headerView.setVisibility(View.VISIBLE);
    				if (backgroundDrawable != null) {
    					//headerView.getBackground().setAlpha(alpha);
    					headerView.getBackground().setAlpha(255);
    				}
        			headerView.layout(0, 0,  mHeaderViewWidth, mHeaderViewHeight);        				
    			}
        		drawChild(canvas, headerView, getDrawingTime());
    			break;
    		case PINNED_HEADER_HIDDEN:
    			headerView.setVisibility(View.GONE);
    			break;
    		case PINNED_HEADER_VISIBLE:
    			headerView.setVisibility(View.VISIBLE);
				if (backgroundDrawable != null) {
					//headerView.getBackground().setAlpha(alpha);
					headerView.getBackground().setAlpha(255);
				}
    			headerView.layout(0, 0,  mHeaderViewWidth, mHeaderViewHeight);
        		drawChild(canvas, headerView, getDrawingTime());
    			break;
    		}
    	}
	}
	
	
	
}
