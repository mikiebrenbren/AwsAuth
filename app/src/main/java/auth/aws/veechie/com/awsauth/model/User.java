package auth.aws.veechie.com.awsauth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by michaelbrennan on 5/23/15.
 */

@DynamoDBTable(tableName = "USER")
public class User implements Parcelable {

    private long joinDate;
    private String email;
    private String username;

    public User(){
    }

    private User(Parcel parcel){
        this.joinDate = parcel.readLong();
        this.email = parcel.readString();
        this.username = parcel.readString();
    }

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

    /*
    ------------------------------------------------------------------------------------------------------------------------------------------
    Parceable boilerplate
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(joinDate);
        parcel.writeString(email);
        parcel.writeString(username);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {

        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
