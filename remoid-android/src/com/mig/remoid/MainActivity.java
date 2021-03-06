package com.mig.remoid;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;
import com.miw.remoid.OperationMapper;
import com.miw.remoid.PhoneDimension;
import com.miw.remoid.WSRequest;

// mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system
// http://forums.xamarin.com/discussion/689/app-in-rom-system-app-fail-to-start

//- su
//- mount -o remount rw /system
//- cp /sdcard/libmonodroid.so /system/lib
//- cp /sdcard/test.apk /system/app
//- chmod 644 /system/lib/libmonodroid.so 
//- chmod 644 /system/app/test.apk 
//- pm install /system/app/test.apk 
//- mount -o remount ro /system
//- reboot

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Create a TextView and set its content. the text is retrieved by
		 * calling a native function.
		 */

		RelativeLayout layout = new RelativeLayout(this);

		Button button = new Button(this);
		button.setText("Connect via websocket");
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					connectJZ();
				} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
					Log.e("jzjz", "error connecting to ws", e);
				}
			}
		});

		layout.addView(button);
		setContentView(layout);

		// if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
		// Log.i("jzjz", "landscape");
		// x = 0;
		// y = displayHeight / 2;
		// } else {
		// x = displayWidth / 2;
		// y = 200;
		// }
		//
		// touchDown();
		//
		// while (true) {
		// if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
		// if (x == displayHeight - 1) {
		// x = 200;
		// }
		//
		// touchSetPtr(x++, y);
		// } else {
		// if (y == displayHeight - 1) {
		// y = 200;
		// }
		//
		// touchSetPtr(x, y++);
		// }
		//
		// }

	}

	public int getScreenOrientation() {
		Display getOrient = getWindowManager().getDefaultDisplay();
		int orientation = Configuration.ORIENTATION_UNDEFINED;
		if (getOrient.getWidth() == getOrient.getHeight()) {
			orientation = Configuration.ORIENTATION_SQUARE;
		} else {
			if (getOrient.getWidth() < getOrient.getHeight()) {
				orientation = Configuration.ORIENTATION_PORTRAIT;
			} else {
				orientation = Configuration.ORIENTATION_LANDSCAPE;
			}
		}
		return orientation;
	}

	private double getDiagonalInches() {
		WindowManager w = getWindowManager();
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		// since SDK_INT = 1;
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
			try {
				widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
			} catch (Exception ignored) {
			}
		}
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17) {
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
				widthPixels = realSize.x;
				heightPixels = realSize.y;
			} catch (Exception ignored) {
			}
		}

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double x = Math.pow(widthPixels / dm.xdpi, 2);
		double y = Math.pow(heightPixels / dm.ydpi, 2);
		return Math.sqrt(x + y);
	}

	public void connectJZ() throws JsonProcessingException, InterruptedException, ExecutionException {
		DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
		final int displayWidth = metrics.widthPixels;
		final int displayHeight = metrics.heightPixels;

		Log.d("jzjz", "openInput: " + openInputDevice(displayWidth, displayHeight));

		String phoneProtocol = Utils.OBJECT_MAPPER.writeValueAsString(PhoneDimension.instantiate(displayWidth, displayHeight, getDiagonalInches()));

		Future<WebSocket> ws = AsyncHttpClient.getDefaultInstance().websocket("ws://192.168.1.101:8080/remoid/update", phoneProtocol, new WebSocketConnectCallback() {
			@Override
			public void onCompleted(Exception ex, WebSocket webSocket) {
				if (ex != null) {
					ex.printStackTrace();
					return;
				}

				webSocket.setStringCallback(new StringCallback() {
					public void onStringAvailable(String str) {
						WSRequest req = null;
						try {
							req = Utils.OBJECT_MAPPER.readValue(str, WSRequest.class);
						} catch (IOException e) {
						}

						if (req.getOp() == OperationMapper.TOUCH_DOWN) {
							touchDown();
						}

						if (req.getOp() == OperationMapper.MOVE) {
							touchSetPtr(req.getX(), req.getY());
						}

						if (req.getOp() == OperationMapper.TOUCH_UP) {
							touchUp();
						}
					}
				});
				webSocket.setDataCallback(new DataCallback() {
					public void onDataAvailable(ByteBufferList byteBufferList) {
						System.out.println("I got some bytes!");
						// note that this data has been read
						byteBufferList.recycle();
					}

					@Override
					public void onDataAvailable(DataEmitter arg0, ByteBufferList arg1) {

					}
				});
			}
		});
	}

	public static boolean execAsRoot(String cmd) {

		if (cmd == null || cmd.equals(""))
			throw new IllegalArgumentException();

		boolean retval = false;

		try {
			Process suProcess = Runtime.getRuntime().exec("su");

			DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());

			os.writeBytes(cmd + "\n");
			os.flush();

			// String out = is.readLine();
			// System.out.println(out);

			os.writeBytes("exit\n");
			os.flush();

			try {
				int suProcessRetval = suProcess.waitFor();
				if (255 != suProcessRetval) {
					// Root access granted
					retval = true;
				} else {
					// Root access denied
					retval = false;
				}
			} catch (Exception ex) {
				Log.e("Error executing root action", ex.toString());
			}

		} catch (IOException ex) {
			Log.w("ROOT", "Can't get root access", ex);
		} catch (SecurityException ex) {
			Log.w("ROOT", "Can't get root access", ex);
		} catch (Exception ex) {
			Log.w("ROOT", "Error executing internal operation", ex);
		}

		return retval;
	}

	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
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
