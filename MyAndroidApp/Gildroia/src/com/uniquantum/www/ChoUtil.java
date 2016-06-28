package com.uniquantum.www;

import android.app.AlertDialog;
import android.content.Context;

public class ChoUtil {
	public static void MessageBox(Context context, String text) {
		new AlertDialog.Builder(context)
		.setMessage(text)
		.setPositiveButton("�ݱ�", null)
		.show();
	}

	public static void MessageBox(Context context, String title, String text) {
		new AlertDialog.Builder(context)
		.setMessage(text)
		.setTitle(title)
		.setPositiveButton("�ݱ�", null)
		.show();
	}
}

