package ru.fantom.sunmi.sunmyprinter;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import ru.fantom.sunmi.sunmyprinter.ESCUtil;

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

public class SunmyPrinter extends CordovaPlugin {
    
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
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID);
            try{
                socket.connect();
            }catch(IOException e){}
            return socket;
	}

	public static void sendData(byte[] bytes, BluetoothSocket socket) throws IOException {
            OutputStream out = socket.getOutputStream();
            try{
                out.write(bytes, 0, bytes.length);
            }catch(IOException e){ }
            out.close();
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

    private void test(){
        try{
            BluetoothAdapter adapter = this.getBTAdapter();
            BluetoothDevice device = this.getDevice(adapter);
            BluetoothSocket socket = this.getSocket(device);
            byte[] data = ESCUtil.generateMockData();
            this.sendData(data,socket);
        }catch(IOException e){ }
    }

}
