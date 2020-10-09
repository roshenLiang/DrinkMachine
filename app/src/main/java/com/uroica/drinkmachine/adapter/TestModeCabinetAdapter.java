package com.uroica.drinkmachine.adapter;
 
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.uroica.drinkmachine.R;

public class TestModeCabinetAdapter extends RecyclerView.Adapter<RecyclerHolder> {
    private int size;
    private Context context;

    public TestModeCabinetAdapter(Context context, int size) {
        this.context = context;
        this.size = size;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cabinet_view, null);
        //自定义view的宽度，控制一屏显示个数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int width = context.getResources().getDisplayMetrics().widthPixels;
        params.width = width / 3;
        view.setLayoutParams(params);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        //

    }

    @Override
    public int getItemCount() {
        return size;
    }
}

class RecyclerHolder extends RecyclerView.ViewHolder {
    private View view;

    public RecyclerHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public View getView() {
        return view;
    }
}
