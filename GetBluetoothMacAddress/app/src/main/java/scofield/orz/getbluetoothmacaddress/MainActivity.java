package scofield.orz.getbluetoothmacaddress;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Method;

import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.IBluetoothManagerCallback;


public class MainActivity extends AppCompatActivity {

    PackageManager pm;
    Boolean hasBluetooth = false;
    Boolean isBluetoothEnabled = false;
    BluetoothAdapter bluetoothAdapter;
    IntentFilter filter = new IntentFilter();
    String sBTMacAddr;
    TextView tv;

    // BroadcastReceiver to receive 'BluetoothAdapter.ACTION_STATE_CHANGED'
    class BluetoothStateChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //final TextView tv = (TextView) findViewById(R.id.sample_text);

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

                if (bluetoothState == BluetoothAdapter.STATE_ON) {
                    Log.e("scofield", "bluetooth is enabled");
                    isBluetoothEnabled = true;
                    getBluetoothMacAddress();
                }
            }
        }
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setTextSize(50.0f);
        tv.setText(stringFromJNI());

        // check if bluetooth hardware feature exists
        pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            hasBluetooth = true;

        if (hasBluetooth) {
            // add 'BluetoothAdapter.ACTION_STATE_CHANGED' to filter
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(new BluetoothStateChangedReceiver(), filter);

            bluetoothAdapter = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled() == false) {
                bluetoothAdapter.enable();
                Log.e("scofield", "enabling bluetooth ...");
            }
            else {
                isBluetoothEnabled = true;
                getBluetoothMacAddress();
            }
        }
    }

    void getBluetoothMacAddress() {
        if (!isBluetoothEnabled) {
            Log.e("scofield", "getBluetoothMacAddress() !enabled");
            return;
        }

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            sBTMacAddr = bluetoothAdapter.getAddress();
            Log.e("scofield", "original bt mac: " + sBTMacAddr);

            tv.setText("Bluetooth MAC address:\n" + sBTMacAddr);

            return;
        }

        try {
            Class<?> classServiceManager = Class.forName("android.os.ServiceManager");
            Method methodGetService = classServiceManager.getDeclaredMethod("getService", String.class);
            methodGetService.setAccessible(true);

            Object object = new Object();
            final IBinder iBinderBTManager = (IBinder) methodGetService.invoke(object, "bluetooth_manager");
            if (iBinderBTManager == null)
                Log.e("scofield", "iBinderBTManager is null");
            else {
                IBluetoothManager iBTManager = IBluetoothManager.Stub.asInterface(iBinderBTManager);

                IBluetoothManagerCallback iBTManagerCallback = new IBluetoothManagerCallback() {
                    @Override
                    public IBinder asBinder() { return iBinderBTManager;    }

                    @Override
                    public void onBluetoothServiceUp(IBluetooth bluetoothService)
                            throws RemoteException {    }

                    @Override
                    public void onBluetoothServiceDown() throws RemoteException {   }

                    @Override
                    public void onBrEdrDown() throws RemoteException {  }
                };

                IBluetooth iBT = iBTManager.registerAdapter(iBTManagerCallback);
                if (iBT == null) {
                    Log.e("scofield", "IBluetooth is null");
                    return;
                }

                String btName = iBTManager.getName();
                sBTMacAddr = iBT.getAddress();
                Log.e("scofield", "bluetooth Name: " + btName + " MacAddress: "+sBTMacAddr);
                iBTManager.unregisterAdapter(iBTManagerCallback);

                tv.setText("Bluetooth MAC address:\n" + sBTMacAddr);
            }
        }
        catch (Exception e) {
            Log.e("scofield", "scofield_exception:"+e+" cause:"+e.getCause());
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
