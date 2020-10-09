package com.uroica.drinkmachine.ui.fragment.ad;
 
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.DeviceUtils;
import com.uroica.drinkmachine.BR;
import com.uroica.drinkmachine.R;
import com.uroica.drinkmachine.bean.BannerModel;
import com.uroica.drinkmachine.databinding.FragmentAdBinding;
import com.uroica.drinkmachine.util.ChangeTool;
import com.uroica.drinkmachine.util.DownUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.goldze.mvvmhabit.base.BaseFragment;


public class AdFragment extends BaseFragment<FragmentAdBinding, AdViewModel> {

    //播放的视频
    private List<BannerModel.DataBean> playList;
    private String deviceID;
    private boolean isPlay = false;
    private Animation alphaAnimation;
    //当前播放index
    int playIndex = 0;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_ad;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        deviceID = DeviceUtils.getAndroidID();
        deviceID = ChangeTool.codeAddOne(deviceID, 20).toUpperCase();
        playList = new ArrayList<>();
        viewModel.getAd(deviceID);
        alphaAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha);

        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                            binding.videoView.setBackgroundColor(Color.TRANSPARENT);
                        return true;
                    }
                });

            }
        });
    }


    public  void updateAd(){
        viewModel.getAd(deviceID);
    }

    private int positionWhenPause = 0;//标记当视频暂停时的播放位置
    private boolean isPlayingWhenPause = false;//标记最小化或横竖屏时是否正在播放

    @Override
    public void onResume() {
        super.onResume();
        //如果有保存位置，则跳转到暂停时所保存的那个位置
        if (positionWhenPause > 0) {
            binding.videoView.seekTo(positionWhenPause);
            binding.videoView.start();
            //如果暂停前正在播放，则继续播放，并将播放位置置为0
            if (isPlayingWhenPause) {
                binding.videoView.start();
                binding.videoView.requestFocus();
                positionWhenPause = 0;
            }
        }
    }

    @Override
    public void onPause() {
        isPlayingWhenPause = binding.videoView.isPlaying();
        positionWhenPause = binding.videoView.getCurrentPosition();
        //停止回放视频文件，先获取再stopPlayback()
        binding.videoView.stopPlayback();
        super.onPause();
    }
    public synchronized void preparePlay(BannerModel.DataBean advertiseListBean) {
        if (!playList.contains(advertiseListBean)) {
            playList.add(advertiseListBean);
            if (!isPlay) {
                isPlay = true;
                startPlay();
            }
        }
    }

    private void startPlay() {
        //先判断ResourceListBean的时间段是否在当前之内
        //是的话才播放 不是就跳过
        BannerModel.DataBean advertiseListBean = playList.get(playIndex);
        boolean result = DownUtil.isInTime(advertiseListBean);
        Log.i("广告", "result="+result);
        if (result) {
        play(advertiseListBean);
        } else {
            next();
        }
    }

    /*
   播放
  */
    private void play(final BannerModel.DataBean advertiseListBean) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                binding.ivAdvertise.setVisibility(View.GONE);
                binding.videoView.setVisibility(View.GONE);
                if (advertiseListBean.getADType().equals("1")) {
                    Bitmap bitMap = BitmapFactory.decodeFile(DownUtil.doGetVideoFilePath(advertiseListBean.getURL()));
                    binding.ivAdvertise.setImageBitmap(bitMap);
                    binding.ivAdvertise.setVisibility(View.VISIBLE);
                    binding.ivAdvertise.startAnimation(alphaAnimation);//开始动画
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            next();
                        }
                    }, 8 * 1000);
                } else {
                    binding.videoView.setVideoPath(DownUtil.doGetVideoFilePath(advertiseListBean.getURL()));
                    binding.videoView.start();
                    binding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.i("广告", "视频播放完毕");
                            next();
                        }
                    });
                    binding.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            Log.i("广告", "setOnErrorListener 视频播放完毕");
                            next();
                            binding.videoView.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
                            return true;
                        }
                    });
                    binding.videoView.setVisibility(View.VISIBLE);
                    binding.videoView.startAnimation(alphaAnimation);//开始动画
                }
            }
        });
    }

    private void next() {
        playIndex++;
        BannerModel.DataBean advertiseListBean;
        try {
            advertiseListBean = playList.get(playIndex);
        } catch (Exception e) {
            //最后一个了 从头播放
            Log.i("广告", "最后一个了 从头播放");
            playIndex = 0;
            advertiseListBean = playList.get(playIndex);
        }
        Log.i("广告", "播放" + advertiseListBean.getURL());
        final BannerModel.DataBean finalResourceListBean = advertiseListBean;
        play(finalResourceListBean);
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.downCompleteEvent.observe(this, new Observer<BannerModel.DataBean>() {
            @Override
            public void onChanged(@Nullable BannerModel.DataBean dataBean) {
                //下载完成就开始播放
                Log.i("广告", "下载完成" + dataBean.getURL());
                preparePlay(dataBean);
            }
        });
    }
}
