package auth.aws.veechie.com.awsauth.utils;

import android.util.Log;

import java.sql.Timestamp;

/**
 * Created by michaelbrennan on 6/7/15.
 */
public class TimeStamp {
    public final String TAG = this.getClass().getSimpleName();

    public long stamp(){
        java.util.Date date= new java.util.Date();
        long timestamp = new Timestamp(date.getTime()).getTime();
        Log.i(TAG, String.valueOf(timestamp));
        return timestamp;
    }
}
