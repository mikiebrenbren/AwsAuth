package auth.aws.veechie.com.awsauth.samples;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import auth.aws.veechie.com.awsauth.model.User;

/**
 * Created by michaelbrennan on 6/7/15.
 */
public class BookAsync extends AsyncTask <Book, Void, Void>{

    private AmazonDynamoDBClient mAmazonDynamoDBClient;
    private DynamoDBMapper mDynamoDBMapper;
    private Context mContext;
    private CognitoCredentialsProvider mCognitoCredentialsProvider;

    public BookAsync(Context context, CognitoCredentialsProvider cognitoCredentialsProvider){
        mContext = context;
        mCognitoCredentialsProvider = cognitoCredentialsProvider;
    }

    @Override
    protected Void doInBackground(Book... books) {
        mAmazonDynamoDBClient = new AmazonDynamoDBClient(mCognitoCredentialsProvider);
        mDynamoDBMapper = new DynamoDBMapper(mAmazonDynamoDBClient);
        mDynamoDBMapper.save(books[0]);
        return null;
    }
}
