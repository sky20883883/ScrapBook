package com.lotus.scrapbook.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lotus.scrapbook.R;
import com.lotus.scrapbook.data.MarkData;
import com.lotus.scrapbook.recycler.MarkRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SQLiteControl {
    public static void loadScrap(Context context) {
        DialogHelper.WaitProgressDialog(context,"讀取資料","讀取資料中，請稍後...");
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SQLiteHelper sqLiteHelper=new SQLiteHelper(context);
                SQLiteDatabase sql=sqLiteHelper.getWritableDatabase();
                String cmd="select * from markinfo";
                Cursor cursor=sql.rawQuery(cmd,null);
                ArrayList<MarkData> arrayList=new ArrayList<>();
                while (cursor.moveToNext()){
                    String id = cursor.getString(0);
                    String title = cursor.getString(1);
                    String msg = cursor.getString(2);
                    msg=msg.replace(System.getProperty("line.separator").toString(), "");
                    if (msg.length()>20)msg=msg.substring(0,10)+"...";
                    String time = cursor.getString(3);
                    String check=cursor.getString(4);
                    if (check.equals("true")){
                        msg="此項目被密碼保護，無法在此顯示";
                    }
                    String password=cursor.getString(5);
                    arrayList.add(new MarkData(title,msg,time,check,password));
                }
                cursor.close();
                sqLiteHelper.close();
                DialogHelper.waitProgressDialog.dismiss();
                setRecyclerView(arrayList,context);
            }
        });
    }
    public static void setRecyclerView(ArrayList<MarkData> arrayList,Context context) {
        DialogHelper.WaitProgressDialog(context,"顯示資料","顯示資料中，請稍後...");
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Collections.reverse(arrayList);
                RecyclerView recyclerView=((Activity)context).findViewById(R.id.recycler_scrap);
                MarkRecyclerViewAdapter recyclerViewAdapter;
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerViewAdapter = new MarkRecyclerViewAdapter(context, arrayList);
                recyclerView.setAdapter(recyclerViewAdapter);
                DialogHelper.waitProgressDialog.dismiss();
            }
        });
    }
    public static void updateScrap(String title,String msg,String date,Context context){
        String[] strings=findInput(date,context);
        title=strings[0];
        msg=strings[1];
        String check=strings[2];
        String pwd=strings[3];
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog=inflater.inflate(R.layout.dialog_add,null);
        Button okBtn=dialog.findViewById(R.id.dialog_add_btn);
        Button notBtn=dialog.findViewById(R.id.dialog_back_btn);
        TextView textView=dialog.findViewById(R.id.dialog_title_textview);
        textView.setText("修改內容");
        okBtn.setText("修改");
        EditText titleEditText=dialog.findViewById(R.id.dialog_title_edittext);
        EditText msgEditText=dialog.findViewById(R.id.dialog_msg_edittext);
        CheckBox checkBox=dialog.findViewById(R.id.dialog_add_passowrd);
        EditText password=dialog.findViewById(R.id.dialog_add_passowrd_edixt);
        LinearLayout layout=dialog.findViewById(R.id.dialog_add_passowrd_layout);
        if (check.equals("true"))checkBox.setChecked(true);
        if (checkBox.isChecked()){layout.setVisibility(View.VISIBLE);password.setText(pwd);}
        titleEditText.setText(title);
        msgEditText.setText(msg);
        builder.setView(dialog).setCancelable(false);
        AlertDialog showDialog=builder.create();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    layout.setVisibility(View.VISIBLE);
                }
                else {
                    layout.setVisibility(View.GONE);
                    password.setText("");
                }
            }
        });
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
                String m=msgEditText.getText().toString();
                if (!m.trim().equals("")){
                    if (checkBox.isChecked()){
                        if (!password.getText().toString().trim().equals("")){
                            update(titleEditText.getText().toString(),m,date,"true",password.getText().toString().trim(),context);
                        }else {
                            update(titleEditText.getText().toString(),m,date,"false","",context);
                            Toast.makeText(context,"密碼因為為空，因此改變為不需要密碼",Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        update(titleEditText.getText().toString(),m,date,"false","",context);
                    }
                    showDialog.dismiss();
                }else {
                    Toast.makeText(context,"內容不可為空",Toast.LENGTH_LONG).show();
                    showDialog.dismiss();
                }
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
    public static String[] findInput(String date, Context context) {
        String[] strings=new String[4];
        SQLiteHelper sqLiteHelper=new SQLiteHelper(context);
        SQLiteDatabase sql=sqLiteHelper.getWritableDatabase();

        String cmd="select * from markinfo where created_at='"+date+"'";
        Cursor cursor=sql.rawQuery(cmd,null);
        while (cursor.moveToNext()){
            strings[0]=cursor.getString(1);//標題
            strings[1]=cursor.getString(2);//內容
            strings[2]=cursor.getString(4);//檢查密碼
            strings[3]=cursor.getString(5);//密碼
            break;
        }
        cursor.close();
        sqLiteHelper.close();
        DialogHelper.waitProgressDialog.dismiss();
        return strings;
    }
    public static void update(String title,String msg,String old_date,String check,String pwd,Context context){
        DialogHelper.WaitProgressDialog(context,"更新資料","更新資料中，請稍後...");
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SQLiteHelper sqLiteHelper=new SQLiteHelper(context);
                SQLiteDatabase sql=sqLiteHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("title",title);
                values.put("inupt",msg);
                Date date = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                values.put("created_at",dateFormat.format(date));
                values.put("check_pwd",check);
                values.put("password",pwd);
                sql.update("markinfo",values,"created_at='"+old_date+"'",null);
                sqLiteHelper.close();
                DialogHelper.waitProgressDialog.dismiss();
                loadScrap(context);
            }
        });
    }
    public static void deleteScrap(String date, Context context) {
        DialogHelper.WaitProgressDialog(context,"刪除資料","刪除資料中，請稍後...");
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SQLiteHelper sqLiteHelper=new SQLiteHelper(context);
                SQLiteDatabase sql=sqLiteHelper.getWritableDatabase();
                sql.delete("markinfo","created_at='"+date+"'",null);
                sqLiteHelper.close();
                DialogHelper.waitProgressDialog.dismiss();
                loadScrap(context);
            }
        });
    }
    public static void addScrap(Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog=inflater.inflate(R.layout.dialog_add,null);
        Button okBtn=dialog.findViewById(R.id.dialog_add_btn);
        Button notBtn=dialog.findViewById(R.id.dialog_back_btn);
        EditText title=dialog.findViewById(R.id.dialog_title_edittext);
        EditText msg=dialog.findViewById(R.id.dialog_msg_edittext);
        CheckBox checkBox=dialog.findViewById(R.id.dialog_add_passowrd);
        EditText password=dialog.findViewById(R.id.dialog_add_passowrd_edixt);
        LinearLayout layout=dialog.findViewById(R.id.dialog_add_passowrd_layout);
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
                layout.setVisibility(View.VISIBLE);
            }
            else {
                layout.setVisibility(View.GONE);
                password.setText("");
            }

        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m=msg.getText().toString();
                if (!m.trim().equals("")){
                    if (checkBox.isChecked()){
                        if (!password.getText().toString().trim().equals("")){
                            saveScrap(title.getText().toString(),msg.getText().toString(),"true",password.getText().toString().trim(),context);
                        }else {
                            saveScrap(title.getText().toString(),msg.getText().toString(),"false","",context);
                            Toast.makeText(context,"密碼因為為空，因此改變為不需要密碼",Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        saveScrap(title.getText().toString(),msg.getText().toString(),"false","",context);
                    }
                    showDialog.dismiss();
                }else {
                    Toast.makeText(context,"內容不可為空",Toast.LENGTH_LONG).show();
                    showDialog.dismiss();
                }
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
    public static void saveScrap(String title,String msg,String check,String pwd,Context context){
        DialogHelper.WaitProgressDialog(context,"儲存資料","儲存資料中，請稍後...");
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SQLiteHelper sqLiteHelper=new SQLiteHelper(context);
                SQLiteDatabase sql=sqLiteHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("title",title);
                values.put("inupt",msg);
                Date date = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                values.put("created_at",dateFormat.format(date));
                values.put("check_pwd",check);
                values.put("password",pwd);
                sql.insert("markinfo",null,values);
                sqLiteHelper.close();
                DialogHelper.waitProgressDialog.dismiss();
                loadScrap(context);
            }
        });
    }
}
