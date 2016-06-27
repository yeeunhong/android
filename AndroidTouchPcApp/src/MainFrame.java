import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;


public class MainFrame extends JFrame implements ActionListener {
	DeviceConnector devConnecter = new DeviceConnector(); 
	TouchEvent		touchEvent   = new TouchEvent();
	
	final int BTN_ID_CONNECTION_TO_DEVICE 	= 100;
	final int BTN_ID_DISCONNECTION			= 200;
	final int BTN_ID_START					= 300;
	final int BTN_ID_END					= 400;
	
	final int BTN_ID_TOUCHEVENT_SAVE_START	= 1000;
	final int BTN_ID_TOUCHEVENT_SAVE_END	= 1100;
	final int BTN_ID_TOUCHEVENT_PLAY_START	= 1200;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ButtonEx btn = ( ButtonEx ) e.getSource();
		switch( btn.ID ) {
		case BTN_ID_CONNECTION_TO_DEVICE :
			if( devConnecter.connection()) {
				touchEvent.init(devConnecter);
			}
			break;
		case BTN_ID_DISCONNECTION : 
			devConnecter.close();
			break;
			
		case BTN_ID_START :
			System.out.println("BTN_ID_START");
			break;
		case BTN_ID_END :
			System.out.println("BTN_ID_END");
			break;
			
		case BTN_ID_TOUCHEVENT_SAVE_START :
			touchEvent.touchHistoryStart(null);
			break;
		case BTN_ID_TOUCHEVENT_SAVE_END	:
			touchEvent.touchHistoryStop();
			break;
		case BTN_ID_TOUCHEVENT_PLAY_START :
			touchEvent.touchHistoryPlay();
			break;
		}
	}
	
	public MainFrame() {
		getContentPane().setFont(new Font("바탕체", Font.PLAIN, 12));
		setFont(new Font("바탕체", Font.PLAIN, 12));
		getContentPane().setLayout(null);
		
		AddButton( getContentPane(), BTN_ID_CONNECTION_TO_DEVICE, 27, 23, 275, 41, "Connection to device" );
		AddButton( getContentPane(), BTN_ID_DISCONNECTION, 308, 23, 105, 41, "Disconnection" );
		AddButton( getContentPane(), BTN_ID_START, 308, 91, 99, 41, "Start" );
		AddButton( getContentPane(), BTN_ID_END, 27, 91, 275, 41, "End" );
		
		AddButton( getContentPane(), BTN_ID_TOUCHEVENT_SAVE_START,  30, 380, 150, 41, "Start" );
		AddButton( getContentPane(), BTN_ID_TOUCHEVENT_SAVE_END,   190, 380, 150, 41, "End" );
		AddButton( getContentPane(), BTN_ID_TOUCHEVENT_PLAY_START, 350, 380, 150, 41, "Play" );
	}
	
	class ButtonEx extends Button {
		private static final long serialVersionUID = 1L;
		public int ID;
		public ButtonEx() throws HeadlessException { super(); }
		public ButtonEx(String label) throws HeadlessException { super(label); 	}
	};
	
	public void AddButton( Container container, int id, int x, int y, int w, int h, String title ) {
		ButtonEx btn = new ButtonEx( title );
		btn.ID = id;
		btn.addActionListener( this );
		btn.setBounds( x, y, w, h );
		container.add( btn );
	}
	
	
	public static void main( String args[] ) {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int scr_width  = ( int ) screenSize.getWidth();
		int scr_height = ( int ) screenSize.getHeight();
		int win_width  = 640;
		int win_height = 480;
		
		MainFrame mainFrame = new MainFrame();
		mainFrame.pack();
		mainFrame.setBounds(( scr_width - win_width ) / 2, ( scr_height - win_height ) / 2, win_width, win_height );
		mainFrame.setVisible( true );
	}


	
}
