package com.you.signaldemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-10-28.
 */
public class MyUtil {
    public void CreateToast(Context context,String string){
        Toast toast=new Toast(context);
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.toast_view,null);
        TextView textView= (TextView) view.findViewById(R.id.toast_tv);
        textView.setText(string);
        toast.setView(view);
        toast.show();
    }
}
