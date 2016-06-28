package code.inka.android.ui.listview;

import java.util.ArrayList;

public class SectionListData extends ArrayList<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String name;
		
	public SectionListData( String name ) {
		this.name = name;
	}
	
	public String getSectionName() {
		return name;
	}
}
