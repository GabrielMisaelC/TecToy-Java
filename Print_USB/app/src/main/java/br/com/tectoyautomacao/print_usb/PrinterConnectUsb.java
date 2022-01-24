package br.com.tectoyautomacao.print_usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

public class PrinterConnectUsb implements IPrinter {
    @Override
    public void ValidPermission() {

    }

    @Override
    public void PrinterConnect() {
    }
//    private static Context context;
//    private static PrinterConnectUsb printerConnectUsb = new PrinterConnectUsb();
//
//
//    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
//    private static final String LOG_TAG = MainActivity.class.getName();
//    private IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//
//    public PrinterConnectUsb(Context ctx){
////        context = ctx
//    }
//
//    public PrinterConnectUsb() {
//
//    }
//
//    public static PrinterConnectUsb getInstance() {
//        return printerConnectUsb;
//    }
//
//    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
//
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.d(LOG_TAG, action);
//            if (ACTION_USB_PERMISSION.equals(action)) {
//                synchronized (this) {
//                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        if (device != null) {
//                            Toast.makeText(MainActivity.this, "Dispositivo permitido", Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        Toast.makeText(MainActivity.this, "Dispositivo não permitido.", Toast.LENGTH_LONG).show();
//                        Log.d(TAG, "permission denied for device " + device);
//                    }
//                }
//            }
//        }
//    };
//
//
//    @Override
//    public void ValidPermission() {
//        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        registerReceiver(usbReceiver, filter);
//        usbManager.requestPermission(mDevice, mPermissionIntent);
//    }
//
//    @Override
//    public void PrinterConnect() {
//
//    }
}
