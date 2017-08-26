package iguana.iguana.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Issue implements Parcelable{

    private int mData;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }
    public static final Parcelable.Creator<Issue> CREATOR
            = new Parcelable.Creator<Issue>() {

        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    private Issue(Parcel in) {
        mData = in.readInt();
    }

    private boolean isSelected = false;


    @SerializedName("project")
    @Expose
    private String project;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("assignee")
    @Expose
    private List<String> assignee = null;
    @SerializedName("participant")
    @Expose
    private List<String> participant = null;
    @SerializedName("dependsOn")
    @Expose
    private List<String> dependsOn = null;
    @SerializedName("kanbancol")
    @Expose
    private String kanbancol;
    @SerializedName("sprint")
    @Expose
    private String sprint;
    @SerializedName("logged_total")
    @Expose
    private String loggedTotal;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("storypoints")
    @Expose
    private Integer storypoints;
    @SerializedName("was_in_sprint")
    @Expose
    private Boolean wasInSprint;
    @SerializedName("archived")
    @Expose
    private Boolean archived;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getAssignee() {
        return assignee;
    }

    public void setAssignee(List<String> assignee) {
        this.assignee = assignee;
    }

    public List<String> getParticipant() {
        return participant;
    }

    public void setParticipant(List<String> participant) {
        this.participant = participant;
    }

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getKanbancol() {
        return kanbancol;
    }

    public void setKanbancol(String kanbancol) {
        this.kanbancol = kanbancol;
    }

    public String getSprint() {
        return sprint;
    }

    public void setSprint(String sprint) {
        this.sprint = sprint;
    }

    public String getLoggedTotal() {
        return loggedTotal;
    }

    public void setLoggedTotal(String loggedTotal) {
        this.loggedTotal = loggedTotal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStorypoints() {
        return storypoints;
    }

    public void setStorypoints(Integer storypoints) {
        this.storypoints = storypoints;
    }

    public Boolean getWasInSprint() {
        return wasInSprint;
    }

    public void setWasInSprint(Boolean wasInSprint) {
        this.wasInSprint = wasInSprint;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getProjectShortName() { return this.getUrl().split("/")[5];}

    public void toggleSelected() { this.isSelected = !this.isSelected; };

    public boolean isSelected() { return this.isSelected; };


}