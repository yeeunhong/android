package com.uniquantum.www;

public class DrawingFacade {  

	private static EventListener listener;
	  
	public static interface EventListener 
	{             
		void OnMessage(String text);       
		void OnImageUpdate (int[] pixels, int x ,int y, int w, int h);	
		void OnSysError(String text);
	}
	  
	public static void setListener (EventListener l) {
		listener = l;
	}  
	        
	public static native int DrawingCreate(String sdcardpath);
	public static native int DrawingResize(int width, int height);
	public static native int DrawingRelease();
	public static native int keyClicked(int key);
	public static native int moved(int dx, int dy);
	         
	public static native void SetLocationPos( long lon, long lat, long angle );
    public static native void changeDirectionMode( int mode );
                      
	/***************************** ******************************
	 * C - Callbacks  
	 ***********************************************************/
    
	//@SuppressWarnings("unused")
	private static void OnMessage(String text) {
		if ( listener != null)
			listener.OnMessage(text); 
	}  
	
	//@SuppressWarnings("unused")
	private static void OnImageUpdate(int[] pixels, int x ,int y, int w, int h) {
		if ( listener != null)
			listener.OnImageUpdate(pixels, x, y, w, h);  
	} 
	
	//@SuppressWarnings("unused")
	private static void OnSysError(String message) {
		if ( listener != null)
			listener.OnSysError(message + " - Please report this error.");
	}

}
