package br.com.tectoy.tectoysunmi.activity;

import static br.com.tectoy.tectoysunmi.R.drawable.test;
import static br.com.tectoy.tectoysunmi.R.drawable.test1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Presentation;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunmi.extprinterservice.ExtPrinterService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.activity.ExemploNFCIdRW.NfcExemplo;
import br.com.tectoy.tectoysunmi.threadhelp.ThreadPoolManageer;
import br.com.tectoy.tectoysunmi.utils.ESCUtil;
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.HintOneBtnDialog;

public class MainActivity extends AppCompatActivity {

    int height= 0;
    HintOneBtnDialog mHintOneBtnDialog;
    boolean run;
    String alert = "Fun????o N??o Disponivel No Device";
    public static boolean isK1 = false;
    public static boolean isVertical = false;
    private ExtPrinterService extPrinterService = null;
    public static KTectoySunmiPrinter kPrinterPresenter;
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


        private final DemoDetails[] demos = {
                new DemoDetails(R.string.function_all, R.drawable.function_all,
                        null),
                new DemoDetails(R.string.function_qrcode, R.drawable.function_qr,
                        null),
                new DemoDetails(R.string.function_barcode, R.drawable.function_barcode,
                        null),
                new DemoDetails(R.string.function_text, R.drawable.function_text,
                        null),
                new DemoDetails(R.string.function_tab, R.drawable.function_tab,
                        null),
                new DemoDetails(R.string.function_pic, R.drawable.function_pic,
                        null),
                new DemoDetails(R.string.function_threeline, R.drawable.function_threeline,
                        null),
                new DemoDetails(R.string.function_cash, R.drawable.function_cash,
                        null),
                new DemoDetails(R.string.function_lcd, R.drawable.function_lcd,
                        null),
                new DemoDetails(R.string.function_status, R.drawable.function_status,
                        null),
                new DemoDetails(R.string.function_blackline, R.drawable.function_blackline,
                        null),
                new DemoDetails(R.string.function_label, R.drawable.function_label,
                        null),
                new DemoDetails(R.string.cut_paper, R.drawable.function_cortar, null),
                new DemoDetails(R.string.function_scanner, R.drawable.function_scanner, null),
                new DemoDetails(R.string.function_led, R.drawable.function_led, null),
                new DemoDetails(R.string.function_paygo, R.drawable.function_payment, Paygo.class),
                new DemoDetails(R.string.function_scan, R.drawable.function_scanner, null),
                new DemoDetails(R.string.function_nfc, R.drawable.function_nfc, NfcExemplo.class),
                new DemoDetails(R.string.function_m_Sitef, R.drawable.function_payment, MSitef.class),
                new DemoDetails(R.string.display, R.drawable.telas, null
                )
        };
    private VideoDisplay videoDisplay = null;
    private ScreenManager screenManager = ScreenManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupRecyclerView();
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// Largura da tela
        height = dm.heightPixels;// Largura da tela
        isVertical = height > width;
        isK1 = isHaveCamera() && isVertical;
        usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        // Faz a valida????o de Permiss??o para acessar a porta USB

        mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);
        String deviceName = getDeviceName();
        if (isK1 = true && height > 1856){
            connectKPrintService();
        }
        MediaRouter mediaRouter = (MediaRouter) this.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(1);
        if (route != null) {
            Display presentationDisplay = route.getPresentationDisplay();
            if (presentationDisplay != null) {
                Presentation presentation = new VideoDisplay(this, presentationDisplay, Environment.getExternalStorageDirectory().getPath() + "/video_01.mp4");
              presentation.show();
            }
        }
    }
    // Cone????o Impress??o K2

    private void connectKPrintService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.extprinterservice");
        intent.setAction("com.sunmi.extprinterservice.PrinterService");
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            extPrinterService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            extPrinterService = ExtPrinterService.Stub.asInterface(service);
            kPrinterPresenter = new KTectoySunmiPrinter(MainActivity.this, extPrinterService);
        }
    };

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

    // Modulo de Verifica????o do Device
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

    private void setupRecyclerView() {
        final GridLayoutManager layoutManage = new GridLayoutManager(this, 2);
        RecyclerView mRecyclerView = findViewById(R.id.worklist);
        mRecyclerView.setLayoutManager(layoutManage);
        mRecyclerView.setAdapter(new WorkTogetherAdapter());
    }

    class WorkTogetherAdapter extends RecyclerView.Adapter<WorkTogetherAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.demoDetails = demos[position];
            holder.tv.setText(demos[position].titleId);
            holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(demos[position].iconResID), null, null);
        }
        public void alerta(String alerta){
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Aten????o");
            alertDialog.setMessage(alerta);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        @Override
        public int getItemCount() {
            return demos.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            DemoDetails demoDetails;

            MyViewHolder(View v) {
                super(v);
                tv = v.findViewById(R.id.worktext);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(demoDetails == null){
                            return;
                        }
                        if(demoDetails.activityClass != null){
                            startActivity(new Intent(MainActivity.this, demoDetails.activityClass));
                        }
                        if(demoDetails.titleId == R.string.function_all){
                            Log.d("Nome", getDeviceName());
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI P2mini") || getDeviceName().equals("SUNMI D2mini") || getDeviceName().equals("SUNMI L2H")){
                                alerta(alert);
                            }else {
                                if (isK1 = true && height > 1856) {
                                    try {
                                        KTesteCompleto();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    String result = TectoySunmiPrint.getInstance().statusPrinter(MainActivity.this);
                                    if (result.equals("Impressora n??o existe")) {
                                        connect_impressora();
                                        EscTesteCompleto();
                                    }else{
                                        TesteCompleto();
                                    }
                                }
                            }
                        }
                        if(demoDetails.titleId == R.string.cut_paper){
                            if(getDeviceName().equals("SUNMI T2s") || getDeviceName().equals("SUNMI K2") || getDeviceName().equals("SUNMI K2_MINI") || getDeviceName().equals("SUNMI T2mini")){
                                if (isK1 = true && height > 1856) {
                                    try {

                                        kPrinterPresenter.print3Line();
                                        kPrinterPresenter.cutpaper(KTectoySunmiPrinter.CUTTING_PAPER_FEED, 10);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    TectoySunmiPrint.getInstance().cutpaper();
                                }
                            }else {
                                alerta(alert);
                            }
                        }
                        if(demoDetails.titleId == R.string.function_threeline){
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI L2H") || getDeviceName().equals("SUNMI D2mini") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI P2mini")){
                                alerta(alert);
                            }else {
                                if (isK1 = true && height > 1856) {
                                    try {
                                        kPrinterPresenter.print3Line();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    TectoySunmiPrint.getInstance().print3Line();
                                }
                            }
                        }
                        if(demoDetails.titleId == R.string.function_cash){
                            if(getDeviceName().equals("SUNMI D2s") || getDeviceName().equals("SUNMI T2mini")){
                                TectoySunmiPrint.getInstance().openCashBox();
                            }else {
                                alerta(alert);
                            }
                            }

                        if(demoDetails.titleId == R.string.function_status){
                            if (isK1 = true && height > 1856){
                                try {
                                    Toast.makeText(getApplicationContext(), kPrinterPresenter.traduzStatusImpressora(kPrinterPresenter.getStatus()), Toast.LENGTH_LONG ).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                TectoySunmiPrint.getInstance().showPrinterStatus(MainActivity.this);
                            }
                        }
                        if(demoDetails.titleId == R.string.function_multi){
                            if(mHintOneBtnDialog  == null){
                                mHintOneBtnDialog = DialogCreater.createHintOneBtnDialog(MainActivity.this, null, getResources().getString(R.string.multithread), getResources().getString(R.string.multithread_stop), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        run = false;
                                        mHintOneBtnDialog.cancel();
                                    }
                                });
                            }
                            mHintOneBtnDialog.show();
                            run = true;
                            multiPrint();
                        }
                        if(demoDetails.titleId == R.string.function_led){
                            if(getDeviceName().equals("SUNMI K2_MINI") || getDeviceName().equals("SUNMI K2")){
                                Intent intent = new Intent(MainActivity.this, LedActivity.class);
                                startActivity(intent);
                            }else {
                                alerta(alert);
                            }
                        }
                        if (demoDetails.titleId == R.string.display){
                            if(getDeviceName().equals("SUNMI T2s")){
                                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                                startActivity(intent);
                            }else {
                                alerta(alert);
                            }
                        }
                        if(demoDetails.titleId == R.string.function_scan){
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI L2H") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI P2mini") || getDeviceName().equals("SUNMI V2_PRO")){
                                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                                startActivity(intent);
                            }else {
                                alerta(alert);
                            }
                        }
                        if(demoDetails.titleId == R.string.function_lcd){
                            if(getDeviceName().equals("SUNMI T2mini")){
                                Intent intent = new Intent(MainActivity.this, LcdActivity.class);
                                startActivity(intent);
                            }else {
                                alerta(alert);
                            }
                        }
                        if(demoDetails.titleId == R.string.function_blackline){
                            if(getDeviceName().equals("SUNMI V2_PRO")){
                                Intent intent = new Intent(MainActivity.this, BlackLabelActivity.class);
                                startActivity(intent);
                            }else {
                                alerta("Fun????o Disponivel No Device V2 Pro");
                            }
                        }
                        if (demoDetails.titleId == R.string.function_label){
                            if(getDeviceName().equals("SUNMI V2_PRO")){
                                Intent intent = new Intent(MainActivity.this, LabelActivity.class);
                                startActivity(intent);
                            }else {
                                alerta("Fun????o Disponivel No Device V2 Pro");
                            }
                        }
                        if (demoDetails.titleId == R.string.function_scanner){
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI L2H") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI P2mini")){
                                alerta(alert);
                            }else {
                                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                                startActivity(intent);
                            }
                        }
                        if (demoDetails.titleId == R.string.function_barcode){
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI L2H") || getDeviceName().equals("SUNMI D2mini") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI P2mini")){
                                alerta(alert);
                            }else {
                                Intent intent = new Intent(MainActivity.this, BarCodeActivity.class);
                                startActivity(intent);
                            }
                        }
                        if (demoDetails.titleId == R.string.function_qrcode){
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI L2H") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI P2mini") || getDeviceName().equals("SUNMI D2mini")){
                                alerta(alert);
                            }else {
                                Intent intent = new Intent(MainActivity.this, QrActivity.class);
                                startActivity(intent);
                            }
                        }
                        if (demoDetails.titleId == R.string.function_text){
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI L2H") || getDeviceName().equals("SUNMI D2mini") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI P2mini")){
                                alerta(alert);
                            }else {
                                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                                startActivity(intent);
                            }
                        }
                        if (demoDetails.titleId == R.string.function_tab){
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI L2H") || getDeviceName().equals("SUNMI D2mini") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI P2mini")){
                                alerta(alert);
                            }else {
                                Intent intent = new Intent(MainActivity.this, TableActivity.class);
                                startActivity(intent);
                            }
                        }
                        if (demoDetails.titleId == R.string.function_pic){
                            if(getDeviceName().equals("SUNMI L2") || getDeviceName().equals("SUNMI D2mini") || getDeviceName().equals("SUNMI L2K") || getDeviceName().equals("SUNMI L2H") || getDeviceName().equals("SUNMI P2mini")){
                                alerta(alert);
                            }else {
                                Intent intent = new Intent(MainActivity.this, BitmapActivity.class);
                                startActivity(intent);
                            }
                        }
                        //SUNMI V2_PRO
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.function, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    // Teste Completo Do K2
    public void KTesteCompleto() throws RemoteException {

        // Alinhamento
        kPrinterPresenter.printStyleBold(false);
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.text("Alinhamento\n");
        kPrinterPresenter.text("--------------------------------\n");
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT);
        kPrinterPresenter.text("TecToy Automa????o\n");
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.text("TecToy Automa????o\n");
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_RIGTH);
        kPrinterPresenter.text("TecToy Automa????o\n");
        kPrinterPresenter.print3Line();

        // Formas de impress??o

        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.text("Formas de Impress??o\n");
        kPrinterPresenter.text("--------------------------------\n");
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT);
        kPrinterPresenter.printStyleBold(true);
        kPrinterPresenter.text("TecToy Automa????o\n");


        // Barcode

        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.text("Imprime BarCode\n");
        kPrinterPresenter.text("--------------------------------\n");
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT);
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 0);
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 2);
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_RIGTH);
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 1);
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 3);
        kPrinterPresenter.print3Line();
        // QrCode

        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.text("Imprime QrCode\n");
        kPrinterPresenter.text("--------------------------------\n");
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT);
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0);
        kPrinterPresenter.print3Line();
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0);
        kPrinterPresenter.print3Line();
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_RIGTH);
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0);
        kPrinterPresenter.print3Line();
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT);
        kPrinterPresenter.printDoubleQRCode("www.tectoyautomacao.com.br","tectoy", 7, 1);
        // Imagem


        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.text("Imprime Imagem\n");
        kPrinterPresenter.text("--------------------------------\n");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        Bitmap bitmap1 = null;
        if (bitmap1 == null) {
            bitmap1 = BitmapFactory.decodeResource(getResources(), test1, options);
            bitmap1 = scaleImage(bitmap1);
        }
        kPrinterPresenter.printBitmap(bitmap1,  0);
        kPrinterPresenter.setAlign(0);
        kPrinterPresenter.printBitmap(bitmap1,  0);
        kPrinterPresenter.setAlign(2);
        kPrinterPresenter.printBitmap(bitmap1,  0);
        // Tabelas


        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER);
        kPrinterPresenter.text("Imprime Tabelas\n");
        kPrinterPresenter.text("--------------------------------\n");
        String[] prod = new String[3];
        int[] width = new int[3];
        int[] align = new int[3];

        width[0] = 100;
        width[1] = 50;
        width[2] = 50;

        align[0] = kPrinterPresenter.Alignment_LEFT;
        align[1] = kPrinterPresenter.Alignment_CENTER;
        align[2] = kPrinterPresenter.Alignment_RIGTH;

        prod[0] = "Produto 001";
        prod[1] = "10 und";
        prod[2] = "3,98";
        kPrinterPresenter.printTable(prod, width, align);

        prod[0] = "Produto 002";
        prod[1] = "10 und";
        prod[2] = "3,98";
        kPrinterPresenter.printTable(prod, width, align);


        prod[0] = "Produto 003";
        prod[1] = "10 und";
        prod[2] = "3,98";
        kPrinterPresenter.printTable(prod, width, align);


        prod[0] = "Produto 004";
        prod[1] = "10 und";
        prod[2] = "3,98";
        kPrinterPresenter.printTable(prod, width, align);


        prod[0] = "Produto 005";
        prod[1] = "10 und";
        prod[2] = "3,98";
        kPrinterPresenter.printTable(prod, width, align);

        prod[0] = "Produto 006";
        prod[1] = "10 und";
        prod[2] = "3,98";
        kPrinterPresenter.printTable(prod, width, align);


        kPrinterPresenter.print3Line();
        kPrinterPresenter.cutpaper(KTectoySunmiPrinter.HALF_CUTTING, 10);

    }
    // Teste Completo dos Demais Devices
    public void EscTesteCompleto() {
        sendcommadPrint(ESCUtil.alignCenter());
        sendcommadPrint("Teste de Alinhamento\n".getBytes());
        sendcommadPrint("------------------------\n\n".getBytes());
        sendcommadPrint(ESCUtil.feedPaper(1));

        sendcommadPrint(ESCUtil.alignLeft());
        sendcommadPrint("TecToy Automa????o\n".getBytes());

        sendcommadPrint(ESCUtil.alignCenter());
        sendcommadPrint("TecToy Automa????o\n".getBytes());

        sendcommadPrint(ESCUtil.alignRight());
        sendcommadPrint("TecToy Automa????o\n".getBytes());
        sendcommadPrint(ESCUtil.alignLeft());

        sendcommadPrint(ESCUtil.alignCenter());
        sendcommadPrint("Formas de Impress??o\n".getBytes());
        sendcommadPrint("------------------------\n\n".getBytes());
        sendcommadPrint(ESCUtil.feedPaper(1));

        sendcommadPrint(ESCUtil.alignCenter());
        sendcommadPrint("Imprime Barcode\n".getBytes());
        sendcommadPrint("------------------------\n\n".getBytes());
        sendcommadPrint(ESCUtil.feedPaper(1));

        sendcommadPrint("Formas de QrCode\n".getBytes());
        sendcommadPrint("------------------------\n\n".getBytes());
        sendcommadPrint(ESCUtil.feedPaper(1));
        sendcommadPrint(ESCUtil.alignLeft());
        sendcommadPrint(ESCUtil.getPrintQRCode("7894900700046", 300, 2));
        sendcommadPrint(ESCUtil.alignCenter());
        sendcommadPrint(ESCUtil.getPrintQRCode("7894900700046", 162, 2));
        sendcommadPrint(ESCUtil.alignRight());
        sendcommadPrint(ESCUtil.getPrintQRCode("7894900700046", 162, 2));
        sendcommadPrint(ESCUtil.alignLeft());
        sendcommadPrint(ESCUtil.getPrintDoubleQRCode("7894900700046","7894900700046",162,2));

        sendcommadPrint(ESCUtil.alignCenter());
        sendcommadPrint("Imprime Tabela\n".getBytes());
        sendcommadPrint("------------------------\n\n".getBytes());
        sendcommadPrint(ESCUtil.feedPaper(1));
    }
    public void TesteCompleto() {


        TectoySunmiPrint.getInstance().initPrinter();
        TectoySunmiPrint.getInstance().setSize(24);

        // Alinhamento do texto

        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Alinhamento\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");

        // Formas de impress??o
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Formas de Impress??o\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");
        TectoySunmiPrint.getInstance().setSize(28);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printStyleBold(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleAntiWhite(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleDoubleHeight(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleDoubleWidth(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleInvert(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleItalic(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleStrikethRough(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleUnderLine(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automa????o\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automa????o\n", 35);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automa????o\n", 28);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automa????o\n",50);
       // TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().setSize(24);


        // Impress??o de BarCode
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Imprime BarCode\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
                TectoySunmiPrint.BarCodeTextPosition_INFORME_UM_TEXTO);
        TectoySunmiPrint.getInstance().printAdvanceLines(2);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
                TectoySunmiPrint.BarCodeTextPosition_ABAIXO_DO_CODIGO_DE_BARRAS);
        TectoySunmiPrint.getInstance().printAdvanceLines(2);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
                TectoySunmiPrint.BarCodeTextPosition_ACIMA_DO_CODIGO_DE_BARRAS_BARCODE);
        TectoySunmiPrint.getInstance().printAdvanceLines(2);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
                TectoySunmiPrint.BarCodeTextPosition_ACIMA_E_ABAIXO_DO_CODIGO_DE_BARRAS);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().print3Line();
        // Impress??o de BarCode
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Imprime QrCode\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printDoubleQRCode("www.tectoysunmi.com.br","tectoysunmi", 7, 1);
        //TectoySunmiPrint.getInstance().feedPaper();


        // Impres??o Imagem
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Imprime Imagem\n");
        TectoySunmiPrint.getInstance().printText("-------------------------------\n");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        Bitmap bitmap1 = null;
        Bitmap bitmap = null;
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), test, options);
        }
        if (bitmap1 == null) {
            bitmap1 = BitmapFactory.decodeResource(getResources(), test1, options);
            bitmap1 = scaleImage(bitmap1);
        }
        TectoySunmiPrint.getInstance().printBitmap(bitmap1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().print3Line();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printBitmap(bitmap1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().print3Line();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printBitmap(bitmap1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().print3Line();

        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Imprime Tabela\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");

        String[] prod = new String[3];
        int[] width = new int[3];
        int[] align = new int[3];

        width[0] = 100;
        width[1] = 50;
        width[2] = 50;

        align[0] = TectoySunmiPrint.Alignment_LEFT;
        align[1] = TectoySunmiPrint.Alignment_CENTER;
        align[2] = TectoySunmiPrint.Alignment_RIGTH;

        prod[0] = "Produto 001";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 002";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 003";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 004";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 005";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 006";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        TectoySunmiPrint.getInstance().print3Line();
        TectoySunmiPrint.getInstance().openCashBox();
        TectoySunmiPrint.getInstance().cutpaper();

    }
    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // ?????????????????????
        int newWidth = (width / 8 + 1) * 8;
        // ??????????????????
        float scaleWidth = ((float) newWidth) / width;
        // ?????????????????????matrix??????
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // ??????????????????
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }



    private void multiPrint(){
        ThreadPoolManageer.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                while(run){
//                    TectoySunmiPrint.getInstance().sendRawData(BytesUtil.getBaiduTestBytes());
                    TesteCompleto();
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }


            }
        });

    }

    private class DemoDetails {
        @StringRes
        private final int titleId;
        @DrawableRes
        private final int iconResID;
        private final Class<? extends Activity> activityClass;

        private DemoDetails(@StringRes int titleId, @DrawableRes int descriptionId,
                            Class<? extends Activity> activityClass) {
            super();
            this.titleId = titleId;
            this.iconResID = descriptionId;
            this.activityClass = activityClass;
        }
    }
    // Impress??o Via USB
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
                        //  Toast.makeText(MainActivity.this, "Dispositivo n??o permitido.", Toast.LENGTH_LONG).show();
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