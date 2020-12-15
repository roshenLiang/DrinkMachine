package com.uroica.drinkmachine.ui.fragment.main

import android.app.Application
import me.goldze.mvvmhabit.base.BaseViewModel

class MainViewModel : BaseViewModel<MainModel>{
    constructor(application: Application) : super(application) {}
    constructor(application: Application, model: MainModel) : super(application, model) {}
}