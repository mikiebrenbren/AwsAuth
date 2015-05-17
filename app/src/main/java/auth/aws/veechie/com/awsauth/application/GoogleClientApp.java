package auth.aws.veechie.com.awsauth.application;

import android.app.Application;
import android.app.PendingIntent;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by michaelbrennan on 5/7/15.
 */
public class GoogleClientApp extends Application{

    // GoogleApiClient wraps our service connection to Google Play services and
// provides access to the users sign in state and Google's APIs.
    private GoogleApiClient mGoogleApiClient;

    // We use mSignInProgress to track whether user has clicked sign in.
// mSignInProgress can be one of three values:
//
//       STATE_DEFAULT: The default state of the application before the user
//                      has clicked 'sign in', or after they have clicked
//                      'sign out'.  In this state we will not attempt to
//                      resolve sign in errors and so will display our
//                      Activity in a signed out state.
//       STATE_SIGN_IN: This state indicates that the user has clicked 'sign
//                      in', so resolve successive errors preventing sign in
//                      until the user has successfully authorized an account
//                      for our app.
//   STATE_IN_PROGRESS: This state indicates that we have started an intent to
//                      resolve an error, and so we should not start further
//                      intents until the current intent completes.
    private int mSignInProgress = 0;//todo this is automatically set to state_default because user profile is the main activity and if user is not signed in it must send the user back to the login page this logic may need to be changed

    // Used to store the PendingIntent most recently returned by Google Play
    // services until the user clicks 'sign in'.
    private PendingIntent mSignInIntent;

    public PendingIntent getmSignInIntent() {
        return mSignInIntent;
    }

    public void setmSignInIntent(PendingIntent mSignInIntent) {
        this.mSignInIntent = mSignInIntent;
    }

    public int getmSignInProgress() {
        return mSignInProgress;
    }

    public void setmSignInProgress(int mSignInProgress) {
        this.mSignInProgress = mSignInProgress;
    }


    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

}
