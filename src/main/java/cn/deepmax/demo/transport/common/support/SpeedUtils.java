package cn.deepmax.demo.transport.common.support;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class SpeedUtils {


    public static BigDecimal getCostSeconds(long now, long createTime){
        long costMill = now - createTime;
        BigDecimal s = BigDecimal.valueOf(costMill).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.HALF_UP);
        return s;
    }

    public static BigDecimal getPercentage(long index, long contentLength){
        if( index == -1){
            return BigDecimal.ZERO;
        }
        BigDecimal decimal = BigDecimal.valueOf(index*100.0/contentLength);
        return decimal.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getSpeedKB(long index, long now, long createTime){
        if(index == -1 ){
            return BigDecimal.ZERO;
        }
        long costMill = now - createTime;   //ms

        Double dSpeed = index*1000.0/costMill/1024;
        if(dSpeed.isInfinite() || dSpeed.isNaN()){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(dSpeed).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * speed string  KB/s  MB/s
     * @return
     */
    public static String getSpeedString(long index, long now, long createTime){
        BigDecimal decimal = getSpeedKB(index, now, createTime);
        if(decimal.intValue()> 2000){
            BigDecimal mbSpeed = decimal.divide(BigDecimal.valueOf(1024L));
            mbSpeed = mbSpeed.setScale(2, RoundingMode.HALF_UP);
            return mbSpeed+" MB/s";
        }else{
            return decimal+" KB/s";
        }
    }
}
