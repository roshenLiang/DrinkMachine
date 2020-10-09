package com.uroica.drinkmachine.util.serialport;

import android.os.HandlerThread;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;

import com.uroica.drinkmachine.util.ChangeTool;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class SerialPortManager {

    private static final String TAG = "SerialPortManager";

    private SerialReadThread mReadThread;
    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;
    private Scheduler mSendScheduler;
    private OnSerialPortDataListener mOnSerialPortDataListener;

    /**
     * 添加数据通信监听
     *
     * @param listener listener
     * @return SerialPortManager
     */
    public void setOnSerialPortDataListener(OnSerialPortDataListener listener) {
        mOnSerialPortDataListener = listener;
    }
    private static class InstanceHolder {

        public static SerialPortManager sManager = new SerialPortManager();
    }

    public static SerialPortManager instance() {
        return InstanceHolder.sManager;
    }

    private SerialPort mSerialPort;

    private SerialPortManager() {
    }


    /**
     * 打开串口
     *
     * @param devicePath
     * @param baudrateString
     * @return
     */
    public boolean openSerialPort(String devicePath, String baudrateString) {
        if (mSerialPort != null) {
            closeSerialPort();
        }

        try {
            File device = new File(devicePath);
            int baurate = Integer.parseInt(baudrateString);
            mSerialPort = new SerialPort(device, baurate, 0);

            mReadThread = new SerialReadThread(mSerialPort.getInputStream());
            mReadThread.start();

            mOutputStream = mSerialPort.getOutputStream();

            mWriteThread = new HandlerThread("write-thread");
            mWriteThread.start();
            mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());

            return true;
        } catch (Throwable tr) {
            Log.e(TAG, "打开串口失败", tr);
            closeSerialPort();
            return false;
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        if (mReadThread != null) {
            mReadThread.close();
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mWriteThread != null) {
            mWriteThread.quit();
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        mOnSerialPortDataListener = null;
    }

    /**
     * 发送数据
     *
     * @param datas
     * @return
     */
    private void sendData(byte[] datas) throws Exception {
        mOutputStream.write(datas);
    }

    /**
     * (rx包裹)发送数据
     *
     * @param datas
     * @return
     */
    private Observable<Object> rxSendData(final byte[] datas) {

        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    sendData(datas);
                    emitter.onNext(new Object());
                } catch (Exception e) {
//                    Log.e(TAG,"发送：" + ChangeTool.ByteArrToHex(datas) + " 失败"+e.getMessage());
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                        return;
                    }
                }
                emitter.onComplete();
            }
        });
    }
    /**
     * 发送命令包
     */
    public void sendCommand(final String command) {

        // TODO: 2018/3/22  
//        Log.i(TAG,"发送命令：" + command);


        byte[] bytes = ChangeTool.hexStr2bytes(command);
        rxSendData(bytes).subscribeOn(mSendScheduler).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                
            }

            @Override
            public void onNext(Object o) {
//                LogManager.instance().post(new SendMessage(command));
//                Log.e(TAG,"发送：" +command+"成功");
            }

            @Override
            public void onError(Throwable e) {
//                Log.e(TAG,"发送失败"+command);
            }

            @Override
            public void onComplete() {

            }
        });
    }
    public void sendBytes(final byte[] bytes ) {
        // TODO: 2018/3/22
        if (null != mOnSerialPortDataListener) {
            mOnSerialPortDataListener.onDataSent(bytes);
        }
//        Log.i(TAG,"串口发送命令：" + ChangeTool.ByteArrToHex(bytes));
        rxSendData(bytes).subscribeOn(mSendScheduler).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(Object o) {
//                Log.e(TAG,"成功");
            }

            @Override
            public void onError(Throwable e) {
//                Log.e(TAG,"发送失败"+ChangeTool.ByteArrToHex(bytes));
            }

            @Override
            public void onComplete() {

            }
        });
    }


    public class SerialReadThread extends Thread {

        private static final String TAG = "SerialReadThread";

        private BufferedInputStream mInputStream;

        public SerialReadThread(InputStream is) {
            mInputStream = new BufferedInputStream(is);
        }

        @Override
        public void run() {
            byte[] received = new byte[1024];
            int size;

            Log.e(TAG,"开始读线程");

            while (true) {

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {

                    int available = mInputStream.available();

                    if (available > 0) {
                        size = mInputStream.read(received);
                        if (size > 0) {
                            onDataReceive(received, size);
                        }
                    } else {
                        // 暂停一点时间，免得一直循环造成CPU占用率过高
                        SystemClock.sleep(1);
                    }
                } catch (IOException e) {
                    Log.e(TAG,"读取数据失败", e);
                }
                //Thread.yield();
            }

            Log.e(TAG,"结束读进程");
        }

        /**
         * 处理获取到的数据
         *
         * @param received
         * @param size
         */
        private void onDataReceive(byte[] received, int size) {
            String hexStr = ChangeTool.bytes2HexStr(received, 0, size);
//            Log.i("rrrosh","接收到數據 hexStr="+hexStr);
            byte[] temp = new byte[size];
            System.arraycopy(received, 0, temp, 0, size);
            if (mOnSerialPortDataListener!=null) {
                mOnSerialPortDataListener.onDataReceived(temp);
            }
        }



        /**
         * 停止读线程
         */
        public void close() {

            try {
                mInputStream.close();
            } catch (IOException e) {
                Log.e("异常", e.getLocalizedMessage());
            } finally {
                super.interrupt();
            }
        }
    }


    ///------------新增

    public class Driver {
        public Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        private String mDriverName;
        private String mDeviceRoot;
        Vector<File> mDevices = null;

        public Vector<File> getDevices() {
            if (mDevices == null) {
                mDevices = new Vector<File>();
                File dev = new File("/dev");

                File[] files = dev.listFiles();

                if (files != null) {
                    int i;
                    for (i = 0; i < files.length; i++) {
                        if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
                            Log.d(TAG, "Found new device: " + files[i]);
                            mDevices.add(files[i]);
                        }
                    }
                }
            }
            return mDevices;
        }

        public String getName() {
            return mDriverName;
        }
    }


    private Vector<Driver> mDrivers = null;

    Vector<Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new Vector<Driver>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null) {
                // Issue 3:
                // Since driver name may contain spaces, we do not extract driver name with split()
                String drivername = l.substring(0, 0x15).trim();
                String[] w = l.split(" +");
                if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
                    Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length - 4]);
                    mDrivers.add(new Driver(drivername, w[w.length - 4]));
                }
            }
            r.close();
        }
        return mDrivers;
    }

    public String[] getAllDevices() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while (itdev.hasNext()) {
                    String device = itdev.next().getName();
                    String value = String.format("%s (%s)", device, driver.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    public String[] getAllDevicesPath() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while (itdev.hasNext()) {
                    String device = itdev.next().getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }


}
