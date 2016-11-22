package ru.fantom.sunmi.sunmyprinter;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import ru.fantom.sunmi.sunmyprinter.ICallback;
import ru.fantom.sunmi.sunmyprinter.IWoyouService;

import ru.fantom.sunmi.sunmyprinter.ESCUtil;

// import java.util.Random;

// import java.io.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;


/**
 * This class echoes a string called from JavaScript.
 */
public class SunmyPrinter extends CordovaPlugin {

    // private static final String TAG = "PrinterTestDemo";
	
	// public static final int DO_PRINT = 0x10001;
	
	// private IWoyouService woyouService;
	// private byte[] inputCommand ;
    
	// private final int RUNNABLE_LENGHT = 11;
	
	// private Random random = new Random();
	
	// private ICallback callback = null;
	
	// private ServiceConnection connService = new ServiceConnection() {

	// 	@Override
	// 	public void onServiceDisconnected(ComponentName name) {

	// 		woyouService = null;
	// 	}

	// 	@Override
	// 	public void onServiceConnected(ComponentName name, IBinder service) {
	// 		woyouService = IWoyouService.Stub.asInterface(service);
	// 	}
	// };

	// private final int MSG_TEST = 1;
	// private long printCount = 0;
	
	// private void test(){
	// 	ThreadPoolManager.getInstance().executeTask(new Runnable(){

	// 		@Override
	// 		public void run() {
	// 			// TODO Auto-generated method stub
	// 			try {
	// 				woyouService.printerSelfChecking(null);
	// 				woyouService.printText(" printed: " + printCount + " bills.\n\n\n\n", null);
	// 				printCount++;
	// 			} catch (RemoteException e) {
	// 				// TODO Auto-generated catch block
	// 				e.printStackTrace();
	// 			}
	// 		}});
	// }

    
	private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static final String Innerprinter_Address = "00:11:22:33:44:55";

	public static BluetoothAdapter getBTAdapter() {
		return BluetoothAdapter.getDefaultAdapter();
	}

	public static BluetoothDevice getDevice(BluetoothAdapter bluetoothAdapter) {
		BluetoothDevice innerprinter_device = null;
		Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
		for (BluetoothDevice device : devices) {
			if (device.getAddress().equals(Innerprinter_Address)) {
				innerprinter_device = device;
				break;
			}
		}
		return innerprinter_device;
	}

	public static BluetoothSocket getSocket(BluetoothDevice device) throws IOException {
        try{
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID);
            socket.connect();
            return socket;
        }catch(IOException e){
            return null;
        }
	}

	public static void sendData(byte[] bytes, BluetoothSocket socket) throws IOException {
        try{
            OutputStream out = socket.getOutputStream();
            out.write(bytes, 0, bytes.length);
            out.close();
         }catch(IOException e){
            return null;
        }
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
            this.test();    
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }    

    private void test(){
        BluetoothAdapter adapter = this.getBTAdapter();
        BluetoothDevice device = this.getDevice(adapter);
        BluetoothSocket socket = this.getSocket(device);
        byte[] data = ESCUtil.generateMockData();
        this.sendData(data,socket);
    }

}
