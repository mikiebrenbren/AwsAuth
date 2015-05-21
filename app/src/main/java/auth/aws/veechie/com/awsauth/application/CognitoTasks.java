package auth.aws.veechie.com.awsauth.application;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import auth.aws.veechie.com.awsauth.R;

/**
 * Created by michael.brennan on 5/5/15.
 */
public class CognitoTasks extends ApplicationGlobal {

    private CognitoCachingCredentialsProvider mCredentialsProvider;
    private String identityId;
    private String mToken;
    private Account[] accounts;
    private String clientId;

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

    public void getTokenForCognito(final Context context){

        GooglePlayServicesUtil.isGooglePlayServicesAvailable(context.getApplicationContext());
        AccountManager am = AccountManager.get(context);
        accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        mToken = null;
        clientId = "audience:server:client_id:" + context.getResources().getString(R.string.google_client_id);
        new AsyncTask<Context, Void, String>(){
                @Override
                protected String doInBackground(Context... contexts) {

                    try {
                            mToken = GoogleAuthUtil.getToken(contexts[0].getApplicationContext(),
                                     accounts[0].name,
                                     clientId);
                    } catch (IOException | GoogleAuthException e) {
                        Log.i("EXCEPTION", "Message:" + e.getMessage() + "\nCause:" + e.getCause());
                        e.printStackTrace();
                    }
                    return mToken;
                }
                @Override
                protected void onPostExecute(String token){
                    CognitoTasks.this.mToken = token;
                }
            }.execute(context);

        Map<String, String> logins = new HashMap<>();
        logins.put(context.getResources().getString(R.string.login_accounts_google_com), mToken);
        mCredentialsProvider.setLogins(logins);
        Log.i("CREDZ", clientId);
        Log.i("Logins", mCredentialsProvider.getLogins().get("login.accounts.google.com") + "is a string");
    }

}

