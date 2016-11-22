package ru.fantom.sunmi.sunmyprinter;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import ru.fantom.sunmi.sunmyprinter.ICallback;
import ru.fantom.sunmi.sunmyprinter.IWoyouService;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

/**
 * This class echoes a string called from JavaScript.
 */
public class SunmyPrinter extends CordovaPlugin {

    private static final String TAG = "PrinterTestDemo";
	
	public static final int DO_PRINT = 0x10001;
	
	private IWoyouService woyouService;
	private byte[] inputCommand ;
    
	private final int RUNNABLE_LENGHT = 11;
	
	private Random random = new Random();
	
	private ICallback callback = null;
	
	private ServiceConnection connService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

			woyouService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			woyouService = IWoyouService.Stub.asInterface(service);
			setButtonEnable(true);
		}
	};

	private final int MSG_TEST = 1;
	private long printCount = 0;
	
	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
	    @Override
	    public void handleMessage(Message msg){
	    	if(msg.what == MSG_TEST){
	    		testAll();
	    		long mm = MemInfo.getmem_UNUSED(PrinterTestDemoAct.this);
	    		if( mm < 100){
	    			handler.sendEmptyMessageDelayed(MSG_TEST, 20000);
	    		}else{
	    			handler.sendEmptyMessageDelayed(MSG_TEST, 800);
	    		}
	    		Log.i(TAG,"testAll: " + printCount + " Memory: " + mm);
	    	}
	    }
	};
	
	private void test(){
		ThreadPoolManager.getInstance().executeTask(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					woyouService.printerSelfChecking(null);
					woyouService.printText(" printed: " + printCount + " bills.\n\n\n\n", null);
					printCount++;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});
	}

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }    
}
