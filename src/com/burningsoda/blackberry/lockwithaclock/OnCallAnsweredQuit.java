package com.burningsoda.blackberry.lockwithaclock;

import net.rim.blackberry.api.phone.AbstractPhoneListener;

public class OnCallAnsweredQuit extends AbstractPhoneListener {
    private LockApp lockApp;
	
	public OnCallAnsweredQuit(LockApp app) {
		lockApp = app;
	}
	
	public void callAnswered(int callId) {
        lockApp.quit();
	}
}
