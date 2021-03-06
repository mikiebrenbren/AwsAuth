package auth.aws.veechie.com.awsauth.activity;

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

import auth.aws.veechie.com.awsauth.R;
import auth.aws.veechie.com.awsauth.application.CognitoTasks;
import auth.aws.veechie.com.awsauth.application.GoogleClientApp;
import auth.aws.veechie.com.awsauth.dynamodb.user.RetrieveUser;
import auth.aws.veechie.com.awsauth.model.User;
import auth.aws.veechie.com.awsauth.utils.RetrieveUserCallback;
import auth.aws.veechie.com.awsauth.utils.TimeStamp;


public class LoginActvity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>, View.OnClickListener, RetrieveUserCallback {

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

    private SharedPreferences.Editor editor;

    // Initialize the Amazon Cognito credentials provider
    protected CognitoCachingCredentialsProvider mCredentialsProvider;
    // Initialize the Cognito Sync client
    protected CognitoSyncManager mSyncClient;

    protected Dataset mDataset;
    private CognitoTasks mCognitoTasks;
    private SharedPreferences mSharedPreferences;
    private String mUsername;
    private User mUser;
    private Person mCurrentUser;
    private String mEmail;

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

        initializeCredentialsProvider();  //TODO COGNITO

        mCirclesList = new ArrayList<>();
        mCirclesAdapter = new ArrayAdapter<>(
                this, R.layout.circle_member, mCirclesList);

        if (savedInstanceState != null) {
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
        outState.putInt(SAVED_PROGRESS, ((GoogleClientApp) this.getApplication()).getmSignInProgress());
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
    * TODO this will have to be refactored for other authentication methods
    */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Reaching onConnected means we consider the user signed in.
        Log.i(TAG, "onConnected");

        // Retrieve some profile information to personalize our app for the user.
        mCurrentUser = Plus.PeopleApi.getCurrentPerson(((GoogleClientApp)this.getApplication()).getGoogleApiClient());
        Plus.PeopleApi.loadVisible(((GoogleClientApp)this.getApplication()).getGoogleApiClient(), null)
                .setResultCallback(this);

        /*
        if the user exists and has already signed in, then send directly to user profile, else send to username activity to
        choose username, so if user is not signed in but does have a user name, the async task below should retrieve user data to confirm
        user has a username, then send to UserProfile
         */
//        if(((GoogleClientApp)this.getApplication()).getmSignInProgress() == STATE_SIGN_IN) {
            //todo I don't think this is necessary after putting the google client at the application context
            editor = mSharedPreferences.edit();
            editor.putInt(getResources().getString(R.string.sign_in_progress_PREFKEY),  ((GoogleClientApp)this.getApplication()).getmSignInProgress()).apply();
            Log.i(TAG, "User is signed in");
//        }else {

            mEmail = Plus.AccountApi.getAccountName(((GoogleClientApp) this.getApplication()).getGoogleApiClient());
            Log.i(TAG, String.format(
                    getResources().getString(R.string.signed_in_as),
                    mCurrentUser.getDisplayName()) + "mEmail is " + mEmail);

        //checking to see if user is already in the database
        RetrieveUser retrieveUser = new RetrieveUser(this, mCognitoTasks.getCredentialsProvider());
        retrieveUser.execute(mEmail);

        /*
        TODO ---------------------------------------------------------------------------------------------------------------------
        need login here to verify that this is the users first time logging, if it is not send to UsernameActivity, otherwise
        send the user to UserProfileActivity, will make query to aws with mEmail, and search for a username, if username not there
        then go to Username Activity
        TODO ---------------------------------------------------------------------------------------------------------------------
         */
//        TODO REMOVE THIS, NEW USER SHOULD ONLY BE CREATED AFTER THE USERNAME ACTIVITY
//        SaveUserAsync saveUserAsync = new SaveUserAsync(this, mCognitoTasks.getCredentialsProvider());
//        saveUserAsync.execute(mUser);

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

    private void initializeCredentialsProvider(){
        Log.i(TAG, "Initializing cognito caching...");
        mCognitoTasks = new CognitoTasks();
        mCognitoTasks.init(this);
        mCredentialsProvider = mCognitoTasks.getCredentialsProvider();
    }

    @Override
    public void onUserRetrieved(User user) {
        Intent intent;
        if(user.getUsername() != null) {
            intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra(USER_NAME, mCurrentUser.getDisplayName());
            intent.putExtra(getResources().getString(R.string.sign_in_progress), ((GoogleClientApp)this.getApplication()).getmSignInProgress());
            startActivity(intent);
            finish();
        }else{
            if(user.getEmail() != null) {
                intent = new Intent(this, UsernameActivity.class);
                Log.i(TAG, "This is the user email and joindate being passed in the intent " + user.getEmail() + " " + user.getJoinDate());
                intent.putExtra("newUser", user);
                startActivity(intent);
            }else{
                user = new User();
                user.setJoinDate(new TimeStamp().stamp());
                user.setEmail(mEmail);
                Log.i(TAG, "This is the user email and joindate being passed in the intent" + user.getEmail() + " " + user.getJoinDate());
                intent = new Intent(this, UsernameActivity.class);
                intent.putExtra("newUser", user);
                startActivity(intent);
            }
        }

    }
}
