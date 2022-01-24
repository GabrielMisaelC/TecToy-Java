package br.com.tectoyautomacao.print_usb;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.LongFunction;

public class MainActivity extends AppCompatActivity {


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        // Faz a validação de Permissão para acessar a porta USB

        mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

        connect_impressora();

        findViewById(R.id.btnAvancaLinhas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//
////                sendcommadPrint("------------------------\n\n".getBytes());
////                sendcommadPrint("Print data in Page mode\n".getBytes());
////                sendcommadPrint("TecToy Automação\n".getBytes());
//                sendcommadPrint(ESCUtil.init_printer());
//                sendcommadPrint(ESCUtil.printDataInPageMode(1));
////                sendcommadPrint(ESCUtil.feedPaper(2));
//
//
//                try {
//                    Charset charset = Charset.forName("ISO-8859-1");
//                    CharsetEncoder encoder = charset.newEncoder();
//                    ByteBuffer bbuf = encoder.encode(CharBuffer.wrap("TecToy Automação"));
//
//                    sendcommadPrint("ISO-8859-1\n".getBytes());
//                    sendcommadPrint("------------------------\n\n".getBytes());
//                    byte[] arr = new byte[bbuf.remaining()];
//                    sendcommadPrint(arr);
//
//                } catch (CharacterCodingException e) {
//                    e.printStackTrace();
//                }


//
////                sendcommadPrint(ESCUtil.feedPaper(10));
//                sendcommadPrint(ESCUtil.feedPaper(10));

//                sendcommadPrint(ESCUtil.alignCenter());
//                outputCommand();
//                connect_impressora();
//                readcommadPrint();
                  outputCommand();

//                Uri uri = Uri.parse("jjjjj");
//
//                File file = new  File(uri);
//                file.

            }
        });

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
                            Toast.makeText(MainActivity.this, "Dispositivo permitido", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Dispositivo não permitido.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    public void connect_impressora(){
        for(UsbDevice device : usbManager.getDeviceList().values()) {

            Log.d(LOG_TAG, "------------------------------------------------------");
            Log.d(LOG_TAG, device.getManufacturerName());
            Log.d(LOG_TAG, device.getDeviceName());
            Log.d(LOG_TAG, device.getSerialNumber());
            Log.d(LOG_TAG, String.valueOf(device.getDeviceId()));
            Log.d(LOG_TAG, String.valueOf(device.getProductId()));
            Log.d(LOG_TAG, String.valueOf(device.getProductName()));
            Log.d(LOG_TAG, "------------------------------------------------------");

            if(device.getProductId() == 4864){
                Log.d(LOG_TAG, "-----------------Conectando Impressora-------------------------");
                mDevice = device;
                usbManager.requestPermission(mDevice, mPermissionIntent);
                connection = usbManager.openDevice(mDevice);

                UsbInterface mUsbInterface = findHidInterface();
                try {
                    connection.claimInterface(mUsbInterface, true);
                    for (int i = 0; i < mUsbInterface.getEndpointCount(); i++) {
                        UsbEndpoint usbEndpoint = mUsbInterface.getEndpoint(i);

                        Log.d(LOG_TAG, String.valueOf(usbEndpoint.getType()));
                        Log.d(LOG_TAG, String.valueOf(usbEndpoint.getDirection()));

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


    public int sendcommadPrint(byte[] command){

        int ret = connection.bulkTransfer(usbEndpointOut, command, 0, command.length, 1000);
        Log.d(LOG_Send_Print, String.valueOf(ret));
        return ret;
    }

    public void readcommadPrint(){

        int readLength = 0;
        byte[] inData = new byte[64];
        UsbInterface mUsbInterface = findHidInterface();
        boolean claimed = connection.claimInterface(mUsbInterface, true);
        connection.controlTransfer(0x40, 0x03, 0x0034, 0, null, 0, 0); // baud rate 57600
        connection.controlTransfer(0x40, 0x04, 0x0008, 0, null, 0, 0); // 8-N-1

        while (readLength != -1){
            readLength = connection.bulkTransfer(usbEndpointIn, inData, inData.length, 100);
            Log.d(LOG_Send, "------------------------------------------------------");
            Log.d(LOG_Send, String.valueOf(readLength));
            Log.d(LOG_Send, new String(inData, StandardCharsets.US_ASCII));
            Log.d(LOG_Send, new String(inData, StandardCharsets.UTF_8));
            Log.d(LOG_Send, new String(inData, StandardCharsets.ISO_8859_1));
            Log.d(LOG_Send, new String(inData, StandardCharsets.UTF_16));
            Log.d(LOG_Send, new String(inData, StandardCharsets.UTF_16BE));
            Log.d(LOG_Send, new String(inData, StandardCharsets.UTF_16LE));
            Log.d(LOG_Send, "------------------------------------------------------");
        }



//        byte[] inData1 = connection.getRawDescriptors();
//
//        connection.releaseInterface(mUsbInterface);
//        connection.close();
//        byte[] result = new byte[inData.length - 2];
////        System.arrayCopy(inData, 2, result, 0, inData.length - 2);
//
//
//        String s ="1f3d44";
//        int len = s.length();
//        byte[] ans = new byte[len / 2];
//
//        for (int i = 0; i < s.length(); i += 2) {
//            // using left shift operator on every character
//            ans[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i+1), 16));
//        }
//
//        for(int i=0;i<ans.length;i++){
//            System.out.print(ans[i]+" ");
//            Log.d(LOG_Send, String.valueOf(ans[i]));
//        }
//
////        long result = 0;
//        for (int i = inData1.length - 1; i >= 0; --i) {
////            result <<= 8;
////            result |= inData1[i] & 0x0FF;
//            Log.d(LOG_Send, String.valueOf(inData1[i] & 0x0FF));
//        }
//
//        Log.d(LOG_Send, new String(ans, Charset.defaultCharset()));


//        return 0;
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