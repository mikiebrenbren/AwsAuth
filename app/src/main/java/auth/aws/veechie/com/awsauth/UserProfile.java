package auth.aws.veechie.com.awsauth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import auth.aws.veechie.com.awsauth.application.GoogleClientApp;


public class UserProfile extends ActionBarActivity {

    public final String TAG = this.getClass().getSimpleName();
    protected TextView mSuccessFullLoginMessage;
    protected SharedPreferences mSharedPreferences;
    protected int mIsUserSignedIn;
    protected SharedPreferences.Editor editor;
    protected static boolean isFirstTimeDisplayNamePopulated = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.my_shared_preferences), Context.MODE_PRIVATE);
        mIsUserSignedIn = ((GoogleClientApp)this.getApplication()).getmSignInProgress();
        ((GoogleClientApp) this.getApplication()).getmSignInProgress();
        Log.i(TAG + " this is the signInProgress taken from application context", String.valueOf(((GoogleClientApp) this.getApplication()).getmSignInProgress()));
        String s = String.valueOf(getIntent().getIntExtra(getResources().getString(R.string.sign_in_progress), -1));//TODO
        Log.i(TAG + "this is the msigninprogress integer", s);

        if(mIsUserSignedIn != 1){
            Log.i(TAG, "Sending back to login...");
            Intent intent = new Intent(this, LoginActvity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_user_profile);

            //TODO FIX THIS, MAY HAVE TO PUT THIS DISPLAY NAME IN SHARED PREFS
            if(isFirstTimeDisplayNamePopulated){
                Log.i(TAG, "inside first time display name populated conditional block");
                Intent intent = getIntent();
                String displayName = intent.getStringExtra("USER_NAME");
                editor = mSharedPreferences.edit();
                editor.putString(getResources().getString(R.string.display_name_PREFKEY), displayName).apply();
                isFirstTimeDisplayNamePopulated = false;
            }
            Log.i(TAG, mSharedPreferences.getString(getResources().getString(R.string.display_name_PREFKEY), "ERROR MESSAGE"));
            mSuccessFullLoginMessage = (TextView) findViewById(R.id.successful_login_textView);
            mSuccessFullLoginMessage.setText(String.format(
                    getResources().getString(R.string.user_profile_text_view),
                    mSharedPreferences.getString(getResources().getString(R.string.display_name_PREFKEY),"ERROR")
            ));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings:
                return true;
            case R.id.logout_settings:
                Log.i(TAG + " Signing out...", String.valueOf(mSharedPreferences.getInt(getResources().getString(R.string.sign_in_progress_PREFKEY),0)));
                Intent intent = new Intent(this, LoginActvity.class);
                intent.putExtra(getResources().getString(R.string.user_is_signed_out), 0);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
