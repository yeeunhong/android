package com.arista.enginetest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;

import android.content.Context;

public class ParsingEngine14111 {
	private Context context;
	private InputStream in = null;
	private Element root;
	
	public ParsingEngine14111( Context context, String paramString ) throws Exception {
		this.context = context;
		init( paramString );
	}
	
	private void init(String paramString) throws Exception {
		parsing(paramString);
	}

	private void parsing(String paramString) throws Exception {
		DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			
		try
		{
			in = context.getResources().getAssets().open("st12124.txt");
			root = localDocumentBuilderFactory.newDocumentBuilder().parse(in).getDocumentElement();
			closeStream();
			return;
			
		} catch (MalformedURLException localMalformedURLException) {
			localMalformedURLException.printStackTrace();
		}
	}
	
	public void closeStream() {
		try {
			in.close();
			return;
			
	    }  catch (IOException localIOException)  {
	    	localIOException.printStackTrace();
	    }
	}

	public Element getRootElement() {
		return root;
	}
}
