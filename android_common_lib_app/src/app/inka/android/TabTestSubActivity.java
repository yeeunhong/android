package app.inka.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TabTestSubActivity extends Activity {
static int g_index = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        TextView tv = new TextView( this );
        setContentView( tv );
        
        tv.setText( String.format( "%d activity", g_index++ ));
    }
}
