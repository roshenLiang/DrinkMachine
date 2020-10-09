package com.uroica.drinkmachine.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
  
import com.uroica.drinkmachine.bean.BannerModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class DownUtil {


    public static final int DOWNLOAD_FAIL = 0;
    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_SUCCESS = 2;
    public static final int DOWNLOAD_ALLSUCCESS = 3;
    private static DownUtil downloadUtil;
    private final OkHttpClient okHttpClient;


    private String defaultPath = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();

    public static DownUtil getInstance() {
        if (downloadUtil == null) {
            downloadUtil = new DownUtil();
        }
        return downloadUtil;
    }

    private DownUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * url 地址
     * saveDir 保存地址
     */
    public synchronized void download(final BannerModel.DataBean advertiseListBean, final OnDownloadListener listener) {
        this.listener = listener;
        final String url = advertiseListBean.getURL();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = DOWNLOAD_FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                //储存下载文件的目录
                String savePath = isExistDir(defaultPath);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中
                        Message message = Message.obtain();
                        message.what = DOWNLOAD_PROGRESS;
                        message.obj = progress;
                        mHandler.sendMessage(message);

                    }
                    fos.flush();
                    //下载完成
                    Message message = Message.obtain();
                    message.what = DOWNLOAD_SUCCESS;
                    message.obj = advertiseListBean;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = DOWNLOAD_FAIL;
                    mHandler.sendMessage(message);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {

                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }
                }
            }
        });
    }

    private String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_PROGRESS:
                    listener.onDownloading((Integer) msg.obj);
                    break;
                case DOWNLOAD_FAIL:
                    listener.onDownloadFailed();
                    break;
                case DOWNLOAD_SUCCESS:
                    listener.onDownloadSuccess((BannerModel.DataBean) msg.obj);
                    break;
            }
        }
    };
    OnDownloadListener listener;

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(BannerModel.DataBean advertiseListBean);

        /**
         * 下载进度
         *
         * @param progress
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
        /**
         * 全部下载成功
         */
//        void onDownloadComplete();
    }

    public static List<String> getFileList(String strPath) {
        List<String> fileList = new ArrayList<>();
        File dir = new File(strPath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件夹
        if (files == null)
            return null;
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                fileList.add(files[i].getName());//对于文件才把它的路径加到filelist中
            }
        }
        return fileList;
    }

    /**
     * 根据map返回key和value的list
     *
     * @param map
     * @return
     */
    public static List<String> getListByMap(Map<String, BannerModel.DataBean> map) {
        List<String> list = new ArrayList<String>();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            list.add(key);
        }
        return list;
    }
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 获取视频文件路径
     *
     * @param url
     * @return
     */
    public static String doGetVideoFilePath(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        return "/mnt/sdcard/Download/" + fileName;
    }

    /**
     * 删除单个文件
     *
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String url) {

        String fileName = doGetVideoFilePath(url);
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.i("广告", "删除单个文件" + fileName + "成功！");
//                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                Log.i("广告", "删除单个文件" + fileName + "失败！");
//                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            Log.i("广告", "删除单个文件失败：" + fileName + "不存在！");
//            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 获取指定文件大小
     *
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) {
        long size = 0;
        FileInputStream fis = null;
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                size = fis.available();
            }
            if (fis != null)
                fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static boolean isInTime(BannerModel.DataBean advertiseListBean) {
        long currentDate = System.currentTimeMillis();
        long startData=TimeUtil.string2Millis(advertiseListBean.getUploadTime());
        long endData=TimeUtil.string2Millis(advertiseListBean.getExpDate());
        if (startData < currentDate && currentDate < endData) {
                Log.i("广告", "时间内");
                return true;
        } else {
            //超过最大时间后要删除该视频
            //未检查
            Log.i("广告", "不在时间播放范围");
//            DownUtil.deleteFile(advertiseListBean.getURL());
            return false;
        }
    }

}
