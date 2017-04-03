package io.rapid;


import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.rapid.converter.RapidGsonConverter;
import io.rapid.converter.RapidJsonConverter;


public class Rapid {
	private static Map<String, Rapid> sInstances = new HashMap<>();
	private final String mApiKey;
	private RapidJsonConverter mJsonConverter;
	private Handler mHandler;
	private RapidConnection mRapidConnection;

	private CollectionProvider mCollectionProvider;


	private Rapid(Context context, String apiKey) {
		mApiKey = apiKey;
		mJsonConverter = new RapidGsonConverter(new Gson());
		mCollectionProvider = new CollectionProvider();
		mHandler = new Handler();
		mRapidConnection = new WebSocketRapidConnection(context, mHandler, new RapidConnection.Listener() {
			@Override
			public void onMessage(MessageBase message) {
				if(message.getMessageType() == MessageBase.MessageType.VAL) {
					MessageVal valMessage = ((MessageVal) message);
					mCollectionProvider.findCollectionByName(valMessage.getCollectionId()).onValue(valMessage);
				} else if(message.getMessageType() == MessageBase.MessageType.UPD) {
					MessageUpd updMessage = ((MessageUpd) message);
					mCollectionProvider.findCollectionByName(updMessage.getCollectionId()).onUpdate(updMessage);
				}
			}


			@Override
			public void onReconnected() {
				for(RapidCollectionReference rapidCollectionReference : mCollectionProvider.getCollections().values()) {
					if(rapidCollectionReference.isSubscribed()) {
						rapidCollectionReference.resubscribe();
					}
				}
			}
		});
	}


	public static Rapid getInstance(String apiKey) {
		if(!sInstances.containsKey(apiKey))
			throw new IllegalStateException("Rapid SDK not initialized. Please call Rapid.initialize(apiKey) first.");
		return sInstances.get(apiKey);
	}


	public static Rapid getInstance() {
		if(sInstances.isEmpty())
			throw new IllegalStateException("Rapid SDK not initialized. Please call Rapid.initialize(apiKey) first.");
		else if(sInstances.size() > 1) {
			throw new IllegalStateException("Multiple Rapid instances initialized. Please use Rapid.getInstance(apiKey) to select the one you need.");
		} else {
			return getInstance(sInstances.keySet().iterator().next());
		}
	}


	public static void initialize(Application context, String apiKey) {
		if(!sInstances.containsKey(apiKey))
			sInstances.put(apiKey, new Rapid(context, apiKey));
	}


	public <T> RapidCollectionReference<T> collection(String collectionName, Class<T> itemClass) {
		return mCollectionProvider.provideCollection(this, collectionName, itemClass);
	}


	public RapidJsonConverter getJsonConverter() {
		return mJsonConverter;
	}


	public void setJsonConverter(RapidJsonConverter jsonConverter) {
		mJsonConverter = jsonConverter;
	}


	public String getApiKey() {
		return mApiKey;
	}


	public void addConnectionStateListener(RapidConnectionStateListener listener) {
		mRapidConnection.addConnectionStateListener(listener);
	}


	public void removeConnectionStateListener(RapidConnectionStateListener listener) {
		mRapidConnection.removeConnectionStateListener(listener);
	}


	public void removeAllConnectionStateListeners() {
		mRapidConnection.removeAllConnectionStateListeners();
	}


	public ConnectionState getConnectionState() {
		return mRapidConnection.getConnectionState();
	}


	void onSubscribe(Subscription subscription) {
		mRapidConnection.onSubscribe();
	}


	void onUnsubscribe(Subscription subscription) {
		// find out if this was the last subscription
		boolean stillSomeSubscription = false;
		for(RapidCollectionReference rapidCollectionReference : mCollectionProvider.getCollections().values()) {
			if(rapidCollectionReference.isSubscribed()) {
				stillSomeSubscription = true;
				break;
			}
		}
		mRapidConnection.onUnsubscribe(!stillSomeSubscription);
	}


	Handler getHandler() {
		return mHandler;
	}


	MessageFuture sendMessage(MessageBase message) {
		return mRapidConnection.sendMessage(message);
	}
}
