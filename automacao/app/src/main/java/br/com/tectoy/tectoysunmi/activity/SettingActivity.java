package br.com.tectoy.tectoysunmi.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.sunmi.extprinterservice.ExtPrinterService;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil;
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    String[] method = new String[]{"API"};

    private TextView mTextView1, mTextView2;
    private ExtPrinterService extPrinterService = null;
    public static KTectoySunmiPrinter kPrinterPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setMyTitle(R.string.setting_title);
        setBack();

        findViewById(R.id.setting_connect).setOnClickListener(this);
        findViewById(R.id.setting_info).setOnClickListener(this);

        if (getDeviceName().equals("SUNMI K2")){

        }

        mTextView1 = findViewById(R.id.setting_conected);
        mTextView2 = findViewById(R.id.setting_disconected);
        mTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDeviceName().equals("SUNMI K2")){
                    connectKPrintService();
                }else {
                    TectoySunmiPrint.getInstance().initSunmiPrinterService(SettingActivity.this);
                }
                setService();
            }
        });
        mTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDeviceName().equals("SUNMI K2")){

                }else {
                    TectoySunmiPrint.getInstance().deInitSunmiPrinterService(SettingActivity.this);
                }
                setService();
            }
        });

            ((TextView)findViewById(R.id.setting_textview1)).setText("API");

        setService();
    }

    @Override
    public void onClick(View v) {
        final ListDialog listDialog;
        switch (v.getId()){
            case R.id.setting_connect:
                listDialog = DialogCreater.createListDialog(this, getResources().getString(R.string.connect_method), getResources().getString(R.string.cancel), method);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        ((TextView)findViewById(R.id.setting_textview1)).setText(method[position]);
                        setMyTitle(R.string.setting_title);
                        listDialog.cancel();
                    }
                });
                listDialog.show();
                break;
            case R.id.setting_info:
                startActivity(new Intent(SettingActivity.this, PrinterInfoActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     *  Set print service connection status
     */
    private void setService(){
          if (getDeviceName().equals("SUNMI K2")){
              if(kPrinterPresenter.sunmiPrinter == kPrinterPresenter.FoundSunmiPrinter){
                  mTextView1.setTextColor(getResources().getColor(R.color.white1));
                  mTextView1.setEnabled(false);
                  mTextView2.setTextColor(getResources().getColor(R.color.white));
                  mTextView2.setEnabled(true);
              }else if(kPrinterPresenter.sunmiPrinter == kPrinterPresenter.CheckSunmiPrinter){
                  handler.postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          setService();
                      }
                  }, 2000);
              }else if(kPrinterPresenter.sunmiPrinter == kPrinterPresenter.LostSunmiPrinter){
                  mTextView1.setTextColor(getResources().getColor(R.color.white));
                  mTextView1.setEnabled(true);
                  mTextView2.setTextColor(getResources().getColor(R.color.white1));
                  mTextView2.setEnabled(false);
              }else{
                  mTextView1.setTextColor(getResources().getColor(R.color.white1));
                  mTextView1.setEnabled(true);
                  mTextView2.setTextColor(getResources().getColor(R.color.white1));
                  mTextView2.setEnabled(false);
              }
          }else {
              if(TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.FoundSunmiPrinter){
                  mTextView1.setTextColor(getResources().getColor(R.color.white1));
                  mTextView1.setEnabled(false);
                  mTextView2.setTextColor(getResources().getColor(R.color.white));
                  mTextView2.setEnabled(true);
              }else if(TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.CheckSunmiPrinter){
                  handler.postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          setService();
                      }
                  }, 2000);
              }else if(TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.LostSunmiPrinter){
                  mTextView1.setTextColor(getResources().getColor(R.color.white));
                  mTextView1.setEnabled(true);
                  mTextView2.setTextColor(getResources().getColor(R.color.white1));
                  mTextView2.setEnabled(false);
              } else{
                  mTextView1.setTextColor(getResources().getColor(R.color.white1));
                  mTextView1.setEnabled(true);
                  mTextView2.setTextColor(getResources().getColor(R.color.white1));
                  mTextView2.setEnabled(false);
              }
          }
    }

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
            kPrinterPresenter = new KTectoySunmiPrinter(SettingActivity.this, extPrinterService);
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

}
