package code.inka.android.log;

public class Log {
	private static String m_tag = "";
	
	public static void setLogTag( String tag ) {
		m_tag = tag;
	}

	public static final String getCalledClassName( int depth ) {
		String className = new Throwable().getStackTrace()[depth].getClassName(); 
		if( className != null ) {
			String sp[] = className.split( "\\." );
			if( sp != null && sp.length > 1 ) {
				return sp[sp.length-1];
			} else {
				return className;
			}
		}
		return "";		
	}
	
	public static final String getCalledFuncName( int depth ) {
		return new Exception().getStackTrace()[depth].getMethodName();
	}
	
	public static final int getCalledLineNumber( int depth ) {
		return new Throwable().getStackTrace()[depth].getLineNumber();
	}
	
	public static final void message( String format, Object... obj ) {
		if( format.endsWith("\n")) {
			format = format.substring( 0, format.length() - 1 );
		}
		message( String.format( format, obj ));		
	}
	
	public static final void message( String msg ) {
		String pre = "";//String.format( "%s::%s[%d]==>", getCalledClassName(2), getCalledFuncName(2), getCalledLineNumber(2) );
		android.util.Log.d( m_tag, pre + msg );
	}
}
