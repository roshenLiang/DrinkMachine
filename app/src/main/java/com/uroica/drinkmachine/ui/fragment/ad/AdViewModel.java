package com.uroica.drinkmachine.ui.fragment.ad;
 
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.uroica.drinkmachine.bean.BannerModel;
import com.uroica.drinkmachine.rxnetwork.RetrofitHelper;
import com.uroica.drinkmachine.util.DownUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class AdViewModel extends BaseViewModel {
    String defaultPath = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent downCompleteEvent = new SingleLiveEvent<>();
    }

    public AdViewModel(@NonNull Application application) {
        super(application);
    }

    public void getAd(final String deviceID) {
        RetrofitHelper.getAdAPI().getAD(deviceID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BannerModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BannerModel bannerModel) {
                        if(bannerModel.getData()!=null&&bannerModel.getData().size()>0){
                            downFile(bannerModel);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getAd( deviceID);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void downFile(BannerModel bannerModel) {
        //本地缓存集合
        List<String> fileListName = DownUtil.getFileList(defaultPath);
        //网络集合
        Map<String, BannerModel.DataBean> netMap = new HashMap<>();
        for (int i = 0; i < bannerModel.getData().size(); i++) {// BannerModelEvent.DataBean.AdvertiseListBean advertiseListBean : bannerModel.getData().getResource_list()
            BannerModel.DataBean advertiseListBean = bannerModel.getData().get(i);
            String netFileName = advertiseListBean.getURL().substring(advertiseListBean.getURL().lastIndexOf('/') + 1);
            netMap.put(netFileName, advertiseListBean);
        }
        Set<String> keys = netMap.keySet();// 得到网络全部的文件夹的名字
        List<String> netFileList = DownUtil.getListByMap(netMap);//SET转LIST

        for (int i = 0; i < fileListName.size(); i++) {
            if (!netFileList.contains(fileListName.get(i))) {
                Log.i("广告", "情况3 本地有 网络无" + fileListName.get(i));
                //删除本地视频
                DownUtil.deleteFile(fileListName.get(i));
            }
        }
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            String netFileName = iter.next();
            if (fileListName.contains(netFileName)) {//如果本地集合中有网络,请求下载集合移除对象
                Log.i("广告", "情况1 本地有 网络有");
                //判断大小是否一样 如果一样移除下载 不一样删除文件重新下载
                File file = new File(DownUtil.doGetVideoFilePath(netFileName));
                //本地文件大小
                long size = DownUtil.getFileSize(file);

                BannerModel.DataBean advertiseListBean = netMap.get(netFileName);
                Log.i("广告", "本地文件大小netFileName=" + netFileName + "==size:" + size);
                if (Long.valueOf(advertiseListBean.getFileSize()) != size) {
                    //删除本地文件 重新下载
                    Log.i("广告", "删除本地文件 重新下载");
                    DownUtil.deleteFile(advertiseListBean.getURL());
                    downFiles(advertiseListBean);
                } else {
                    //添加到播放列表
                    Log.i("广告", "已下载 添加到播放列表");
                    uc.downCompleteEvent.postValue(advertiseListBean);
//                    uc.downCompleteEvent.call2();
                }
            } else {
                Log.i("广告", "情况2 本地没 网络有");
                //下载该对象到本地
                BannerModel.DataBean advertiseListBean = netMap.get(netFileName);
                downFiles(advertiseListBean);
            }
        }

    }
//
//    private void loadFile(BannerModel.DataBean dataBean) {
//        String url=dataBean.getURL();
//        final String destFileName = url.substring(url.lastIndexOf('/') + 1);
//        DownLoadManager.getInstance().load(url, new ProgressCallBack<ResponseBody>(defaultPath, destFileName) {
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onCompleted() {
////                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onSuccess(ResponseBody responseBody) {
////
////                try {
////                    Log.i("广告", "下载完成"+ responseBody.string());
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                uc.downCompleteEvent.postValue(advertiseListBean);
////                uc.downCompleteEvent.call2();
//
////                Log.i("roshen","文件下载完成！" + destFileName);
////                downIndex++;
////                if (downIndex < downFileLists.size()) {
////                    downFile(downFileLists.get(downIndex));
////                }else{
////                    Log.i("roshen","全部下载完成");
////                    uc.downCompleteEvent.call();
////                }
////                ToastUtils.showShort("文件下载完成！" + destFileName);
//            }
//
//            @Override
//            public void progress(final long progress, final long total) {
////                progressDialog.setMax((int) total);
////                progressDialog.setProgress((int) progress);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
////                ToastUtils.showShort("文件下载失败！");
////                progressDialog.dismiss();
//            }
//        });
//    }

    public synchronized void downFiles(BannerModel.DataBean advertiseListBean) {
        DownUtil.getInstance().download(advertiseListBean, new DownUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(BannerModel.DataBean advertiseListBean) {
//                Log.i("广告", "下载完成"+ advertiseListBean.getURL());
                uc.downCompleteEvent.postValue(advertiseListBean);
//                uc.downCompleteEvent.call2();
            }

            @Override
            public void onDownloading(int progress) {
            }

            @Override
            public void onDownloadFailed() {
            }
        });
    }

}
