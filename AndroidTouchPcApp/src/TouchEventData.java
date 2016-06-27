
public class TouchEventData {
	public String strEvent;
	public long time;
	public TouchEventData( String strEvent ) {
		super();
		this.strEvent = strEvent;
		this.time = System.currentTimeMillis();
	}
}
