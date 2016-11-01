package com.you.signaldemo;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecordActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {

    List<File> screenshot;
    MyAdapter mAdapter;
    ListView listView;
    TextView tvNull;

    PopupWindow popupWindow;
    LinearLayout bottomlayout;
    View popupLayout,theline;
    Bitmap mBitmap;
    FileInputStream fis;
    ImageView imgScreenshot;
    boolean isall=true,boxisDisplay=false,pwDisplay=false;
    Button btncancel,btnallcheck,btndelete,popcancel,popedit,popshare;
    TextView tvAll;
    int pwp;
    MyUtil util=new MyUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().hide();
        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initBottomBar();

        initListView();

        initPopupWindow();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        mAdapter=new MyAdapter(RecordActivity.this,getScreenshot());
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgDelete:
                deleteRecord();
                break;
            case R.id.cancel:
                cancelMethod();
                break;
            case R.id.allcheck:
                allCheck();
                break;
            case R.id.pop_cancel:
                popupWindow.dismiss();
                pwDisplay=false;
                break;
            case R.id.pop_edit:
                editPicture(pwp);
                break;
            case R.id.pop_share:
                sharePicture(pwp);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if(!boxisDisplay&&pwDisplay==false){
            try {
                fis=new FileInputStream(screenshot.get(position));
                mBitmap= BitmapFactory.decodeStream(fis);
                imgScreenshot.setImageBitmap(mBitmap);
                //背景变暗准备弹出popupWindow
                setBackgroundAlpa(0.5f);
                popupWindow.showAtLocation(findViewById(R.id.activity_record), Gravity.CENTER,0,0);
                pwp=position;
                pwDisplay=true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            RelativeLayout root= (RelativeLayout) parent.getChildAt(position);
            LinearLayout layout= (LinearLayout) root.getChildAt(0);
            CheckBox checkBox= (CheckBox) layout.getChildAt(2);
            checkBox.setChecked(!checkBox.isChecked());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(!boxisDisplay&&pwDisplay==false){
            bottomlayout.setVisibility(View.VISIBLE);
            theline.setVisibility(View.VISIBLE);
            for (int i = 0; i < screenshot.size(); i++) {
                mAdapter.getIsVisibleMap().put(i,CheckBox.VISIBLE);
                mAdapter.getIsCheckMap().put(i,false);
            }
            mAdapter.notifyDataSetChanged();
            boxisDisplay=true;
            return true;
        }
        return false;
    }

    //监听手机返回键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(boxisDisplay&&!pwDisplay){
                    cancelMethod();
                    return false;
                }else if(!boxisDisplay&&pwDisplay){
                    popupWindow.dismiss();
                    pwDisplay=false;
                    return false;
                }else {
                    finish();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //初始化底部菜单栏
    private  void initBottomBar(){
        bottomlayout= (LinearLayout) findViewById(R.id.bottom_layout);
        theline=findViewById(R.id.the_line);
        btncancel= (Button) findViewById(R.id.cancel);
        btnallcheck= (Button) findViewById(R.id.allcheck);
        btndelete= (Button) findViewById(R.id.imgDelete);
        tvNull=(TextView)findViewById(R.id.tvNull);        //显示无内容的ImageView，默认不显示
        tvAll= (TextView) findViewById(R.id.tvAll);
        btncancel.setOnClickListener(this);
        btnallcheck.setOnClickListener(this);
        btndelete.setOnClickListener(this);
    }

    //初始化listview
    private void initListView(){
        mAdapter=new MyAdapter(RecordActivity.this,getScreenshot());
        if(screenshot.size()==0){
            //显示无内容,不加载listview
            tvNull.setVisibility(View.VISIBLE);
            return;
        }else {
            tvNull.setVisibility(View.INVISIBLE);
        }
        listView=(ListView)findViewById(R.id.picList);
        listView.setAdapter(mAdapter);
        //单击listview使用popupWindow显示截图
        listView.setOnItemClickListener(this);
        //长按listview使用其他应用打开截图
        listView.setOnItemLongClickListener(this);
    }

    //配置popupWindow
    private void initPopupWindow(){

        //动态加载view
        popupLayout=LayoutInflater.from(this).inflate(R.layout.popup_view,null);
        imgScreenshot=(ImageView)popupLayout.findViewById(R.id.imgScreenshot);
        popcancel= (Button) popupLayout.findViewById(R.id.pop_cancel);
        popedit= (Button) popupLayout.findViewById(R.id.pop_edit);
        popshare= (Button) popupLayout.findViewById(R.id.pop_share);
        popcancel.setOnClickListener(this);
        popedit.setOnClickListener(this);
        popshare.setOnClickListener(this);
        //设置popupWindow的属性
        popupWindow=new PopupWindow(popupLayout);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);

//        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                setBackgroundAlpa(1);
//                popupWindow.dismiss();
//                return false;
//            }
//        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpa(1);
            }
        });
    }

    //获取截图数据
    List<File> getScreenshot(){
        screenshot=new ArrayList<>();
        File file=new File("sdcard/signal record");
        if (!file.exists()){
            if(file.mkdir()){
                Log.d("file","已成功创建文件夹");
            }else {
                Log.d("file","未创建文件夹");
            }
        }else {
            File[] files=file.listFiles();
            int count=0;
            for (File tFile:files) {
                if(tFile.isFile()&&tFile.getName().endsWith(".png")) {
                    screenshot.add(count,tFile);
                    Log.d("file",tFile.getName());
                    count++;
                }
            }
        }
        //倒序排列，即为按修改时间排列
        Collections.reverse(screenshot);
        return screenshot;
    }

    //调整背景亮度
    private void setBackgroundAlpa(float alpa){
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=alpa;
        getWindow().setAttributes(lp);
    }
    //删除方法
    private void deleteRecord(){
        if(boxisDisplay){
            int delCount=0;
            for(int i=0;i<screenshot.size();i++){
                boolean value=mAdapter.getIsCheckMap().get(i);
                if(value){
                    screenshot.get(i).delete();
                    delCount++;
                }
            }
            if (delCount==0){
                util.CreateToast(getContext(),"请选择要删除的截图");
            } else{
                //更新listview,重新绑定适配器
                updateMyAdapter();
                util.CreateToast(getContext(),"已删除所选的"+delCount+"项");
                if(screenshot.size()==0){
                    tvNull.setVisibility(View.VISIBLE);
                }else{
                    tvNull.setVisibility(View.INVISIBLE);
                }
                boxisDisplay=false;
                bottomlayout.setVisibility(View.GONE);
                theline.setVisibility(View.GONE);
            }
        }
    }
    //取消方法
    private void cancelMethod(){
        bottomlayout.setVisibility(View.GONE);
        theline.setVisibility(View.GONE);
        for (int i = 0; i < screenshot.size(); i++) {
            mAdapter.getIsVisibleMap().put(i,CheckBox.GONE);
        }
        mAdapter.notifyDataSetChanged();
        boxisDisplay=false;
        if(!isall){
            btnallcheck.setBackgroundResource(R.drawable.allcheck);
            tvAll.setText("全选");
            for(int i=0;i<screenshot.size();i++){
                mAdapter.getIsCheckMap().put(i,false);
            }
            mAdapter.notifyDataSetChanged();
        }
        isall=true;
        pwDisplay=false;
    }

    //全选的方法
    private void allCheck(){
            if(isall){
                for(int i=0;i<screenshot.size();i++){
                    mAdapter.getIsCheckMap().put(i,true);
                }
                btnallcheck.setBackgroundResource(R.drawable.alluncheck);
                tvAll.setText("取消全选");
                isall=false;
            }else {
                for(int i=0;i<screenshot.size();i++){
                    mAdapter.getIsCheckMap().put(i,false);
                }
                btnallcheck.setBackgroundResource(R.drawable.allcheck);
                tvAll.setText("全选");
                isall=true;
            }
        mAdapter.notifyDataSetChanged();
    }

    //更新自定义Adapter
    private void updateMyAdapter(){
        mAdapter=new MyAdapter(getContext(),getScreenshot());
        listView.setAdapter(mAdapter);
    }
    private Context getContext(){
        return this;
    }

    //编辑图片方法
    private void editPicture(int position){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory("android.intent.category.DEFAULT");
            Uri uri = Uri.fromFile(screenshot.get(position));
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        }catch (ActivityNotFoundException e) {
            e.printStackTrace();
            util.CreateToast(this,"未安装程序");
        }
    }
    //分享图片
    private void sharePicture(int position){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri= Uri.fromFile(screenshot.get(position));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}



