package io.rapid;


import android.content.Context;

import com.jakewharton.disklrucache.DiskLruCache;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


class SubscriptionDiskCache {

	private static final int DEFAULT_INDEX = 0;
	private DiskLruCache mCache;
	private boolean mEnabled = true;


	public SubscriptionDiskCache(Context context, String apiKey, int maxSizeInMb) throws IOException {
		// TODO better cache dir
		mCache = DiskLruCache.open(new File(context.getCacheDir() + "/rapid/" + apiKey), 0, 1, maxSizeInMb * 1_000_000);
	}


	public void setMaxSize(int maxSizeInMb) {
		mCache.setMaxSize(maxSizeInMb * 1_000_000);
	}


	public synchronized String get(Subscription subscription) throws IOException, JSONException, NoSuchAlgorithmException {
		if(!mEnabled)
			return null;
		String fingerprint = subscription.getFingerprint();
		DiskLruCache.Snapshot record = mCache.get(fingerprint);
		if(record != null) {
			String jsonValue = record.getString(DEFAULT_INDEX);
			Logcat.d("Reading from subscription cache. key=%s; value=%s", fingerprint, jsonValue);
			return jsonValue;
		}
		Logcat.d("Reading from disk subscription cache. key=%s; value=null", fingerprint);
		return null;
	}


	public synchronized void put(Subscription subscription, String jsonValue) throws IOException, JSONException, NoSuchAlgorithmException {
		if(!mEnabled)
			return;
		String fingerprint = subscription.getFingerprint();
		DiskLruCache.Editor editor = mCache.edit(fingerprint);
		editor.set(DEFAULT_INDEX, jsonValue);
		editor.commit();
		Logcat.d("Saving to disk subscription cache. key=%s; value=%s", fingerprint, jsonValue);
	}


	public synchronized void clear() throws IOException {
		mCache.delete();
	}


	public synchronized void remove(Subscription subscription) throws IOException, NoSuchAlgorithmException, JSONException {
		if(!mEnabled)
			return;
		String fingerprint = subscription.getFingerprint();
		mCache.remove(fingerprint);
		Logcat.d("Removing from disk subscription cache. key=%s", fingerprint);
	}


	public void setEnabled(boolean cachingEnabled) {
		mEnabled = cachingEnabled;
	}
}
