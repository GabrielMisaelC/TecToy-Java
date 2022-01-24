package br.com.tectoy.tectoysunmi.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sunmi.extprinterservice.ExtPrinterService;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;

public class BitmapActivity extends BaseActivity {
    ImageView mImageView;
    TextView mTextView1, mTextView6;
    LinearLayout ll, ll1, ll2;
    Bitmap bitmap, bitmap1;
    CheckBox mCheckBox1, mCheckBox2;

    int mytype;
    int myorientation;

    private ExtPrinterService extPrinterService = null;
    public static KTectoySunmiPrinter kPrinterPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        setMyTitle(R.string.bitmap_title);
        setBack();
        initView();

        ll.setVisibility(View.GONE);

        if (getDeviceName().equals("SUNMI K2")){
            connectKPrintService();
        }


    }

    private void initView() {
        mTextView1 = findViewById(R.id.pic_align_info);

        mCheckBox1 = findViewById(R.id.pic_width);
        mCheckBox2 = findViewById(R.id.pic_height);
        ll = findViewById(R.id.pic_style);
        mTextView6 = findViewById(R.id.cut_paper_info);

        findViewById(R.id.pic_align).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] pos = new String[]{getResources().getString(R.string.align_left), getResources().getString(R.string.align_mid), getResources().getString(R.string.align_right)};
                final ListDialog listDialog = DialogCreater.createListDialog(BitmapActivity.this, getResources().getString(R.string.align_form), getResources().getString(R.string.cancel), pos);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        mTextView1.setText(pos[position]);
                        TectoySunmiPrint.getInstance().setAlign(position);

                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });


        findViewById(R.id.cut_paper_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] cut = new String[]{"Sim","Não"};
                final ListDialog listDialog = DialogCreater.createListDialog(BitmapActivity.this, getResources().getString(R.string.error_qrcode), getResources().getString(R.string.cancel), cut);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        mTextView6.setText(cut[position]);

                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });
        mImageView = findViewById(R.id.bitmap_imageview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test, options);

        }

        if (bitmap1 == null) {
            bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test1, options);
            bitmap1 = scaleImage(bitmap1);
        }

        mImageView.setImageDrawable(new BitmapDrawable(bitmap1));

    }

    /**
     * Scaled image width is an integer multiple of 8 and can be ignored
     */
    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width / 8 + 1) * 8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // 得到新的图片
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }

    public void onClick(View view) {
            if(mTextView6.getText().toString() == "Não") {
                if (getDeviceName().equals("SUNMI K2")){
                    kPrinterPresenter.setAlign(1);
                    kPrinterPresenter.text("Imagem\n");
                    kPrinterPresenter.text("--------------------------------\n");
                    kPrinterPresenter.printBitmap(bitmap, 0);
                } else {
                    TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                    TectoySunmiPrint.getInstance().printText("Imagem\n");
                    TectoySunmiPrint.getInstance().printText("--------------------------------\n");
                    TectoySunmiPrint.getInstance().printBitmap(bitmap);
                    TectoySunmiPrint.getInstance().print3Line();
                }

            }else {
                if (getDeviceName().equals("SUNMI K2")){
                    kPrinterPresenter.setAlign(1);
                    kPrinterPresenter.text("Imagem\n");
                    kPrinterPresenter.text("--------------------------------\n");
                    kPrinterPresenter.printBitmap(bitmap, 0);
                    kPrinterPresenter.print3Line();
                    kPrinterPresenter.cutpaper(KTectoySunmiPrinter.CUTTING_PAPER_FEED, 10);
                }else {
                    TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                    TectoySunmiPrint.getInstance().printText("Imagem\n");
                    TectoySunmiPrint.getInstance().printText("--------------------------------\n");
                    TectoySunmiPrint.getInstance().printBitmap(bitmap);
                    TectoySunmiPrint.getInstance().print3Line();
                    TectoySunmiPrint.getInstance().cutpaper();
                }
            }
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
            kPrinterPresenter = new KTectoySunmiPrinter(BitmapActivity.this, extPrinterService);
        }
    };
}


