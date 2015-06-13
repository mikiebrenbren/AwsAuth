package auth.aws.veechie.com.awsauth.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import auth.aws.veechie.com.awsauth.R;

/**
 * user should only see this class if it is the users first time signing in, they will set their username here for the first
 * time.  Any subsequent time will be set in the shared preferences.  This class goes to UserProfile, needs to killed when completed i.e. finish()
 */
public class UsernameActivity extends Activity {


    TextView mUserNameText;
    EditText mUsernameEditText;
    Button mUsernameButton;

    /*
    TODO
    check to see if username is being used already and set username in the system already, if not add username to user table
    if username is taken toast and do nothing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        mUserNameText = (TextView) findViewById(R.id.usernameActivityTextView);
        mUsernameEditText = (EditText) findViewById(R.id.usernameActivityEditText);
        mUsernameButton = (Button) findViewById(R.id.usernameActivityButton);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_username, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
