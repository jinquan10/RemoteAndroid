package com.mig.remoid;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*
         * Create a TextView and set its content. the text is retrieved by
         * calling a native function.
         */
        TextView tv = new TextView(this);
        setContentView(tv);
        
        // Context context = getApplicationContext();
        // CharSequence text = "Hello toast!";
        // int duration = Toast.LENGTH_SHORT;
        //
        // Toast toast = Toast.makeText(context, text, duration);
        // toast.show();
        
        Log.i("jzjz", "root: " + isRootAccessAvailable());
        Log.i("jzjz", "openInput: " + openInputDevice(240, 400));
        touchSetPtr(200, 200);
        touchDown();
    }
    
    public static boolean isRootAccessAvailable() {
        boolean result = false;
        Process suProcess;
        
        try {
            suProcess = Runtime.getRuntime().exec("su");
            
            DataOutputStream outStream = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream inStream = new DataInputStream(suProcess.getInputStream());
            BufferedReader inReader = new BufferedReader(new InputStreamReader(inStream));
            
            if (null != outStream && null != inStream) {
                // Getting the id of the current user to check if this is root
                outStream.writeBytes("id\n");
                outStream.flush();
                
                String currUid = inReader.readLine();
                boolean exitSu = false;
                
                if (currUid == null) {
                    result = false;
                    exitSu = false;
                    Log.d("ROOT", "Can't get root access or denied by user");
                } else if (currUid.contains("uid=0")) {
                    result = true;
                    exitSu = true;
                    Log.d("ROOT", "Root access granted");
                } else {
                    result = false;
                    exitSu = true;
                    Log.d("ROOT", "Root access rejected: " + currUid);
                }
                
                if (exitSu) {
                    outStream.writeBytes("exit\n");
                    outStream.flush();
                }
            }
        } catch (Exception e) {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output
            // stream after su failed, meaning that the device is not rooted
            
            result = false;
            Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Opens uinput(User-level input) device for event injection.
     * 
     * @return true device has opened without error, false otherwise
     */
    private native boolean openInputDevice(final int scrWidth, final int scrHeight);
    
    /**
     * Open input device using suinput, without setting permission 666 to
     * /dev/uinput.<br/>
     * If user has su binary that doesn't supports 'su -c' option, which enables
     * running shell command with root permission, Change permission through
     * org.secmem.remoteroid.util.ComandLine.execAsRoot() first, then use this
     * command to open device.
     * 
     * @return true device has opened without error, false otherwise
     */
    public native boolean openInputDeviceWithoutPermission();
    
    /**
     * Closes uinput device.
     */
    private native void closeInputDevice();
    
    /**
     * Close input device, without reverting back /dev/uinput's permission to
     * 660.
     */
    public native void closeInputDeviceWithoutRevertPermission();
    
    /**
     * Injects keyDown event.
     * 
     * @param keyCode
     *            a KeyCode of KeyEvent
     * @see org.secmem.remoteroid.data.NativeKeyCode NativeKeyCode
     */
    public native void keyDown(int keyCode);
    
    /**
     * Injects keyUp event.
     * 
     * @param keyCode
     *            a KeyCode of KeyEvent
     * @see org.secmem.remoteroid.data.NativeKeyCode NativeKeyCode
     */
    public native void keyUp(int keyCode);
    
    /**
     * Injects key stroke (keyDown and keyUp) event.
     * 
     * @param keyCode
     *            a KeyCode of KeyEvent
     * @see org.secmem.remoteroid.data.NativeKeyCode NativeKeyCode
     */
    public native void keyStroke(int keyCode);
    
    /**
     * Injects touch down (user touched screen) event.<br/>
     * This event just represents <b>'touching a screen'</b> event. Setting
     * touch screen's coordinate is processed on touchSetPtr(int, int) method.
     * 
     * @see #touchSetPtr(int, int)
     */
    public synchronized native void touchDown();
    
    /**
     * Injects touch up (user removed finger from a screen) event.
     */
    public synchronized native void touchUp();
    
    /**
     * Set coordinates where user has touched on the screen.<br/>
     * When user touches the screen, this method called first to set where user
     * has touched, then {@link #touchDown()} called to notify user has touched
     * screen.
     * 
     * @param x
     *            x coordinate that user has touched
     * @param y
     *            y coordinate that user has touched
     */
    public synchronized native void touchSetPtr(int x, int y);
    
    /**
     * Injects 'touch once' event, touching specific coordinate once.<br/>
     * This method calls {@link #touchSetPtr(int, int)}, {@link #touchDown()},
     * and {@link #touchUp()} in sequence.
     * 
     * @param x
     *            x coordinate that user has touched
     * @param y
     *            y coordinate that user has touched
     */
    public void touchOnce(int x, int y) {
        touchSetPtr(x, y);
        touchDown();
        touchUp();
    }
    
    static {
        System.loadLibrary("remoid");
    }
}