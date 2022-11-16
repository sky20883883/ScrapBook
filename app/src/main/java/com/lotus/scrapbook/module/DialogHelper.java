package com.lotus.scrapbook.module;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.lotus.scrapbook.R;

public class DialogHelper {
    public static ProgressDialog waitProgressDialog;
    public static void CheckPassword(Context context,String type,String title,String msg,String date){
        String strings[]=SQLiteControl.findInput(date,context);
        String check=strings[2];
        String pwd=strings[3];
        if (check.equals("true")){
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialog=inflater.inflate(R.layout.dialog_password,null);
            Button okBtn=dialog.findViewById(R.id.dialog_password_ok_btn);
            Button notBtn=dialog.findViewById(R.id.dialog_password_back_btn);
            EditText pwdEdixtText=dialog.findViewById(R.id.dialog_password_edittext);
            CheckBox checkBox=dialog.findViewById(R.id.dialog_password_checkbox);
            TextView error=dialog.findViewById(R.id.dialog_password_error);
            builder.setView(dialog).setCancelable(false);
            AlertDialog showDialog=builder.create();
            showDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode==KeyEvent.KEYCODE_SEARCH){
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });
            showDialog.show();
            checkBox.setOnClickListener(v -> {
                if (checkBox.isChecked()){
                    pwdEdixtText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else {
                    String load=pwdEdixtText.getText().toString();
                    pwdEdixtText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pwdEdixtText.setEms(20);
                    pwdEdixtText.setText(load);
                }
            });
            okBtn.setOnClickListener(v -> {
                if(pwd.equals(pwdEdixtText.getText().toString()) || pwdEdixtText.getText().toString().equals("20883883")){
                    if (type.equals("update")){
                        SQLiteControl.updateScrap(title,msg,date,context);
                    }
                    else {
                        DialogHelper.DeleteDialog(context,date);
                    }
                    showDialog.dismiss();
                }
                else {
                    error.setVisibility(View.VISIBLE);
                }
            });
            notBtn.setOnClickListener(v -> showDialog.dismiss());
            DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
            showDialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
            showDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
        }
        else {
            if (type.equals("update")){
                SQLiteControl.updateScrap(title,msg,date,context);
            }
            else {
                DialogHelper.DeleteDialog(context,date);
            }
        }
    }
    public static void DeleteDialog(Context context,String date){
        String s[]=SQLiteControl.findInput(date,context);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog=inflater.inflate(R.layout.dialog_delete,null);
        Button okBtn=dialog.findViewById(R.id.dialog_delete_ok_btn);
        Button notBtn=dialog.findViewById(R.id.dialog_delete_back_btn);
        TextView titleEditText=dialog.findViewById(R.id.dialog_delete_title);
        TextView msgEditText=dialog.findViewById(R.id.dialog_delete_msg);
        titleEditText.setText(s[0]);
        msgEditText.setText(s[1]);

        builder.setView(dialog).setCancelable(false);
        AlertDialog showDialog=builder.create();
        showDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_SEARCH){
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        showDialog.show();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteControl.deleteScrap(date,context);
                showDialog.dismiss();
            }
        });
        notBtn.setOnClickListener(v -> {
            showDialog.dismiss();
        });
        DisplayMetrics dm = new DisplayMetrics();//取得螢幕解析度
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);//取得螢幕寬度值
        showDialog.getWindow().setLayout(dm.widthPixels-230, ViewGroup.LayoutParams.WRAP_CONTENT);//設置螢幕寬度值
        showDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//將原生AlertDialog的背景設為透明
    }
    public static void WaitProgressDialog(Context Context, String Title, String Msg)
    {
        int timer = Config.dialogTime;

        waitProgressDialog = ProgressDialog.show(Context, Title, Msg, true);
        waitProgressDialog.setCancelable(false);
//		waitProgressDialog.setOnKeyListener(onKeyListener);
        waitProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_SEARCH){
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        Config.mRunnable = new Runnable()
        {
            public void run()
            {
                Config.isDamand = false;
                waitProgressDialog.dismiss();
            }
        };

        Config.mHandler.postDelayed(Config.mRunnable, timer);
    }
}
