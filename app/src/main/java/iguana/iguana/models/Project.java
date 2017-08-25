package iguana.iguana.models;

/**
 * Created by moritz on 07.05.17.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Project implements Parcelable {
    private int mData;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }
    public static final Parcelable.Creator<Project> CREATOR
            = new Parcelable.Creator<Project>() {

        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    /** recreate object from parcel */
    private Project(Parcel in) {
        mData = in.readInt();
    }

    @SerializedName("manager")
    @Expose
    private List<String> manager = null;
    @SerializedName("developer")
    @Expose
    private List<String> developer = null;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("kanbancol")
    @Expose
    private List<String> kanbancol = null;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_short")
    @Expose
    private String nameShort;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("activity_only_for_managers")
    @Expose
    private Boolean activityOnlyForManagers;
    @SerializedName("currentsprint")
    @Expose
    private Integer currentsprint;

    public List<String> getManager() {
        return manager;
    }

    public void setManager(List<String> manager) {
        this.manager = manager;
    }

    public List<String> getDeveloper() {
        return developer;
    }

    public void setDeveloper(List<String> developer) {
        this.developer = developer;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getKanbancol() {
        return kanbancol;
    }

    public void setKanbancol(List<String> kanbancol) {
        this.kanbancol = kanbancol;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Boolean getActivityOnlyForManagers() {
        return activityOnlyForManagers;
    }

    public void setActivityOnlyForManagers(Boolean activityOnlyForManagers) {
        this.activityOnlyForManagers = activityOnlyForManagers;
    }

    public Integer getCurrentsprint() {
        return currentsprint;
    }

    public void setCurrentsprint(Integer currentsprint) {
        this.currentsprint = currentsprint;
    }
}
