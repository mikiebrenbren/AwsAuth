package auth.aws.veechie.com.awsauth.utils;

import android.content.Context;

/**
 * Created by michaelbrennan on 5/18/15.
 */
public class StringUtil {

    public static String getFromStringsXml(Context context, int key) {
        return String.valueOf(context.getResources().getString(key));
    }
}
