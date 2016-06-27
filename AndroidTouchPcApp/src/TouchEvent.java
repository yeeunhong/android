

import java.util.Locale;

public class TouchEvent {
	private DeviceConnector deviceConnector;
	private String deviceName = null;
	private boolean enable = false;
	
	private TouchEventHistory history = new TouchEventHistory();
	
	public void init( DeviceConnector _deviceConnector ) {
		deviceConnector = _deviceConnector;
		
		deviceConnector.command( "sendevent", checkIsEnable, 500 ); 
		deviceConnector.command( "getevent -p", getTouchEventDevice, 500 );
	}
	
	CommandCallback getTouchEventDevice = new CommandCallback() {
		String findDeviceName = null;
		boolean found = false;
		
		@Override
		public void callback(String data) {
			if( found ) return;
			
			String strLine = data.trim();
			if( strLine.startsWith( "add device" )) {
				String token[] = strLine.split(":");
				if( token.length == 2 ) {
					findDeviceName = token[1].trim();
				}
			} else if( strLine.startsWith( "name:" )) {
				String token[] = strLine.split(":");
				if( token.length == 2 ) {
					if( token[1].contains( "touchscreen" ) || token[1].contains( "touch_dev" ) ) {
						setTouchEventDeviceName( findDeviceName );
						found = true;
					}
				}
			}
		}
	};
	
	CommandCallback printCallback = new CommandCallback() {
		@Override
		public void callback(String data) {
			System.out.println( data );
		}
		
	};
	
	/**
	 * TouchEvent 발생 장치로 사용할 장치명을 설정한다. 
	 * 
	 * @param name
	 */
	private void setTouchEventDeviceName( String name ) {
		deviceName = name;
		System.out.println( "set TouchEventDeviceName : " + deviceName );
	}
	
	/**
	 * Android 장치에 TouchEvent 값을 읽고 쓸 수 있는지를 확인 합니다. 
	 * 
	 * @return
	 */
	public boolean isEnable() { return enable; }
	CommandCallback checkIsEnable = new CommandCallback() {
		@Override
		public void callback(String data) {
			if( !data.startsWith("use: sendevent")) {
				System.out.println( String.format( "'%s' is not start with 'use: sendevent'", data ));
				return;
			}			
			enable = true;
		}
	};
	
	public void touchScreen( int x, int y ) {
		touchDOWN( x, y );
		touchUP();
	}
	
	public void touchMOVE( int x, int y ) {
		if( !enable || deviceName == null ) {
			System.out.println( "touchUP cat't working, touchevent not enable" );
			return;
		}
		
		int type[]  = { 0x0003, 0x0003, 0x0000 };
		int code[]  = { 0x0035, 0x0036, 0x0000 };
		int value[] = { x, y, 0 };
		
		for( int i = 0; i < type.length; i++ ) {
			String prog = String.format( Locale.getDefault(), "sendevent %s %d %d %d", deviceName, type[i], code[i], value[i] );
			deviceConnector.command( prog, printCallback, 0 );			
		}
	}
	
	public void touchDOWN( int x, int y ) {
		if( !enable || deviceName == null ) {
			System.out.println( "touchUP cat't working, touchevent not enable" );
			return;
		}
		
		int type[]  = { 0x0003, 0x0001, 0x0003, 0x0003, 0x0003, 0x0000 };
		int code[]  = { 0x0039, 0x014a, 0x0035, 0x0036, 0x0030, 0x0000 };
		int value[] = { 0, 1, x, y, 6, 0 };
		
		for( int i = 0; i < type.length; i++ ) {
			String prog = String.format( Locale.getDefault(), "sendevent %s %d %d %d", deviceName, type[i], code[i], value[i] );
			deviceConnector.command( prog, printCallback, 0 );			
		}
	}
	
	public void touchUP() {
		if( !enable || deviceName == null ) {
			System.out.println( "touchUP cat't working, touchevent not enable" );
			return;
		}
		
		int type[]  = { 0x0003, 0x0001, 0x0000 };
		int code[]  = { 0x0039, 0x014a, 0x0000 };
		int value[] = { -1, 0, 0 };
		
		for( int i = 0; i < type.length; i++ ) {
			String prog = String.format( Locale.getDefault(), "sendevent %s %d %d %d", deviceName, type[i], code[i], value[i] );
			deviceConnector.command( prog, printCallback, 0 );			
		}
	}
	
	/**
	 * @param callback
	 */
	public void touchHistoryStart( CommandCallback callback ) {
		history.start( deviceName );
	}
	
	/**
	 * 
	 */
	public void touchHistoryStop() {
		history.stop();
	}
	
	public void touchHistoryPlay() {
		history.play( deviceConnector );
	}
}
