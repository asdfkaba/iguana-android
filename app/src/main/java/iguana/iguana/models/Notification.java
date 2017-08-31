package iguana.iguana.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by moritz on 30.08.17.
 */

public class Notification {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("issue")
        @Expose
        private String issue;
        @SerializedName("user")
        @Expose
        private String user;
        @SerializedName("type")
        @Expose
        private List<String> type = null;

        private boolean isSelected = false;


    public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
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

        public List<String> getType() {
            return type;
        }

        public void setType(List<String> type) {
            this.type = type;
        }

    public void toggleSelected() { this.isSelected = !this.isSelected; };

    public boolean isSelected() { return this.isSelected; };

}

