package auth.aws.veechie.com.awsauth.samples;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Created by michaelbrennan on 6/14/15.
 */
public class GetAllSharedPreferences extends Activity{

    SharedPreferences prefs;

    public void getAllSharedPreferences(){
        prefs = this.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        Map<String,?> keys = prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()) {
            if (entry == null)
                continue;
            Log.i("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }
}
