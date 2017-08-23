// IBluetoothManagerCallback.aidl
package android.bluetooth;


import android.bluetooth.IBluetooth;


interface IBluetoothManagerCallback {
    void onBluetoothServiceUp(IBluetooth bluetoothService);
    void onBluetoothServiceDown();
    void onBrEdrDown();
}
