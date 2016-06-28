package app.inka.android;

public class AndroidCommonLibAppSo {
	static {
		System.loadLibrary("android_common_lib_app");
	}
	
	public native void test_function();
}
