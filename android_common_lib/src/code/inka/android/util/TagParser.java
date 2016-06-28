package code.inka.android.util;

/**
 * 문자열에 포함된 TAG 정보를 추출해주는 CLASS <br>
 * class ver 1.0
 * @author purehero
 *
 */
public class TagParser {
	public static final String parserTag( String msg, String tag ) {
		int nStart = msg.indexOf( "<" + tag );
		if( nStart < 0 ) return null;

		int nStartEnd = msg.indexOf( ">", nStart + tag.length() + 1 );
		
		int nEnd = msg.indexOf( "</" + tag + ">", nStartEnd );
		if( nEnd < 0 ) return null;
		
		if( nStart + tag.length() + 2 == nEnd - 1 ) {
			return "";
		}
		
		return msg.substring( nStartEnd + 1, nEnd );
	}
	
	public static final String parserAttr( String msg, String attr ) {
		int nStart = msg.indexOf( attr + "=\"" );
		if( nStart < 0 ) {
			nStart = msg.indexOf( attr + "='" );
			if( nStart < 0 ) {
				nStart = msg.indexOf( attr + "=" );
				if( nStart < 0 ) {
					return null;
				}
			}
		}
		
		int nAddLen = 1;
		int nStartEnd = msg.indexOf( ">", nStart + attr.length() + 1 );
		if( nStartEnd == -1 ) {
			nStartEnd = msg.length();
		}
		
		int nEnd = -1;
		final String endTags[] = { "\" ", "' ", "\"", "'", " ", ">" };
		for( int i = 0; i < endTags.length; i++ ) {
			nEnd = msg.indexOf( endTags[i], nStart + attr.length() + 2 );
			if( nEnd != -1 && nStartEnd >= nEnd ) {
				nAddLen = 2;
				break;
			}
		}
						
		if( nEnd < 0 ) return null;
		if( nStart + attr.length() + nAddLen == nEnd ) {
			return "";
		}
		
		String result = msg.substring( nStart + attr.length() + nAddLen, nEnd ); 
		if( result.startsWith("'") && result.endsWith("'")) {
			return result.substring( 1, result.length() - 1);
		} else if( result.startsWith("\"") && result.endsWith("\"")) {
			return result.substring( 1, result.length() - 1);
		}
		return result;
	}
	
	public static final String parserAttr( String msg, String tag, String attr ) {
		int nStart = msg.indexOf( "<" + tag );
		if( nStart < 0 ) return null;

		int nStartEnd = msg.indexOf( ">", nStart + tag.length() + 1 );
		String source = msg.substring( nStart + tag.length() + 1, nStartEnd );
		
		return parserAttr( source, attr );
	}
}
