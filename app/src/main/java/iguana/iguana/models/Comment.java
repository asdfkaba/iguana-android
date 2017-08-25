package iguana.iguana.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by moritz on 07.05.17.
 */

public class Comment {

        @SerializedName("issue")
        @Expose
        private String issue;
        @SerializedName("creator")
        @Expose
        private String creator;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("seqnum")
        @Expose
        private Integer seqnum;
        @SerializedName("when")
        @Expose
        private String when;
        @SerializedName("modified")
        @Expose
        private String modified;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("attachment")
        @Expose
        private Object attachment;

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getSeqnum() {
            return seqnum;
        }

        public void setSeqnum(Integer seqnum) {
            this.seqnum = seqnum;
        }

        public String getWhen() {
            return when;
        }

        public void setWhen(String when) {
            this.when = when;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Object getAttachment() {
            return attachment;
        }

        public void setAttachment(Object attachment) {
            this.attachment = attachment;
        }

}
