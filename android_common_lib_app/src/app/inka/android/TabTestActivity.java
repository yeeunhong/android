package app.inka.android;

import android.os.Bundle;
import code.inka.android.ui.tabview.TabViewController;

public class TabTestActivity extends TabViewController {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_tab_view_controller);
                
        addTab( R.drawable.bu_1_d, 	R.drawable.bu_1_n, 	TabTestSubActivity.class,	"home", 	false );
		//addTab( R.drawable.bu_2_d, 	R.drawable.bu_2_n, 	TabTestSubActivity.class, 	"folder", 	false );
        addTab( R.drawable.bu_2_d, 	R.drawable.bu_2_n, 	ListTestActivity.class, 	"folder", 	false );
		addTab( R.drawable.bu_33_d, R.drawable.bu_33_n, SectionListTestActivity.class, 	"playlist", false );
		addTab( R.drawable.bu_5_d, 	R.drawable.bu_5_n, 	TabTestSubActivity.class, 	"setting", 	false );
    } 
}
