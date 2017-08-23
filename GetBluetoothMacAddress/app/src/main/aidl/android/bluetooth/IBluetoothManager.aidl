// IBluetoothManager.aidl
package android.bluetooth;


import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothManagerCallback;


interface IBluetoothManager {
    IBluetooth registerAdapter(IBluetoothManagerCallback callback);
    void unregisterAdapter(IBluetoothManagerCallback callback);
    String getName();
}
