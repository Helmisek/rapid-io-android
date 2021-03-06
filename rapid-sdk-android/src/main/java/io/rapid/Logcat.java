package io.rapid;

import android.support.annotation.NonNull;
import android.util.Log;


/**
 * Use for internal logging only. These logs are not visible when using release version of the SDK.
 */
class Logcat {
	private static final String TAG = "Rapid SDK internal";

	private static final boolean IS_ENABLED = !BuildConfig.RELEASE;
	private static final boolean SHOW_CODE_LOCATION = true;


	private Logcat() {}


	public static void d(@NonNull String msg, Object... args) {
		if(IS_ENABLED) Log.d(TAG, getCodeLocation().toString() + formatMessage(msg, args));
	}


	public static void e(@NonNull String msg, Object... args) {
		if(IS_ENABLED) Log.e(TAG, getCodeLocation().toString() + formatMessage(msg, args));
	}


	public static void e(Throwable throwable, @NonNull String msg, Object... args) {
		if(IS_ENABLED) Log.e(TAG, getCodeLocation().toString() + formatMessage(msg, args), throwable);
	}


	public static void i(@NonNull String msg, Object... args) {
		if(IS_ENABLED) Log.i(TAG, getCodeLocation().toString() + formatMessage(msg, args));
	}


	public static void v(@NonNull String msg, Object... args) {
		if(IS_ENABLED) Log.v(TAG, getCodeLocation().toString() + formatMessage(msg, args));
	}


	public static void w(@NonNull String msg, Object... args) {
		if(IS_ENABLED) Log.w(TAG, getCodeLocation().toString() + formatMessage(msg, args));
	}


	public static void wtf(@NonNull String msg, Object... args) {
		if(IS_ENABLED) Log.wtf(TAG, getCodeLocation().toString() + formatMessage(msg, args));
	}


	public static void printStackTrace(Throwable throwable) {
		e(throwable, "");
	}


	@NonNull
	private static String formatMessage(@NonNull String msg, @NonNull Object... args) {
		return args.length == 0 ? msg : String.format(msg, args);
	}


	private static CodeLocation getCodeLocation() {
		return getCodeLocation(3);
	}


	private static CodeLocation getCodeLocation(int depth) {
		StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		StackTraceElement[] filteredStackTrace = new StackTraceElement[stackTrace.length - depth];
		System.arraycopy(stackTrace, depth, filteredStackTrace, 0, filteredStackTrace.length);
		return new CodeLocation(filteredStackTrace);
	}


	private static class CodeLocation {
		private final String mThread;
		private final String mFileName;
		@NonNull private final String mClassName;
		private final String mMethod;
		private final int mLineNumber;


		CodeLocation(StackTraceElement[] stackTrace) {
			StackTraceElement root = stackTrace[0];
			mThread = Thread.currentThread().getName();
			mFileName = root.getFileName();
			String className = root.getClassName();
			mClassName = className.substring(className.lastIndexOf('.') + 1);
			mMethod = root.getMethodName();
			mLineNumber = root.getLineNumber();
		}


		@NonNull
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			if(SHOW_CODE_LOCATION) {
				builder.append('(');
				builder.append(mFileName);
				builder.append(':');
				builder.append(mLineNumber);
				builder.append(") ");
			}
			return builder.toString();
		}
	}
}
