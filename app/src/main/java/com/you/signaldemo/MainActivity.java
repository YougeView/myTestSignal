package com.you.signaldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //各组件
    Button btn_begin,btn_cat,btn_save;

    EditText input_station;

    LinearLayout
            inmoble_4g_layout,inmoble_3g_layout,inmoble_2g_layout,
            inunicom_4g_layout,inunicom_3g_layout,inunicom_2g_layout,
            inlte_4g_layout,inlte_2g_layout;
    ImageView
            inmoble_4g_img,inmoble_3g_img,inmoble_2g_img,
            inunicom_4g_img,inunicom_3g_img,inunicom_2g_img,
            inlte_4g_img,inlte_2g_img;
    ProgressBar
            inmoble_4g_reflesh,inmoble_3g_reflesh,inmoble_2g_reflesh,
            inunicom_4g_reflesh,inunicom_3g_reflesh,inunicom_2g_reflesh,
            inlte_4g_reflesh,inlte_2g_reflesh;
    TextView
            inmoble_4g_dbm,inmoble_3g_dbm,inmoble_2g_dbm,
            inunicom_4g_dbm,inunicom_3g_dbm,inunicom_2g_dbm,
            inlte_4g_dbm,inlte_2g_dbm;

    //数组初始化
    LinearLayout[] layouts={
            inmoble_4g_layout,inmoble_3g_layout,inmoble_2g_layout,
            inunicom_4g_layout,inunicom_3g_layout,inunicom_2g_layout,
            inlte_4g_layout,inlte_2g_layout
    };
    ImageView[]
            imgs={
                    inmoble_4g_img,inmoble_3g_img,inmoble_2g_img,
                    inunicom_4g_img,inunicom_3g_img,inunicom_2g_img,
                    inlte_4g_img,inlte_2g_img
            };
    ProgressBar[]
            refleshs={
                    inmoble_4g_reflesh,inmoble_3g_reflesh,inmoble_2g_reflesh,
                    inunicom_4g_reflesh,inunicom_3g_reflesh,inunicom_2g_reflesh,
                    inlte_4g_reflesh,inlte_2g_reflesh
    };
    TextView[] dbms={
                inmoble_4g_dbm,inmoble_3g_dbm,inmoble_2g_dbm,
                inunicom_4g_dbm,inunicom_3g_dbm,inunicom_2g_dbm,
                inlte_4g_dbm,inlte_2g_dbm
    };
    int[]
            layoutID={
                    R.id.inmoble_4g_layout,R.id.inmoble_3g_layout,R.id.inmoble_2g_layout,
                    R.id.inunicom_4g_layout,R.id.inunicom_3g_layout,R.id.inunicom_2g_layout,
                    R.id.inlte_4g_layout,R.id.inlte_2g_layout
            },
            imgID={
                    R.id.inmoble_4g_img,R.id.inmoble_3g_img,R.id.inmoble_2g_img,
                    R.id.inunicom_4g_img,R.id.inunicom_3g_img,R.id.inunicom_2g_img,
                    R.id.inlte_4g_img,R.id.inlte_2g_img
            },
            refleshID={
                    R.id.inmoble_4g_reflesh,R.id.inmoble_3g_reflesh,R.id.inmoble_2g_reflesh,
                    R.id.inunicom_4g_reflesh,R.id.inunicom_3g_reflesh,R.id.inunicom_2g_reflesh,
                    R.id.inlte_4g_reflesh,R.id.inlte_2g_reflesh
            },
            dbmID={
                    R.id.inmoble_4g_dbm,R.id.inmoble_3g_dbm,R.id.inmoble_2g_dbm,
                    R.id.inunicom_4g_dbm,R.id.inunicom_3g_dbm,R.id.inunicom_2g_dbm,
                    R.id.inlte_4g_dbm,R.id.inlte_2g_dbm
    };

    ImageView launchImg;
    RelativeLayout bgLayout;
    MyUtil util=new MyUtil();
    String signalUnit="dbm";
    int DISPLAY_PEROID=3000;
    long onetime=System.currentTimeMillis(),begintime=System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        launchPage();
        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        init();
        autoReflesh();
    }

    //启动页
    private void launchPage(){
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                );
        getSupportActionBar().hide();
        launchImg= (ImageView) findViewById(R.id.launch_img);
        bgLayout= (RelativeLayout) findViewById(R.id.bg_layout);
        launchImg.postDelayed(new Runnable() {
            @Override
            public void run() {
                launchImg.setVisibility(View.GONE);
                bgLayout.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }, DISPLAY_PEROID);
    }

    //组件初始化
    public void init(){
        for(int i=0;i<layouts.length;i++){
            layouts[i]= (LinearLayout) findViewById(layoutID[i]);
            imgs[i]= (ImageView) findViewById(imgID[i]);
            refleshs[i]= (ProgressBar) findViewById(refleshID[i]);
            dbms[i]= (TextView) findViewById(dbmID[i]);
            final int position=i;
            layouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onetime=System.currentTimeMillis();
                    detectSignal(position);
                }
            });
        }
        btn_begin= (Button) findViewById(R.id.btn_begin);
        btn_cat= (Button) findViewById(R.id.btn_cat);
        btn_save= (Button) findViewById(R.id.btn_save);
        btn_begin.setOnClickListener(this);
        btn_cat.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }
    //检测信号强度
    public void detectSignal(final int position){
        // TODO: 2016-10-20 用随机数测试
        dbms[position].setVisibility(View.GONE);
        imgs[position].setVisibility(View.GONE);
        lostFocus();
        refleshs[position].setVisibility(View.VISIBLE);
        imgs[position].postDelayed(new Runnable() {
            @Override
            public void run() {
                Random random=new Random();
                int dbmvalue=random.nextInt(120)*-1;
                refleshs[position].setVisibility(View.GONE);
                imgs[position].setVisibility(View.VISIBLE);
                dbms[position].setText(dbmvalue+signalUnit);
                dbms[position].setVisibility(View.VISIBLE);
                DesignationImg(dbmvalue,position);
                recoverFocus();
            }
        }, 2000);
    }
    //检测所有信号强度
    public void detectAllSignal(){
        for (int i=0;i<layouts.length;i++){
            detectSignal(i);
        }

    }
    //根据dbm值指定信号图片
    public void DesignationImg(int dbmvalue,int position){
        if(dbmvalue<-120){
            imgs[position].setImageResource(R.drawable.no_signal);
        }else if(dbmvalue<-100&&dbmvalue>=-120){
            imgs[position].setImageResource(R.drawable.one_signal);
        }else if(dbmvalue<-80&&dbmvalue>=-100){
            imgs[position].setImageResource(R.drawable.two_signal);
        }else if(dbmvalue<-60&&dbmvalue>=-80){
            imgs[position].setImageResource(R.drawable.three_signal);
        }else if(dbmvalue<-40&&dbmvalue>=-60){
            imgs[position].setImageResource(R.drawable.four_signal);
        }else{
            imgs[position].setImageResource(R.drawable.five_signal);
        }

    }

    //截取屏幕除状态栏外
    private Bitmap getScreenSnapshot(){
        Bitmap bitmap=null;
        //获取当前屏幕最顶级的View
        View view=getWindow().getDecorView();
        view.buildDrawingCache();

        //获取顶部状态栏高度
        Rect rect=new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBar=rect.top;

        //获取屏幕的高度及宽度
        Display display=getWindowManager().getDefaultDisplay();
        int width=display.getWidth();
        int height=display.getHeight();

        //构造一个bitmap并返回
        bitmap=Bitmap.createBitmap(view.getDrawingCache(),0,statusBar,width,height-statusBar);

        view.destroyDrawingCache();
        return bitmap;
    }
    //保存到sdcard的方法
    private boolean saveBitmapToSdcard(Bitmap bitmap,String dirname,String filename) throws IOException {
        //判断
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File dir=new File(dirname);
            if(!dir.exists()){
                dir.mkdirs();
            }

            File file=new File(filename);
            if(!file.exists()){
                dir.createNewFile();
                FileOutputStream fos=new FileOutputStream(file);
                if(fos!=null){
                    bitmap.compress(Bitmap.CompressFormat.PNG,80,fos);
                    fos.flush();
                    fos.close();
                }
                return true;
            }else {
                util.CreateToast(getContext(),"该站点名已存在");
                return false;
            }
        }else {
            util.CreateToast(getContext(),"Sd卡不存在或不可用");
            return false;
        }
    }
    //截图的操作
    public void screenshot(){
        Bitmap bitmap=getScreenSnapshot();
        input_station= (EditText) findViewById(R.id.input_station);
        String fileName=input_station.getText().toString();
        if(!fileName.equals("")){
            try {
                //保存到应用内部数据文件夹(也就是sdcard/data/data/packagename/files/screen)
                String dir="sdcard/signal record";
                String filepath=dir+"/"+fileName+".png";
                if(saveBitmapToSdcard(bitmap,dir,filepath)==true){
                    util.CreateToast(getContext(),"已保存至sd卡");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            util.CreateToast(getContext(),"请输入站点");
        }
    }

    //底部菜单按钮的点击事件
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_begin:
                detectAllSignal();
                begintime=System.currentTimeMillis();
                break;
            case R.id.btn_cat:
                if(canMakeSmores()){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this,"android.permission.WRITE_EXTERNAL_STORAGE")) {
                                hasRejected();
                        }else {
                            myRequestPermissions(permsRequestCode1);
                        }
                }else {
                    screenshot();
                }
                break;
            case R.id.btn_save:
                if(canMakeSmores()){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,"android.permission.WRITE_EXTERNAL_STORAGE")) {
                        hasRejected();
                    }else {
                        myRequestPermissions(permsRequestCode2);
                    }
                }else {
                    Intent intent=new Intent(MainActivity.this,RecordActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    //定时器自动刷新信号强度
    Timer timer=new Timer();
    public void autoReflesh(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTime=System.currentTimeMillis();
                if(currentTime-onetime>=60000&&currentTime-begintime>=60000){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            detectAllSignal();
                        }
                    });
                    onetime=begintime=System.currentTimeMillis();
                }
            }
        },0,100);
    }

    //无法进行点击事件
    public void lostFocus(){
        for(int i=0;i<layouts.length;i++){
            layouts[i].setEnabled(false);
        }
        btn_begin.setEnabled(false);
        btn_save.setEnabled(false);
        btn_cat.setEnabled(false);
    }
    //恢复到可以点击
    public void recoverFocus(){
        for(int i=0;i<layouts.length;i++){
            layouts[i].setEnabled(true);
        }
        btn_begin.setEnabled(true);
        btn_save.setEnabled(true);
        btn_cat.setEnabled(true);
    }

    final int permsRequestCode1 = 200;
    final int permsRequestCode2 = 201;

    //判断当前sdk版本是否大于23
    private boolean canMakeSmores(){
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    //申请写外部sd的权限
    public void myRequestPermissions(int permsRequestCode){
        String[] perms={"android.permission.WRITE_EXTERNAL_STORAGE"};
        ActivityCompat.requestPermissions(this,perms, permsRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean writeAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(writeAccepted){
                    screenshot();
                }else{
                    hasRejected();
                    //用户授权拒绝之后，友情提示一下就可以了
                }
                break;
            case 201:
                boolean readAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(readAccepted){
                    Intent intent=new Intent(MainActivity.this,RecordActivity.class);
                    startActivity(intent);
                }else{
                    hasRejected();
                    //用户授权拒绝之后，友情提示一下就可以了
                }
                break;
        }
    }

    public Context getContext(){
        return this;
    }

    //拒绝一次后调用
    public void hasRejected(){
        View view=View.inflate(getContext(),R.layout.rejected_dialog_view,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(false);
        Button cancel= (Button) view.findViewById(R.id.cancel);
        Button setting= (Button) view.findViewById(R.id.setting);
        final AlertDialog dialog=builder.create();
        dialog.show();
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppDetailSettingIntent();
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    //跳转到应用程序自身的权限设置页面
    private void getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivityForResult(localIntent,0);
    }
}
