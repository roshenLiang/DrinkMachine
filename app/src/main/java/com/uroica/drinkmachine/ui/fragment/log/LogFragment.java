package com.uroica.drinkmachine.ui.fragment.log;
 
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.adapter.LogAdapter;
import com.uroica.drinkmachine.bean.LogMessage;
import com.uroica.drinkmachine.databinding.FragmentAdBinding;
import com.uroica.drinkmachine.databinding.FragmentLogBinding;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;


public class LogFragment extends BaseFragment<FragmentLogBinding, LogViewModel> {
    RecyclerView ryLog;
    LogAdapter adapter;
    private List<LogMessage> logMessageList;
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_log;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        ryLog = binding.ryLog;
        // 设置布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        ryLog.setLayoutManager(linearLayoutManager);
        logMessageList = new ArrayList<>();
        adapter = new LogAdapter(getActivity(), logMessageList);
        ryLog.setAdapter(adapter);
        binding.btnClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clearAllLogMessage();
                updateAdapter();
            }
        });
    }

    private void updateAdapter() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    ryLog.scrollToPosition(adapter.getItemCount() - 1);
                }
            });
        }catch (Exception e){

        }

    }

    public void sendData(String data){
        adapter.addLogMessage(new LogMessage(data, true, System.currentTimeMillis()));
        updateAdapter();
    }
    public void receiveData(String data){
        adapter.addLogMessage(new LogMessage(data, false, System.currentTimeMillis()));
        updateAdapter();
    }
    public void clearAllData(){
        adapter.clearAllLogMessage();
        updateAdapter();
    }
}
