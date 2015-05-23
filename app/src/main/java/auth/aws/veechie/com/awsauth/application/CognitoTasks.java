package auth.aws.veechie.com.awsauth.application;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auth.aws.veechie.com.awsauth.R;

/**
 * Created by michael.brennan on 5/5/15.
 */
public class CognitoTasks extends ApplicationGlobal {

    private CognitoCachingCredentialsProvider mCredentialsProvider;
    private String mToken;

    public void init(Context context){
        new InitializeCredentialsProvider().execute(context);
    }

    public CognitoCachingCredentialsProvider getCredentialsProvider(){
        return mCredentialsProvider;
    }

    /**
     * Private inner Async task used to initialize cognito Provider
     */
    private class InitializeCredentialsProvider extends AsyncTask<Context, Void, CognitoCachingCredentialsProvider> {
        @Override
        protected CognitoCachingCredentialsProvider doInBackground(Context... contexts) {

            GooglePlayServicesUtil.isGooglePlayServicesAvailable(contexts[0].getApplicationContext());
            AccountManager am = AccountManager.get(contexts[0]);
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String clientId = "audience:server:client_id:" + contexts[0].getResources().getString(R.string.google_client_id);
            mCredentialsProvider = new CognitoCachingCredentialsProvider(
                    contexts[0], // Context
                    contexts[0].getResources().getString(R.string.identity_pool_id), // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );
            try {
                CognitoTasks.this.mToken = GoogleAuthUtil.getToken(contexts[0].getApplicationContext(),
                        accounts[0].name,
                        clientId);
            } catch (IOException | GoogleAuthException e) {
                e.printStackTrace();
            }
            Map<String, String> logins = new HashMap<>();
            logins.put(contexts[0].getResources().getString(R.string.login_accounts_google_com), CognitoTasks.this.mToken);
            CognitoTasks.this.mCredentialsProvider.setLogins(logins);
//            syncCognito(contexts[0], mCredentialsProvider);
            Log.i("Token", mToken);
            Log.i("LogTag", "my ID is " + mCredentialsProvider.getIdentityId());
            return mCredentialsProvider;
        }
        @Override
        protected void onPostExecute(CognitoCachingCredentialsProvider aVoid) {
            super.onPostExecute(aVoid);
            mCredentialsProvider = aVoid;
        }
    }

    public void syncCognito(Context context,
                            CognitoCachingCredentialsProvider credentialsProvider

    )
    {
        // Initialize the Cognito Sync client
        CognitoSyncManager syncClient = new CognitoSyncManager(
                context,
                Regions.US_EAST_1, // Region
                credentialsProvider);

        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
        dataset.put("myKey", "myValue");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {
                //Your handler code here
            }
        });
    }
}

