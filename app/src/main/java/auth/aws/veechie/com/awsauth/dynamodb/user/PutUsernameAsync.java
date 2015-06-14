package auth.aws.veechie.com.awsauth.dynamodb.user;

import android.os.AsyncTask;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

import java.util.HashMap;

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
        try{
            HashMap<String, AttributeValue> primaryKey = new HashMap<>();
            AttributeValue email = new AttributeValue()
                    .withS(users[0].getEmail());
            primaryKey.put("EMAIL", email);

            UpdateItemRequest request = new UpdateItemRequest()
                    .withTableName("USERS")
                    .withKey(primaryKey)
                    .addAttributeUpdatesEntry(
                            "USERNAME", new AttributeValueUpdate()
                                        .withValue(new AttributeValue().withN(mPotentialUsername))
                                        .withAction(AttributeAction.PUT))
                    .addExpectedEntry(
                            "USERNAME", new ExpectedAttributeValue()
                                        .withValue()
                    )

                    )

        }catch(ConditionalCheckFailedException e){

        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean flag) {
        super.onPostExecute(flag);
    }
}
