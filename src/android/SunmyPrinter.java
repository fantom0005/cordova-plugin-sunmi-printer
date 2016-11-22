package ru.fantom.sunmi.sunmyprinter;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import  ru.fantom.sunmi.sunmyprinter.IWoyouService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * This class echoes a string called from JavaScript.
 */
public class SunmyPrinter extends CordovaPlugin {


    private static final SunmyPrinter printer = new SunmyPrinter();
	private  IWoyouService woyouService = null;

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
            this.initPrinter();
            this.print(message);
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
	
	private ServiceConnection connService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			woyouService = null;
		}		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			woyouService = IWoyouService.Stub.asInterface(service);
		}
	};

	private SunmyPrinter(){}
	
	
	public static SunmyPrinter getInstance() {
		return printer;
	}
	
	public void initPrinter(Context context){
		Intent intent=new Intent();
		intent.setPackage("ru.fantom.sunmi.sunmyprinter");
		intent.setAction("ru.fantom.sunmi.sunmyprinter.IWoyouService");
		context.startService(intent);
		context.bindService(intent, connService, Context.BIND_AUTO_CREATE);		
	}
	
	public void print(String msg, ICallback callback){
		if(woyouService != null){
			try {
				woyouService.printText(msg, callback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
}
