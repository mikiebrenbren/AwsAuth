package auth.aws.veechie.com.awsauth.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by michaelbrennan on 5/23/15.
 */

@DynamoDBTable(tableName = "USER")
public class User {

    private long joinDate;
    private String email;
    private String username;

    @DynamoDBHashKey(attributeName = "EMAIL")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBIndexHashKey(attributeName = "USERNAME")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBIndexRangeKey(attributeName = "DATE_JOINED")
    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long uid) {
        this.joinDate = uid;
    }
}
