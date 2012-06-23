package com.burningsoda.blackberry.lockwithaclock;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.MainScreen;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

final class LockScreen
extends MainScreen
implements RealtimeClockListener, KeyListener {

    public void clockUpdated() {
        invalidate();
    }
    
    private LockApp app = null;
    private int displayWidth = -1;
    private int displayHeight = -1;
    private Font clockFont = null;
    private Font unlockFont = null;
    
    private TimerTask displayOffTask = null;
    private Timer displayOffTimer = null;
    private boolean isExposed = false;
    
    private static final int backlightTimeout = 3500;
    private static final int keypressTimeout = 500;
    
    private boolean nextClickUnlocks = false;
    
    private DateFormat timeFormat;
    private DateFormat dateFormat;
    private int maxBrightness;
    
    public void show() {
    	//clockUpdated();
    	reScheduleBacklight();
    	//Ui.getUiEngine().pushGlobalScreen(this, Integer.MAX_VALUE, UiEngine.GLOBAL_MODAL);
    	Ui.getUiEngine().pushScreen(this);
    }
    
    public void paint(Graphics g) {
        if (displayWidth == -1) {
        	displayWidth = Display.getWidth();
        }
        if (displayHeight == -1) {
        	displayHeight = Display.getHeight();
        }
        if (unlockFont == null) {
            try {
                unlockFont = FontFamily.forName("BBMillbankTall").getFont(Font.BOLD, 18);
            } catch (Exception e) {
                unlockFont = Font.getDefault().derive(Font.BOLD, 18);
            }
        }
        if (clockFont == null) {
            int size = displayWidth >= 480 ? (displayWidth >= 640 ? 200 : 160) : 96;
        	try {
        	    clockFont = FontFamily.forName("BBMillbankTall").getFont(FontFamily.SCALABLE_FONT, size).derive(Font.BOLD);
        	} catch(Exception e) {
        		clockFont = unlockFont.derive(Font.BOLD, size, Ui.UNITS_px, Font.ANTIALIAS_SUBPIXEL, 0);
        	}
        }

        if (timeFormat == null) {
        	timeFormat = DateFormat.getInstance(DateFormat.TIME_DEFAULT);
    	}
    	if (dateFormat == null) {
    		dateFormat = DateFormat.getInstance(DateFormat.DATE_FULL);
    	}
    	
    	long now = new Date().getTime();

        g.setBackgroundColor(Color.BLACK);
        g.clear();
        
        g.setColor(Color.WHITE);
        g.setFont(clockFont);
        g.drawText(timeFormat.formatLocal(now), 0, displayHeight / 2,
                   DrawStyle.VCENTER | DrawStyle.HCENTER, displayWidth);

        g.setFont(unlockFont);

        g.setColor(Color.DIMGRAY);
        g.drawText(dateFormat.formatLocal(now), 0, 3, DrawStyle.TOP | DrawStyle.RIGHT, displayWidth - 6);
        	
        g.drawText(nextClickUnlocks ?
        		   "Click Trackpad again to unlock." :
        		   "Double-click Trackpad to unlock.", 0, displayHeight / 2 + displayHeight / 4,
                   DrawStyle.TOP | DrawStyle.HCENTER, displayWidth);
    }

    public LockScreen(LockApp application) {
    	app = application;
    	maxBrightness = Backlight.getBrightness();
    	//Backlight.setBrightness(25);
    }
    
    public void onBacklightEnabled() {
    	System.out.println("*** Backlight enabled");
    	if (isExposed) {
    		reScheduleBacklight();
    	} else {
    		System.out.println("*** i dont have focus, skipping rescheduling of backlight.");
    	}
    }

    public void onExposed() {
    	System.out.println("EXPOSED");
    	reScheduleBacklight();
    	isExposed = true;
    }
    
    public void onObscured() {
    	System.out.println("OBSCURED");
    	isExposed = false;
    	stopBacklightTask();
    }
    
    public void onFocusNotify(boolean focus) {
    	System.out.println("FOCUS: " + (focus ? "GAINED" : "LOST"));
    	
    	isExposed = focus;
    	if (focus) {
    		reScheduleBacklight();
    	} else {
    	    stopBacklightTask();
    	}
    }
    
    public void stopBacklightTask() {
    	System.out.println("*** stopping background task");
    	Backlight.setBrightness(maxBrightness);
    	if (displayOffTask != null) {
    		displayOffTask.cancel();
    		displayOffTask = null;
    	}    	
    }

    public void reScheduleBacklight() {
    	System.out.println("RESCHEDULEBACKLIGHT");

    	if (displayOffTask != null) {
    		displayOffTask.cancel();
    	}
    	
   		displayOffTask = new TimerTask() {
   			public void run() {
   				//Backlight.setBrightness(25);
   				Backlight.enable(false);
   			}
   		};
    	
    	if (displayOffTimer == null) {
    		displayOffTimer = new Timer();
    	}
        displayOffTimer.schedule(displayOffTask, backlightTimeout);
    }
    
    /*public void close() {
        // Display a farewell message before closing application.
        Dialog.alert("Goodbye!");
        super.close();
    }*/

    protected boolean navigationMovement(int dx, int dy, int status, int time) {
        return true;
    }

    protected boolean navigationClick(int status, int time) {
    	//Backlight.setBrightness(maxBrightness);
    	reScheduleBacklight();
    	
    	if (nextClickUnlocks) {
    		app.quit();
    		return true;
    	}
    	
        nextClickUnlocks = true;
        invalidate();
        TimerTask task = new TimerTask() {
        	public void run() {
        		LockScreen.this.nextClickUnlocks = false;
        		LockScreen.this.invalidate();
       		}
       	};
       	new Timer().schedule(task, keypressTimeout);
        return true;
    }

    protected boolean navigationUnclick(int status, int time) {
    	System.out.println("*** Navigation unclick");
        return true;
    }

    protected boolean trackwheelClick(int status, int time) {
        return true;
    }

    protected boolean trackwheelRoll(int amount, int status, int time) {
        return true;
    }

    protected boolean trackwheelUnclick(int status, int time) {
        return true;
    }

    public boolean keyDown(int keycode, int time) {
    	nextClickUnlocks = false;
    	System.out.println("*** keypress!");
		invalidate();
    	return true;
    }

    public boolean keyChar( char key, int status, int time) {
        return true;
    }
    public boolean keyRepeat(int keycode, int time) {
        return true;
    }
    public boolean keyStatus(int keycode, int time) {
        return true;
    }
    public boolean keyUp(int keycode, int time) {
		return true;
    }
}