package auth.aws.veechie.com.awsauth.dynamodb.user;

import android.os.AsyncTask;
import auth.aws.veechie.com.awsauth.model.User;
import auth.aws.veechie.com.awsauth.utils.callback.PutUsernameCallback;

/**
 * Created by michaelbrennan on 6/14/15.
 */
public class PutUsernameAsync extends AsyncTask<User, Void, Boolean> {

    private PutUsernameCallback mUsernameCallback;
    private String mPotentialUsername;

    public PutUsernameAsync(PutUsernameCallback usernameCallback, String potentialUsername){
        mUsernameCallback = usernameCallback;
        mPotentialUsername = potentialUsername;
    }
    @Override
    protected Boolean doInBackground(User... users) {
        /*
        todo check if username attribute is already being used in a user table
        todo if it is already return false
        todo otherwise the user will update and return true
         */
//        PutItemRequest putItemRequest = new PutItemRequest().withTableName("User")
//                .withItem
        return null;
    }

    @Override
    protected void onPostExecute(Boolean flag) {
        super.onPostExecute(flag);
    }
}
