package auth.aws.veechie.com.awsauth.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by michaelbrennan on 6/13/15.
 */
public class UsernameValidator{

    private Pattern pattern;
    private Matcher matcher;

    private static final String USERNAME_PATTERN = "^[a-z0-9._-]{3,16}$";

    public UsernameValidator(){
        pattern = Pattern.compile(USERNAME_PATTERN);
    }

    /**
     * Validate username with regular expression
     * @param username username for validation
     * @return true valid username, false invalid username
     */
    public boolean validate(final String username){

        matcher = pattern.matcher(username);
        return matcher.matches();

    }
}
