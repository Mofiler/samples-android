# Mofiler SDK for Android:

Check the HelloMofiler project to rapidly see how to use the Mofiler SDK.


## Quick stuff to get you going

1) Include the mofiler-sdk library:
```
	// In your app's Gradle config file:
	dependencies {
   		compile 'com.mofiler.android:mofiler-sdk:${mofiler.version}'
   		//... other dependencies
	}
```
NOTE: mofiler-sdk includes `com.google.android.gms:play-services-ads:9.0.2`. If you want to exclude that dependency and replace it with yours use:

```
 	compile ('com.mofiler.android:mofiler-sdk:${mofiler.version}') {
		exclude group: 'com.google.android.gms', module: 'play-services-ads'
 	}
```


2) Add these permissions to your project AndroidManifest.xml file:
```
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
If you are going to use the Verbose Extras mode, you also should add these to your manifest:
```
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <!-- OPTIONAL --->
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>    
```

3) And inside your application tag, add these:        
```
	<!-- MOFILER SERVICES -->
	<service android:name="com.mofiler.service.AlarmService" />
	
	<!-- MOFILER RECEIVERS -->
        <receiver
		android:name="com.mofiler.receivers.AlarmNotificationReceiver"
		android:enabled="true" />
	<receiver
		android:name="com.mofiler.receivers.OnBootReceiver"
		android:enabled="true" >
		<intent-filter>
			<action android:name="android.intent.action.BOOT_COMPLETED" >
			</action>
		</intent-filter>
	</receiver>
	
        <receiver
            android:name="com.mofiler.receivers.PackageNotificationReceiver"
            android:enabled="true"
            android:exported="true" >
            <intent-filter>
                <action android:name="android.intent.action.PACKAGE_REMOVED" />
                <action android:name="android.intent.action.PACKAGE_ADDED" />
                <action android:name="android.intent.action.PACKAGE_CHANGED" />
                <action android:name="android.intent.action.PACKAGE_REPLACED" />
                <action android:name="android.intent.action.PACKAGE_FULLY_REMOVED" />
                <data android:scheme="package"/>
            </intent-filter>
        </receiver>	
	
```
Verbose Extras mode tracks the user activity not only within your application but within the context of the device.

4) If you are not already using an Application class for your app, please create a new class that extends Application like so:
```
	public class HelloMofiler extends Application {
		@Override
		public void onCreate() {
			super.onCreate();
		}
	}
```
We'll add some initialization code to the overriden `onCreate()` method.

### Initialization

In order to set up Mofiler the proper way, please place the following calls withih your Application class' `onCreate()` method:
```
	public class HelloMofiler extends Application {

		@Override
		public void onCreate() {
			super.onCreate();

			// if you have an identity you can set up up front, use initializeWith (there's no need to call setAppKey and setAppName separately)
			mof.initializeWith("MY-APPKEY-HERE-ANDROID", "MyAndroidTestApplication");

			// if you don't have a logged in user yet, you can call setAppKey, setAppName and then only
			// call addIdentity once you know who the logged in user is
			mof.setAppKey("MY-APPKEY-HERE-ANDROID");
			mof.setAppName("MyAndroidTestApplication");
			mof.addIdentity("username", "johndoe");
			mof.addIdentity("email", "john@doe.com");
			mof.addIdentity("id", "265553333");
			// add other identities for this logged in user as you see fit. 
			// The more identities you add, the better then platform can match your users different preferences.

			mof.setUseVerboseContext(true); //defaults to false, but helps Mofiler get a lot of information about the device context if set to true
			mof.setUseLocation(true); //defaults to true
		}

	}
```

- `setURL()` is needed in order to set the server endpoint
- `initializeWith()` is the de-facto call you would make if you knew your user up front. Alternatively, you can make `setAppKey` and `setAppName` calls separately, plus adding at least 1 identity:
- `setAppName()` takes a String with your application name
- `setAppKey()` takes a String with your uniquely assigned App Key (mofiler.com identifies your app with this)
- `setUseVerboseContext()` defaults to false, but helps Mofiler get a lot of information about the device context if set to true
- `setUseLocation()` brings more context to each value injection (when and where did the user do this?)

A special note on `addIdentity()`: `addIdentity()` accepts a key (identity type) and a value that uniquely identifies the current user logged in to your system. It is important to note that you can add as many identities as ways to identify this user you have. It may be an email, a username, an id, the advertising id or whatever other means you have to identify this particular user.

Please also bear in mind that once a user logs out from your app (if that is possible at all - as it depends on your own business logic), you need to reset these values. The most common way to do this is to just re-init Mofiler every time a new user logs back in.

For example, we could have these generics in one method
```
	public void initMofiler() {

		/* mofiler initialisation code */
		Mofiler mof = Mofiler.getInstance(this);
		mof.setURL("mofiler.com");


		mof.setAppKey("MY-APPKEY-HERE-ANDROID");
		mof.setAppName("MyAndroidTestApplication");
		mof.setUseVerboseContext(true); //defaults to false, but helps getting usefull information about the device context 
		mof.setUseLocation(true); //defaults to true

	}
```

Then call it in `onCreate()`:
```
	public class HelloMofiler extends Application {

		@Override
		public void onCreate() {
			super.onCreate();
			initMofiler();
		}
	}
```
and every time a user logs back in to your app:
```
	public void login() {
		[...]
		initMofiler();

		Mofiler mof = Mofiler.getInstance(this);
		mof.addIdentity("username", "johndoe");
		mof.addIdentity("email", "john@doe.com");
		mof.addIdentity("id", "265553333");
		// add other identities for this logged in user as you see fit. 
		// The more identities you add, the better then platform can match your users different preferences.
	}
```

Also, for the sake of keeping track of all the last user did, please remember calling `flushDataToMofiler()` upon user logout:

```
	public void logout() {
		[...]
		Mofiler mof = Mofiler.getInstance(this);
		mof.flushDataToMofiler();
	}
```

### Session handling

The SDK starts tracking a session any time the user brings your app to the foreground, unless it's been in the background for less than 30 seconds. While this number is arbitrary, this is kind of the window that the SDK uses to determine the user actively decided to switch apps or is just handling some random interruption. 

If you're targetting API level 14 (Ice Cream Sandwich) or above, sessions are handled automatically. If you're targetting API level 13 or below, please do the following:

1. On each Activity (or in your Activity super class if your application is architectured like so), override the `onStart()` and `onStop()` methods and place a call to `onStartSession()` and `onEndSession` within them:
```
	public class MyActivity extends Activity {

		@Override
		protected void onStart() {
			super.onStart();
			Mofiler.getInstance(this).onStartSession(this);
		}

		@Override
		protected void onStop() {
			super.onStop();
			Mofiler.getInstance(this).onEndSession(this);
		}
		[...]
	}
```
### Inject values to Mofiler:
```
	mof.injectValue("mykey", "myvalue");
	mof.injectValue("mykey2", "myvalue2", System.currentTimeMillis() + (1000*60*60*24));
```

Mofiler uses an internal stack and persistence in order to collect data from your application before attempting to send it over to the server, thus
ensuring internet usage and user experience is taken care of.
Should you want to send data over to Mofiler server right away, you just need to perform the following call:
```
	mof.flushDataToMofiler();
```

### Get values from Mofiler:

In newer version of the SDK (since we moved to using Volley library) we no longer have a unique listener that is set upon initialization, but rather you pass 
a listener (in this example, anonymous) within the call to `getValue()`:
```
	mof.getValue("mykey0", "username", "johndoe", new ApiListener() {
		@Override
		public void onResponse(int reqCode, JSONObject response) {
			Log.d(MainActivity.class.getName(), response.toString());
		}

		@Override
		public void onError(int reqCode, JSONObject originalPayload, VolleyError error) {
			Log.d(MainActivity.class.getName(), error.getMessage());
		}
	});
```

- usage: key to retrieve; identifier key to use, identifier key value to use.


### Listeners

**DEPRECATED:** ~~If you set a listener, it will have to implement the interface ApiListener and the following abstract method
		methodResponded(String a_methodCalled, Vector a_vectBusinessObject)

This will always be called either in success or if an error occurred. For your ease, you can always rely that
if an error occured you will get a JSONObject with the "error" within.
Otherwise, you get the "result" key.
In all cases, this is true even if the mofiler SDK could not connect to the server, so this way you can handle errors
in the same way.~~

**UPDATE**:
In newer version of the SDK (since we moved to using Volley library) we no longer have a unique listener that is set upon initialization, but rather you pass 
a listener (in this example, anonymous) within the call to `getValue()`:
```
	mof.getValue("mykey0", "username", "johndoe", new ApiListener() {
		@Override
		public void onResponse(int reqCode, JSONObject response) {
			Log.d(MainActivity.class.getName(), response.toString());
		}

		@Override
		public void onError(int reqCode, JSONObject originalPayload, VolleyError error) {
			Log.d(MainActivity.class.getName(), error.getMessage());
		}
	});
```
The original payload is sent as a JSONObject in the `onError()` callback method. You would generally not need this for a call to `getValue()`, but it is present there for consistency with the legacy API where we need to keep track of any failed request attempts (specially useful for POSTs).


