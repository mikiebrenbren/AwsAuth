package auth.aws.veechie.com.awsauth.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import auth.aws.veechie.com.awsauth.R;
import auth.aws.veechie.com.awsauth.model.User;
import auth.aws.veechie.com.awsauth.utils.UsernameValidator;

/**
 * user should only see this class if it is the users first time signing in, they will set their username here for the first
 * time.  Any subsequent time will be set in the shared preferences.  This class goes to UserProfile, needs to killed when completed i.e. finish()
 */
public class UsernameActivity extends Activity implements View.OnClickListener {

    public final String TAG = this.getClass().getSimpleName();

    SharedPreferences prefs;
    TextView mUserNameText;
    EditText mUsernameEditText;
    Button mUsernameButton;

    /*
    TODO
    check to see if username is being used already and set username in the system already, if not add username to user table
    if username is taken toast and do nothing

    todo CREATE A NEW USER  HERE
    todo MAKE SURE TO THEN VALIDATE AGAINST THE DB TO MAKE SURE NO OTHER USERS HAVE THIS USERNAME
    todo IMPLEMENT TOAST ON ANY INVALID USERNAMES
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        mUserNameText = (TextView) findViewById(R.id.usernameActivityTextView);
        mUsernameEditText = (EditText) findViewById(R.id.usernameActivityEditText);
        mUsernameButton = (Button) findViewById(R.id.usernameActivityButton);

        Intent intent = getIntent();
        User newUser = intent.getParcelableExtra("newUser");
        Log.i(TAG, "This is the user email and joindate " + newUser.getEmail() + " " + newUser.getJoinDate());

        mUsernameButton.setOnClickListener(this);
//        SaveUserAsync saveUserAsync = new SaveUserAsync(this, mCognitoTasks.getCredentialsProvider());
//        saveUserAsync.execute(mUser);


    }

    @Override
    public void onClick(View v) {

        String potentialUsername = mUsernameEditText.getText().toString();
        if(new UsernameValidator().validate(potentialUsername)){
            //check db to see if user name is taken toast username is taken
            //todo if true move on to new activity
            //todo if false toast

        }else {
            //todo toast could be better, could be specific to length, and particular characters that are being entered
            Toast.makeText(
                    this,
                    "Try another username, \nUsernames can only contain '. _ -' characters and be between 3 and 16 characters long",
                    Toast.LENGTH_SHORT)
                    .show();
        }


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
