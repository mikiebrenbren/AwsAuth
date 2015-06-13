package auth.aws.veechie.com.awsauth.utils;

import auth.aws.veechie.com.awsauth.model.User;

/**
 * Created by michaelbrennan on 6/13/15.
 */
public interface RetrieveUserCallback {

    void onUserRetrieved(User user);

}
