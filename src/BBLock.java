package com.burningsoda.blackberry.bblock;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.system.Backlight;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;

import java.util.*;
import javax.microedition.global.Formatter;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.FlowFieldManager;

final class RobsKeyListener implements KeyListener, TrackwheelListener {
    private int asteriskTime = -1;

    public boolean keyDown(int keycode, int time) {
        // lock/standby
        /*if (keycode == 268632064 || keycode == 17891328) {
            System.exit(0);
        }*/

        // space == exit
        if (keycode == 2097152) { //net.rim.device.api.ui.Keypad.KEY_SPACE) {
            if (asteriskTime == -1) {
                asteriskTime = time;
                return true;
            } else if (time < asteriskTime + 1000) {
                System.exit(0);
            }
        }

        asteriskTime = -1;
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

    public boolean trackwheelRoll(int amount, int status, int time) {
        return true;
    }
    public boolean trackwheelClick(int status, int time) {
        return true;
    }
    public boolean trackwheelUnclick(int status, int time) {
        return true;
    }
}

public class BBLock extends UiApplication {
    LockScreen screen;

    public static void main(String[] args) {
        // Create a new instance of the application.
        BBLock theApp = new BBLock();

        // Make the currently running thread the application's event
        // dispatch thread and begin processing events.
        theApp.enterEventDispatcher();
    }

    public BBLock() {
        screen = new LockScreen();

        addRealtimeClockListener(screen);
        RobsKeyListener l = new RobsKeyListener();
        addKeyListener(l);
        addTrackwheelListener(l);
        pushScreen(screen);

        //Backlight.enable(false);
    }
}

final class LockScreen
extends MainScreen
implements RealtimeClockListener {

    private String getDateTimeString() {
        Calendar cal = Calendar.getInstance();

        String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(cal.get(Calendar.MINUTE));
        if (hour.length() < 2) {
            hour = "0" + hour;
        }
        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        return hour + ":" + minute;
    }

    public void clockUpdated() {
        setTitle(getDateTimeString());
        invalidate();
    }

    public void paint(Graphics g) {
        int width = 480; // Display.getWidth()
        int height = 360; // Display.getHeight()
        Font font = null;
        try {
            font = FontFamily.forName("BBMillbank").getFont(Font.BOLD, height / 2).derive(Font.BOLD, height / 2, Ui.UNITS_px, Font.ANTIALIAS_SUBPIXEL, 0);
        } catch (Exception e) {
            font = Font.getDefault().derive(Font.BOLD, height / 2);
        }
        g.setBackgroundColor(Graphics.BLACK);
        g.setColor(Graphics.WHITE);
        g.setFont(font);
        g.clear();
        g.drawText(getDateTimeString(), 0, height / 2,
                   DrawStyle.VCENTER | DrawStyle.HCENTER, width);
    }

    public LockScreen() {
        clockUpdated();

        // Add a read only text field (RichTextField) to the screen.  The RichTextField
        // is focusable by default.  In this case we provide a style to make the field
        // non-focusable.
        add(new RichTextField("BBLock", Field.NON_FOCUSABLE));

        Trackball.setSensitivityX(Trackball.SENSITIVITY_OFF);
        Trackball.setSensitivityY(Trackball.SENSITIVITY_OFF);
    }

    public void close() {
        // Display a farewell message before closing application.
        Dialog.alert("Goodbye!");
        super.close();
    }

    protected boolean navigationMovement(int dx, int dy, int status, int time) {
        return true;
    }

    protected boolean navigationClick(int status, int time) {
        return true;
    }

    protected boolean navigationUnclick(int status, int time) {
        return true;
    }
}
