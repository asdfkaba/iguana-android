package iguana.iguana.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by moritz on 07.05.17.
 */

public class Token implements Parcelable {

    private int mData;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }
    public static final Parcelable.Creator<Token> CREATOR
            = new Parcelable.Creator<Token>() {

        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    private Token(Parcel in) {
        mData = in.readInt();
    }

    @SerializedName("token")
    @Expose
    private String token;
    private String refresh_token;

    public String getToken() {
        return token;
    }
    public String getRefreshToken() {
        return refresh_token;
    }


    public void setToken(String token) {
        this.token = token;
    }
    public void setRefresh_token(String token) {
        this.refresh_token = token;
    }

}
