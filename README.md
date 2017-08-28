# AndroidGetBluetoothMacAddress

<Android device identification – get Wifi/Bluetooth MAC address>

* Android has added read MAC address protection, LOCAL_MAC_ADDRESS, since Android M 6.0. 
So if you call Wifi/Bluetooth API to get their MAC addresses, you will get the default MAC address, 
DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00".

* Wifi API to get Wifi MAC address
https://developer.android.com/reference/android/net/wifi/WifiInfo.html#getMacAddress()
String getMacAddress ()

* Bluetooth API to get Bluetooth MAC address
https://developer.android.com/reference/android/bluetooth/BluetoothAdapter.html#getAddress()
String getAddress ()
Returns the hardware address of the local Bluetooth adapter.

* framework/base/api/system-current.txt
field public static final java.lang.String LOCAL_MAC_ADDRESS = "android.permission.LOCAL_MAC_ADDRESS";

* Solution: 
https://stackoverflow.com/questions/33377982/get-bluetooth-local-mac-address-in-marshmallow

* To get Wifi MAC address, you can use the Java NetworkInterface API
https://docs.oracle.com/javase/7/docs/api/java/net/NetworkInterface.html
byte[]	getHardwareAddress()
Returns the hardware address (usually MAC) of the interface if it has one and if it can be accessed given the current privileges.

* To get Bluetooth MAC address, you can use a reflection library, net.vidageek (I have not tried it, so not sure if it works)
http://projetos.vidageek.net/mirror/mirror/
https://github.com/vidageek/mirror/

* I have another method to get Bluetooth MAC address, by using reflection, Android AIDL (IBluetooth.aidl, IBluetoothManager.aidl, IBluetoothManagerCallback.aidl) to access Android Bluetooth service and then get the MAC address.

https://developer.android.com/guide/components/aidl.html

https://docs.oracle.com/javase/7/docs/api/java/lang/Class.html
static Class<?>	forName(String className)
Returns the Class object associated with the class or interface with the given string name.

Method	getDeclaredMethod(String name, Class<?>... parameterTypes)
Returns a Method object that reflects the specified declared method of the class or interface represented by this Class object.

Class<?> classServiceManager = Class.forName("android.os.ServiceManager");
Method methodGetService = classServiceManager.getDeclaredMethod("getService", String.class);
methodGetService.setAccessible(true);
Get the Android “ServiceManager” and then get its member function “getService()”

Object object = new Object();
final IBinder iBinderBTManager = (IBinder) methodGetService.invoke(object, "bluetooth_manager");
Use the Android ServiceManager’s getService() with reflection method to get the Android Bluetooth manager “bluetooth_manger”

IBluetoothManager iBTManager = IBluetoothManager.Stub.asInterface(iBinderBTManager);

IBluetoothManagerCallback iBTManagerCallback = new IBluetoothManagerCallback()

IBluetooth iBT = iBTManager.registerAdapter(iBTManagerCallback);
iBT.getAddress()

Use registerAdapter() to get IBluetooth instance, then invoke getAddress() of IBluetooth to get the Bluetooth MAC address.


