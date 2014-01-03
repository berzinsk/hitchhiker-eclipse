package com.hitchhiker.mobile.tools;

import android.util.Log;

public class Logger {
	private String tag;
	
	public Logger(Class<?> clazz) {
		tag = clazz.getName();
	}

	public void debug(String message) {
		Log.d(tag, message);
	}

	public void warn(String message) {
		Log.w(tag, message);
	}
	
	public void error(String message) {
		Log.e(tag, message);
	}

	public void error(String message, Throwable reason) {
		Log.e(tag, message, reason);
	}
}