package scofield.orz.getbluetoothmacaddress;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    PackageManager pm;
    Boolean hasBluetooth = false;
    Boolean isBluetoothEnabled = false;
    BluetoothAdapter bluetoothAdapter;
    IntentFilter filter = new IntentFilter();

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
                    //getBluetoothMacAddress();
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
                //getBluetoothMacAddress();
            }
        }

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
