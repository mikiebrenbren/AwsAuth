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


public class UserProfile extends ActionBarActivity {

    public final String TAG = this.getClass().getSimpleName();
    protected TextView mSuccessFullLoginMessage;
    protected SharedPreferences mSharedPreferences;
    protected int mIsUserSignedIn;
    protected String userNamePreferences;
    protected SharedPreferences.Editor editor;
    protected static String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.my_shared_preferences), Context.MODE_PRIVATE);
        mIsUserSignedIn = mSharedPreferences.getInt(getResources().getString(R.string.sign_in_progress_PREFKEY), -1);
        Log.i(TAG + " this is the shared preferences", String.valueOf(mSharedPreferences.getInt(getResources().getString(R.string.sign_in_progress_PREFKEY), -1)));
        String s = String.valueOf(getIntent().getIntExtra(getResources().getString(R.string.sign_in_progress), -1));//TODO
        Log.i(TAG + "this is the msigninprogress integer", s);

        if(mIsUserSignedIn != 1){
            Log.i(TAG, "Sending back to login...");
            Intent intent = new Intent(this, LoginActvity.class);
            intent.putExtra( String.valueOf(mSharedPreferences.getInt(getResources().getString(R.string.sign_in_progress_PREFKEY), -1)), getResources().getString(R.string.sign_in_progress));
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_user_profile);

            //TODO FIX THIS, MAY HAVE TO PUT THIS DISPLAY NAME IN SHARED PREFS
            displayName = null;
            Intent intent = getIntent();
            if(intent.getStringExtra("USER_NAME") != null) {
                displayName = intent.getStringExtra("USER_NAME");
                Log.i(TAG, displayName);
            }

            mSuccessFullLoginMessage = (TextView) findViewById(R.id.successful_login_textView);
            mSuccessFullLoginMessage.setText(String.format(
                    getResources().getString(R.string.user_profile_text_view),
                    displayName
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
                Log.i(TAG + " This is the tag", String.valueOf(mSharedPreferences.getInt(getResources().getString(R.string.sign_in_progress_PREFKEY),0)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
