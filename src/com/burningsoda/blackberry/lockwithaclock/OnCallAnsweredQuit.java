package com.burningsoda.blackberry.lockwithaclock;

import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.ApplicationManager;

public class OnCallAnsweredQuit extends AbstractPhoneListener {
	private int parentPid = -1;
	
	public OnCallAnsweredQuit(int pid) {
		parentPid = pid;
	}
	
	public void callAnswered(int callId) {
		System.out.println("*** callAnswered");
		ApplicationManager.getApplicationManager().postGlobalEvent(parentPid, 0xCAFEBABE, 0, 0, null, null);
		Phone.removePhoneListener(this);
	}
}
