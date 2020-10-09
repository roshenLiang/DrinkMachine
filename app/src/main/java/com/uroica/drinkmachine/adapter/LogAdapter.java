package com.uroica.drinkmachine.adapter;
 
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.LogMessage;

import java.text.SimpleDateFormat;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<MyHolder> {
    private Context context;
    private List<LogMessage> mList;

    public LogAdapter(Context context, List<LogMessage> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void addLogMessage(LogMessage logMessage) {
        mList.add(logMessage);
    }

    public void clearAllLogMessage() {
        mList.clear();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_log, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        //
        LogMessage logMessage = mList.get(position);
        String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logMessage.getCurrentTime());
        String isSend;
        if (logMessage.isToSend()) {
            isSend = "   发送数据：   ";
            holder.tv_log.setTextColor(Color.BLUE);
        } else {
            isSend = "   接收数据：   ";
            holder.tv_log.setTextColor(Color.RED);
        }
        holder.tv_log.setText(timeNow + isSend + logMessage.getMessage());
        holder.tv_num.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

class MyHolder extends RecyclerView.ViewHolder {
    TextView tv_num, tv_log;

    public MyHolder(View itemView) {
        super(itemView);
        tv_num = itemView.findViewById(R.id.tv_num);
        tv_log = itemView.findViewById(R.id.tv_log);
    }

}
