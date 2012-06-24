package com.burningsoda.blackberry.lockwithaclock;

import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.PhoneListener;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.UiApplication;

public class LockApp extends UiApplication implements SystemListener2 {
    LockScreen screen;
    PhoneListener phoneListener;

    public static void main(String[] args) {
        LockApp theApp = new LockApp();
        theApp.enableLock();
        theApp.enterEventDispatcher();
    }

    public LockApp() {
        addSystemListener(this);
        phoneListener = new OnCallAnsweredQuit(this);
        Phone.addPhoneListener(phoneListener);
    }

    public void quit() {
        invokeLater(new Runnable() {
            public void run() {
                if (phoneListener != null) {
                    Phone.removePhoneListener(phoneListener);
                }

                removeSystemListener(LockApp.this);

                if (screen != null) {
                    removeKeyListener(screen);
                    removeRealtimeClockListener(screen);
                    System.out.println("*** closing screen");
                    screen.stopBacklightTask();
                    screen.close();
                }

                System.out.println("*** exiting");
                System.exit(0);
            }
        });
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
