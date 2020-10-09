package com.uroica.drinkmachine.util.crc;
 
import android.util.Log;

import static com.uroica.drinkmachine.util.crc.Crc16.Crc16Modbus;

public class CrcHelper {
    static long ReverseBits(long ul, int valueLength) {
        long newValue = 0;
        for (int i = valueLength - 1; i >= 0; i--) {
            newValue |= (ul & 1) << i;
            ul >>= 1;
        }
        return newValue;
    }

    public static long Check(byte[] bytes) {

        CrcCalculator calculator = new CrcCalculator(Crc16Modbus);
        long cal_L = calculator.Calc(bytes, 0, bytes.length);
        //高低位互换
        //未输出 ，直接用crc16util
        // 合并byte[]，并返回
        byte[] newByte = new byte[bytes.length + 1];
        Log.i("roshen","新的byte[]长度="+newByte.length);
        System.arraycopy(bytes,0,newByte,0,bytes.length);

        return 0;
    }
}
