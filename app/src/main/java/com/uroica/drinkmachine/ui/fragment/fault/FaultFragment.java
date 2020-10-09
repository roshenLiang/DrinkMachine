package com.uroica.drinkmachine.ui.fragment.fault;
 
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.databinding.FragmentFaultBinding;
import com.uroica.drinkmachine.databinding.FragmentShopBinding;

import me.goldze.mvvmhabit.base.BaseFragment;


public class FaultFragment extends BaseFragment<FragmentFaultBinding, FaultViewModel> {


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_fault;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
    }



}
