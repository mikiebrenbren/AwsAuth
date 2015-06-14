package auth.aws.veechie.com.awsauth.dynamodb.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import auth.aws.veechie.com.awsauth.model.User;
import auth.aws.veechie.com.awsauth.utils.RetrieveUserCallback;

/**
 * Created by michaelbrennan on 6/8/15.
 *
 */
public class RetrieveUser extends AsyncTask<String, Void, User> {

    public final String TAG = this.getClass().getSimpleName();
    private DynamoDBMapper mDynamoDBMapper;
    private AmazonDynamoDBClient mAmazonDynamoDBClient;
    private Context mContext;
    private CognitoCredentialsProvider mCognitoCredentialsProvider;
    private SharedPreferences mSharedPreferences;
    private RetrieveUserCallback mRetrieveUserCallback;

    public RetrieveUser(RetrieveUserCallback context, CognitoCredentialsProvider cognitoCredentialsProvider){
        mCognitoCredentialsProvider = cognitoCredentialsProvider;
        mRetrieveUserCallback = context;
    }

    @Override
    protected User doInBackground(String... emails) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(mCognitoCredentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        return  mapper.load(User.class, emails[0]);
    }

    protected void onPostExecute(User user){
        mRetrieveUserCallback.onUserRetrieved(user);
    }
}
