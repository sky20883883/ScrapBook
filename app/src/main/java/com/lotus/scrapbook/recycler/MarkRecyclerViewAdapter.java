package com.lotus.scrapbook.recycler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lotus.scrapbook.R;
import com.lotus.scrapbook.data.MarkData;
import com.lotus.scrapbook.module.DialogHelper;
import com.lotus.scrapbook.module.SQLiteControl;

import java.util.ArrayList;

public class MarkRecyclerViewAdapter extends RecyclerView.Adapter<MarkRecyclerViewAdapter.ViewHolder>{
    ArrayList<MarkData> listdata;
    Context context;

    public MarkRecyclerViewAdapter(Context context,ArrayList<MarkData> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_scrap,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarkData data=listdata.get(position);
        holder.title.setText(data.getTitle());
        holder.msg.setText(data.getScrap());
        holder.date.setText(data.getCreate_time());
        String title,msg,date;
        title=data.getTitle();
        msg=data.getScrap();
        date=data.getCreate_time();
        if (msg.equals("此項目被密碼保護，無法在此顯示"))holder.msg.setTextColor(context.getResources().getColor(R.color.red));
        holder.changeBtn.setOnClickListener(v -> {
            DialogHelper.CheckPassword(context,"update",title,msg,date);
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.CheckPassword(context,"delete",title,msg,date);
            }
        });
        holder.layout.setOnClickListener(v -> {
            String[] s= SQLiteControl.findInput(date,context);
            String t=s[0];
            String m=s[1];
            ClipboardManager clipboard = (ClipboardManager) ((Activity)context).getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(null,m);
            clipboard.setPrimaryClip(clipData);
            Toast.makeText(context, "已複製: "+t, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button changeBtn,deleteBtn;
        TextView title,msg,date;
        LinearLayout layout;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        changeBtn=itemView.findViewById(R.id.recycler_scrap_change_btn);
        deleteBtn=itemView.findViewById(R.id.recycler_scrap_delete_btn);
        title=itemView.findViewById(R.id.recycler_scrap_title_text);
        msg=itemView.findViewById(R.id.recycler_scrap_msg_text);
        date=itemView.findViewById(R.id.recycler_scrap_time_text);
        layout=itemView.findViewById(R.id.recycler_scrap_layout);
    }
}
}
