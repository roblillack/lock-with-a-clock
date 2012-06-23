package com.burningsoda.blackberry.lockwithaclock;

import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.PhoneListener;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
//import net.rim.device.api.ui.decor.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import java.util.*;

public class LockApp extends UiApplication implements SystemListener2, GlobalEventListener {
    LockScreen screen;

    public static void main(String[] args) {
        LockApp theApp = new LockApp();
        theApp.enableLock();
        theApp.enterEventDispatcher();
    }

    public LockApp() {
        addSystemListener(this);
        addGlobalEventListener(this);
        Phone.addPhoneListener(new OnCallAnsweredQuit(this.getProcessId()));
    }
    
    public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
    	System.out.println("*** EVENT ***");
        if (guid == 0xCAFEBABE) {
        	invokeLater(new Runnable() {
            	public void run() {
                	quit();
            	}
            });
        }
    }
    
    public void quit() {
    	if (screen != null) {
    		System.out.println("*** closing screen");
    		screen.close();
    	}
    	System.out.println("*** exiting");
    	System.exit(0);
    }
    
    public void enableLock() {
    	if (screen == null) {
    		screen = new LockScreen(this);
    	}
        addRealtimeClockListener(screen);
        addKeyListener(screen);
        invokeLater(new Runnable() {
        	public void run() {
        		screen.show();
        	}
        });
    }
    
    /* Ignores Green & Red key, when backlight is off. */
    public void deactivate() {
    	System.out.println("DEACTIVATE");
        requestForeground();
    }

    public void activate() {
    	System.out.println("ACTIVATE");
        //requestForeground();
    }
    
    private int phoneProcessId = -1;
    public void foregroundMe() {
    	if (this.isForeground()) {
    		return;
    	}

    	System.out.println("IM NOT FOREGROUND");
    	
    	ApplicationManager appMgr = ApplicationManager.getApplicationManager();
    	if (phoneProcessId == -1) {
	        ApplicationDescriptor[] descriptors = appMgr.getVisibleApplications();
	        for (int i = 0; i < descriptors.length; i++) {
	        	if (descriptors[i].getName().equals("Phone")) {
	        		phoneProcessId = appMgr.getProcessId(descriptors[i]);
	        		break;
	        	}
	        }
    	}
    	
        if (appMgr.getForegroundProcessId() != phoneProcessId) {
        	System.out.println("NEITHER IS THE PHONE APP");
        	requestForeground();
        }
    }
    
    private void startForegroundChecker() {
    	new Thread(new Runnable() {
    		public void run() {
    			for (;;) {
    		    	ApplicationManager appMgr = ApplicationManager.getApplicationManager();
    		        ApplicationDescriptor[] descriptors = appMgr.getVisibleApplications();
    		        for (int i = 0; i < descriptors.length; i++) {
    		        	if (appMgr.getProcessId(descriptors[i]) == appMgr.getForegroundProcessId()) {
    		        		System.out.print("*");
    		        	}
    		        	System.out.print(descriptors[i].getName() + ", ");
    		    	}
    		        System.out.println();
    				try {
    					Thread.sleep(500);
    				} catch (Exception e) {}
    			}
    		}
    	}).start();
    }

    /** SystemListener2 *******************************************************/
    public void batteryGood() {}
    public void batteryLow() {}
    public void batteryStatusChange(int status) {}
    public void powerOff() {
    	System.out.println("POWER-OFF");
    }

    public void powerUp() {
    	System.out.println("POWER-UP");
        //requestForeground();
    	//startForegroundChecker();
    }

    public void backlightStateChange(boolean on) {
    	System.out.println("BACKLIGHT: " + (on ? "ON" : "OFF"));

    	// falls nicht gerade die phone app oder so angezeigt wird,
    	// sagen wir dem screen, dass das timeout von vorn beginnen soll
        if (on) {
        	screen.onBacklightEnabled();
        }

        this.requestForeground();
    }
    public void cradleMismatch(boolean mismatch) {}
    public void fastReset() {}
    public void powerOffRequested(int reason) {}
    public void usbConnectionStateChange(int state) {}
    /**************************************************************************/

}
