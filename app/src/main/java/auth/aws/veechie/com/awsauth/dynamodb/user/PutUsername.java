package auth.aws.veechie.com.awsauth.dynamodb.user;

import android.os.AsyncTask;

import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

import auth.aws.veechie.com.awsauth.model.User;

/**
 * Created by michaelbrennan on 6/14/15.
 */
public class PutUsername extends AsyncTask<User, Void, Void> {
    @Override
    protected Void doInBackground(User... users) {
        /*
        todo check if username attribute is already being used in a user table
        todo if it is already return false
        todo otherwise the user will update and return true
         */
//        PutItemRequest putItemRequest = new PutItemRequest().withTableName("User")
//                .withItem
        return null;
    }
}
