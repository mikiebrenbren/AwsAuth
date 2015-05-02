package auth.aws.veechie.com.awsauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class UserProfile extends ActionBarActivity {

    public final String TAG = this.getClass().getSimpleName();
    protected TextView mSuccessFullLoginMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        String displayName = intent.getStringExtra("USER_NAME");
        Log.i(TAG, displayName);

        mSuccessFullLoginMessage = (TextView) findViewById(R.id.successful_login_textView);
        mSuccessFullLoginMessage.setText(String.format(
                getResources().getString(R.string.user_profile_text_view),
                displayName
        ));

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
