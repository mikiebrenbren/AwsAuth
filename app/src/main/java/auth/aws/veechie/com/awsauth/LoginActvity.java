package auth.aws.veechie.com.awsauth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;

import auth.aws.veechie.com.awsauth.application.GoogleClientApp;


public class LoginActvity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>, View.OnClickListener{

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private static final int RC_SIGN_IN = 0;

public final String TAG = this.getClass().getSimpleName();

private static final String USER_NAME = "USER_NAME";
    // Used to store the error code most recently returned by Google Play services
// until the user clicks 'sign in'.
    private int mSignInError;
private static final String SAVED_PROGRESS = "sign_in_progress";

private SignInButton mGoogleSignInButton;
private Button mSignOutButton;
private Button mRevokeButton;
private TextView mStatus;
private ListView mCirclesListView;
private ArrayAdapter<String> mCirclesAdapter;
private ArrayList<String> mCirclesList;

protected TextView mCreateAccountTextView;
protected EditText mPasswordEditText;
protected EditText mEmailEditText;
protected Button mUsernameLoginButton;

private SharedPreferences mSharedPreferences;
private SharedPreferences.Editor editor;

// Initialize the Amazon Cognito credentials provider
protected CognitoCachingCredentialsProvider mCredentialsProvider;
// Initialize the Cognito Sync client
protected CognitoSyncManager mSyncClient;

protected Dataset mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_actvity);

        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.my_shared_preferences), Context.MODE_PRIVATE);
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mUsernameLoginButton = (Button) findViewById(R.id.username_login);
        mCreateAccountTextView = (TextView) findViewById(R.id.textViewCreateAccount);

        Intent intent = getIntent();
        if(intent.getIntExtra(getResources().getString(R.string.user_is_signed_out), -1) == 0){
            onSignedOut();
        }

//        initializeCredentialsProvider();  TODO COGNITO

        mCirclesList = new ArrayList<>();
        mCirclesAdapter = new ArrayAdapter<>(
                this, R.layout.circle_member, mCirclesList);

        if (savedInstanceState != null) { //TODO
            ((GoogleClientApp) this.getApplication()).setmSignInProgress(savedInstanceState.getInt(SAVED_PROGRESS, 0));
        }

        // Button listeners
        mGoogleSignInButton.setOnClickListener(this);
        mCreateAccountTextView.setOnClickListener(this);


        ((GoogleClientApp) this.getApplication()).setGoogleApiClient(buildGoogleApiClient());
        
    }

    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN);

        return builder.build();
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }

    @Override
    protected void onStop() {
        super.onStop();

        if (((GoogleClientApp)this.getApplication()).getGoogleApiClient().isConnected()) {
            ((GoogleClientApp)this.getApplication()).getGoogleApiClient().disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, ((GoogleClientApp)this.getApplication()).getmSignInProgress());
    }

    @Override
    public void onClick(View v) {
        if (!((GoogleClientApp)this.getApplication()).getGoogleApiClient().isConnecting()) {
            // We only process button clicks when GoogleApiClient is not transitioning
            // between connected and not connected.
            switch(v.getId()) {
                case R.id.google_sign_in_button:
//                    mStatus.setText(R.string.status_signing_in);
                    ((GoogleClientApp)this.getApplication()).setmSignInProgress(STATE_SIGN_IN);
                    ((GoogleClientApp)this.getApplication()).getGoogleApiClient().connect();
                    break;
                case R.id.textViewCreateAccount:
                    Log.i(TAG, "Create account text view has been pressed");
                    Intent intent = new Intent(this, NewUserActivity.class);
                    startActivity(intent);
            }
        }
    }

    /* onConnected is called when our Activity successfully connects to Google
    * Play services.  onConnected indicates that an account was selected on the
    * device, that the selected account has granted any requested permissions to
    * our app and that we were able to establish a service connection to Google
    * Play services.
    */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Reaching onConnected means we consider the user signed in.
        Log.i(TAG, "onConnected");

        // Update the user interface to reflect that the user is signed in.
//        mGoogleSignInButton.setEnabled(false); TODO may set this to false
//        getTokenForCognito();//TODO COGNITO
//        cognitoSyncInitialize();
//        Log.d(TAG, " my ID is: " + mCredentialsProvider.getIdentityId());//todo aws stuff


        // Retrieve some profile information to personalize our app for the user.
        Person currentUser = Plus.PeopleApi.getCurrentPerson(((GoogleClientApp)this.getApplication()).getGoogleApiClient());

        Log.i(TAG, String.format(
                getResources().getString(R.string.signed_in_as),
                currentUser.getDisplayName()));

        //TODO send this information to the new successful login activity
//        mStatus.setText(String.format(
//                getResources().getString(R.string.signed_in_as),
//                currentUser.getDisplayName()));

        Plus.PeopleApi.loadVisible(((GoogleClientApp)this.getApplication()).getGoogleApiClient(), null)
                .setResultCallback(this);

        if(((GoogleClientApp)this.getApplication()).getmSignInProgress() == STATE_SIGN_IN) {
            //todo I don't think this is necessary after putting the google client at the application context
            editor = mSharedPreferences.edit();
            editor.putInt(getResources().getString(R.string.sign_in_progress_PREFKEY),  ((GoogleClientApp)this.getApplication()).getmSignInProgress()).apply();
            Log.i(TAG, "User is signed in");
            Intent intent = new Intent(this, UserProfile.class);
            intent.putExtra(USER_NAME, currentUser.getDisplayName());
            intent.putExtra(getResources().getString(R.string.sign_in_progress), ((GoogleClientApp)this.getApplication()).getmSignInProgress());
            startActivity(intent);
            finish();
        }

        // Indicate that the sign in process is complete.
//        mSignInProgress = STATE_DEFAULT;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.
            Log.w(TAG, "API Unavailable.");
        } else if (((GoogleClientApp)this.getApplication()).getmSignInProgress() != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            ((GoogleClientApp)this.getApplication()).setmSignInIntent(result.getResolution());
            mSignInError = result.getErrorCode();

            if (((GoogleClientApp)this.getApplication()).getmSignInProgress() == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
                resolveSignInError();
            }
        }
    }

    /* Starts an appropriate intent or dialog for user interaction to resolve
     * the current error preventing the user from being signed in.  This could
     * be a dialog allowing the user to select an account, an activity allowing
     * the user to consent to the permissions being requested by your app, a
     * setting to enable device networking, etc.
     */
    private void resolveSignInError() {
        if (((GoogleClientApp)this.getApplication()).getmSignInIntent() != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                ((GoogleClientApp)this.getApplication()).setmSignInProgress(STATE_IN_PROGRESS);
                startIntentSenderForResult(((GoogleClientApp)this.getApplication()).getmSignInIntent().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, "Sign in intent could not be sent: "
                        + e.getLocalizedMessage());
                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                ((GoogleClientApp)this.getApplication()).setmSignInProgress(STATE_SIGN_IN);
                ((GoogleClientApp)this.getApplication()).getGoogleApiClient().connect();
            }
        } else {
            // Google Play services wasn't able to provide an intent for some
            // error types, so we show the default Google Play services error
            // dialog which may still start an intent on our behalf if the
            // user can resolve the issue.
            createErrorDialog().show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // If the error resolution was successful we should continue
                    // processing errors.
                    ((GoogleClientApp)this.getApplication()).setmSignInProgress(STATE_SIGN_IN);
                } else {
                    // If the error resolution was not successful or the user canceled,
                    // we should stop processing errors.
                    ((GoogleClientApp)this.getApplication()).setmSignInProgress(STATE_DEFAULT);
                }


                if (!((GoogleClientApp)this.getApplication()).getGoogleApiClient().isConnecting()) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
                    ((GoogleClientApp)this.getApplication()).getGoogleApiClient().connect();
                }
                break;
        }
    }
    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            mCirclesList.clear();
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    mCirclesList.add(personBuffer.get(i).getDisplayName());
                }
            } finally {
                personBuffer.close();
            }

            mCirclesAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Error requesting visible circles: " + peopleData.getStatus());
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        ((GoogleClientApp)this.getApplication()).getGoogleApiClient().connect();
    }

    private Dialog createErrorDialog() {
        if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    mSignInError,
                    this,
                    RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.e(TAG, "Google Play services resolution cancelled");
                            ((GoogleClientApp)LoginActvity.this.getApplication()).setmSignInProgress(STATE_DEFAULT);
                            mStatus.setText(R.string.status_signed_out);
                        }
                    });
        } else {
            return new AlertDialog.Builder(this)
                    .setMessage(R.string.play_services_error)
                    .setPositiveButton(R.string.close,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e(TAG, "Google Play services error could not be "
                                            + "resolved: " + mSignInError);
                                    ((GoogleClientApp)LoginActvity.this.getApplication()).setmSignInProgress(STATE_DEFAULT);
                                    mStatus.setText(R.string.status_signed_out);
                                }
                            }).create();
        }
    }

    //TODO may need to sign out other preferences from Amazon
    private void onSignedOut() {
        // Update the UI to reflect that the user is signed out.
        mGoogleSignInButton.setEnabled(true);
//        mGoogleApiClient.disconnect();
        ((GoogleClientApp)this.getApplication()).setmSignInProgress(STATE_DEFAULT);

        Log.i(TAG, getResources().getString(R.string.status_signed_out));

        if (mCirclesList != null) {
            mCirclesList.clear();
            mCirclesAdapter.notifyDataSetChanged();
        }
    }

//    private void getTokenForCognito(){
//
//        GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
//        AccountManager am = AccountManager.get(this);
//        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
//        String token = null;
//        try {
//            token = GoogleAuthUtil.getToken(getApplicationContext(), accounts[0].name,
//                    "audience:server:client_id:"+getResources().getString(R.string.coginito_client_id));
//        } catch (IOException | GoogleAuthException e) {
//            e.printStackTrace();
//        }
//        Map<String, String> logins = new HashMap<>();
//        logins.put(getResources().getString(R.string.login_accounts_google_com), token);
//        mCredentialsProvider.setLogins(logins);
//    }
//
//    private void cognitoSyncInitialize(){
//        mSyncClient = new CognitoSyncManager(
//                this,
//                Regions.US_EAST_1, // Region
//                mCredentialsProvider);
//        // Create a record in a mDataset and synchronize with the server
//        mDataset = mSyncClient.openOrCreateDataset("myDataset");
//        mDataset.put("awsMyTestKey", "awsAuthTestValue");
//        mDataset.synchronize(new DefaultSyncCallback() {
//            @Override
//            public void onSuccess(Dataset dataset, List newRecords) {
//                Log.i(TAG, "Successful cognito sync");
//            }
//        });
//    }
//
//    private void initializeCredentialsProvider(){
//        mCredentialsProvider = new CognitoCachingCredentialsProvider(
//                this, // Context
//                getResources().getString(R.string.identity_pool_id), // Identity Pool ID
//                Regions.US_EAST_1 // Region
//        );
//    }

}
