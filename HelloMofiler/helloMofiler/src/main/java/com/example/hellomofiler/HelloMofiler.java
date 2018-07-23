package com.example.hellomofiler;

import android.app.Application;

import com.mofiler.Mofiler;

public class HelloMofiler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /* mofiler initialisation code */
        Mofiler mof = Mofiler.getInstance(this);

        // set the server endpoint
        mof.setURL("mofiler.com/mock");

        // if you have an identity you can set up up front, call initializeWith
        mof.initializeWith("MY-APPKEY-HERE-ANDROID", "MyAndroidTestApplication");

        // if you don't have a logged in user yet, you can call setAppKey, setAppName and then only
        // call addIdentity once you know who the logged in user is
//        mof.setAppKey("MY-APPKEY-HERE-ANDROID");
//        mof.setAppName("MyAndroidTestApplication");

        mof.setUseVerboseContext(true); //defaults to false, but helps Mofiler get a lot of information about the device context if set to true
        mof.setUseLocation(true); //defaults to true
        mof.setReadPhoneState(false);
        mof.setUseIds(true); //defaults to true
        mof.setUseAdvertisingId(true); //defaults to true

        mof.addIdentity("username", "johndoe");
        mof.addIdentity("email", "john@doe.com");
        mof.addIdentity("another", "identity");


    }

}
