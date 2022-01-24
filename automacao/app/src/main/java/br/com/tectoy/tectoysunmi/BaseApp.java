package br.com.tectoy.tectoysunmi;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sunmi.extprinterservice.ExtPrinterService;

import java.util.HashMap;
import java.util.Map;

import br.com.tectoy.tectoysunmi.activity.MainActivity;
import br.com.tectoy.tectoysunmi.utils.ESCUtil;
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

public class BaseApp extends Application{

    private ExtPrinterService extPrinterService = null;
    public static KTectoySunmiPrinter kPrinterPresenter;
    public static boolean isK1 = false;
    public static boolean isVertical = false;
    int height = 0;
    private static final String LOG_TAG = "USB_TEST_ARDUINO";
    private static final String LOG_Send_Print = "Send_Command_USB_Send";
    private static final String LOG_Send = "Send_Command_USB";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private PendingIntent mPermissionIntent;
    private UsbDevice mDevice;
    private UsbManager usbManager;
    private UsbEndpoint usbEndpointOut = null;
    private UsbEndpoint usbEndpointIn = null;
    private UsbDeviceConnection connection = null;
    @Override
    public void onCreate()  {
        super.onCreate();

        String deviceName = getDeviceName();


        /*
        connect_impressora();

        sendcommadPrint("Teste de Alinhamento\n".getBytes());
        sendcommadPrint("------------------------\n\n".getBytes());
        sendcommadPrint(ESCUtil.feedPaper(2));

        sendcommadPrint(ESCUtil.alignLeft());
        sendcommadPrint("TecToy Automação\n".getBytes());

        sendcommadPrint(ESCUtil.alignCenter());
        sendcommadPrint("TecToy Automação\n".getBytes());

        sendcommadPrint(ESCUtil.alignRight());
        sendcommadPrint("TecToy Automação\n".getBytes());
        sendcommadPrint(ESCUtil.alignLeft());
         */
        if (deviceName.equals("SUNMI K2")){

        }else {
            init();
        }
    }

    /**
     * Connect print servive through interface library
     */
    private void init(){
          TectoySunmiPrint.getInstance().initSunmiPrinterService(this);
    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public boolean isHaveCamera() {
        HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) getSystemService(Activity.USB_SERVICE)).getDeviceList();
        for (Map.Entry entry : deviceHashMap.entrySet()) {
            UsbDevice usbDevice = (UsbDevice) entry.getValue();
            if (!TextUtils.isEmpty(usbDevice.getInterface(0).getName()) && usbDevice.getInterface(0).getName().contains("Orb")) {
                return true;
            }
            if (!TextUtils.isEmpty(usbDevice.getInterface(0).getName()) && usbDevice.getInterface(0).getName().contains("Astra")) {
                return true;
            }
        }
        return false;
    }

    // Impressão Via USB
    public void connect_impressora(){
        for(UsbDevice device : usbManager.getDeviceList().values()) {

            if(device.getProductId() == 4864){
                //Log.d(LOG_TAG, "-----------------Conectando Impressora-------------------------");
                mDevice = device;
                usbManager.requestPermission(mDevice, mPermissionIntent);
                connection = usbManager.openDevice(mDevice);

                UsbInterface mUsbInterface = findHidInterface();
                try {
                    connection.claimInterface(mUsbInterface, true);
                    for (int i = 0; i < mUsbInterface.getEndpointCount(); i++) {
                        UsbEndpoint usbEndpoint = mUsbInterface.getEndpoint(i);

                       // Log.d(LOG_TAG, String.valueOf(usbEndpoint.getType()));
                       // Log.d(LOG_TAG, String.valueOf(usbEndpoint.getDirection()));

                        if (usbEndpoint.getType() == UsbConstants.USB_CLASS_COMM) {
                            if (usbEndpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                                usbEndpointOut = usbEndpoint;
                            } else {
                                usbEndpointIn = usbEndpoint;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private UsbInterface findHidInterface() {
        if (mDevice != null) {
            final int interfaceCount = mDevice.getInterfaceCount();

            for (int interfaceIndex = 0; interfaceIndex < interfaceCount; interfaceIndex++) {
                UsbInterface usbInterface = mDevice.getInterface(interfaceIndex);
                return usbInterface;
            }
            Log.w(LOG_TAG, "HID interface not found.");
        }
        return null;
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(LOG_TAG, action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                          //  Toast.makeText(MainActivity.this, "Dispositivo permitido", Toast.LENGTH_LONG).show();
                        }
                    } else {
                      //  Toast.makeText(MainActivity.this, "Dispositivo não permitido.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    public int sendcommadPrint(byte[] command){

        int ret = connection.bulkTransfer(usbEndpointOut, command, 0, command.length, 1000);
        Log.d(LOG_Send_Print, String.valueOf(ret));
        return ret;
    }
    public void outputCommand(){
        connection.close();
    }

    public static String customData(){

        String pulaLinha = "\n";

        for(int i = 0; i < 10; i++ ){
            pulaLinha += "\n";
            Log.i(LOG_TAG, String.valueOf(i));
        }

        return  pulaLinha;
    }
}
