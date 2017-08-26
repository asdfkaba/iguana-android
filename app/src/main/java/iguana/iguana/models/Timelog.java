package iguana.iguana.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by moritz on 07.05.17.
 */

public class Timelog implements Parcelable {
    private int mData;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }
    public static final Parcelable.Creator<Timelog> CREATOR
            = new Parcelable.Creator<Timelog>() {

        public Timelog createFromParcel(Parcel in) {
            return new Timelog(in);
        }

        public Timelog[] newArray(int size) {
            return new Timelog[size];
        }
    };

    private Timelog(Parcel in) {
        mData = in.readInt();
    }

    private boolean isSelected = false;

        @SerializedName("number")
        @Expose
        private Integer number;
        @SerializedName("issue")
        @Expose
        private String issue;
        @SerializedName("user")
        @Expose
        private String user;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("time")
        @Expose
        private String time;
    @SerializedName("url")
    @Expose
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

    public void toggleSelected() { this.isSelected = !this.isSelected; };

    public boolean isSelected() { return this.isSelected; };

    public String getNameShort() {
        String [] split = getUrl().split("/");
        return split[split.length-5];
    }
    public int getIssueNumber() {
        String [] split = getUrl().split("/");
        return Integer.parseInt(split[split.length-3]);
    }

    }
