package auth.aws.veechie.com.awsauth.application;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import auth.aws.veechie.com.awsauth.R;

/**
 * Created by michael.brennan on 5/5/15.
 */
public class CognitoAsyncTasks extends ApplicationGlobal {

    private CognitoCachingCredentialsProvider mCredentialsProvider;
    private String identityId;

    public void init(Context context){
        new InitializeCredentialsProvider().execute(context);
    }

    public CognitoCachingCredentialsProvider getCredentialsProvider(){
        return mCredentialsProvider;
    }

    public String getIdentity(){
        if (identityId == null){
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    identityId = mCredentialsProvider.getIdentityId();
                    return null;
                }
            }.execute();
        }
        return identityId;
    }

    /**
     * Private inner Async task used to initialize cognito Provider
     */
    private class InitializeCredentialsProvider extends AsyncTask<Context, Void, CognitoCachingCredentialsProvider> {
        @Override
        protected CognitoCachingCredentialsProvider doInBackground(Context... params) {

            String s = "test";
            return new CognitoCachingCredentialsProvider(
                    params[0], // Context
                    params[0].getResources().getString(R.string.identity_pool_id), // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );
        }
        @Override
        protected void onPostExecute(CognitoCachingCredentialsProvider aVoid) {
            super.onPostExecute(aVoid);
            mCredentialsProvider = aVoid;
        }
    }

}

