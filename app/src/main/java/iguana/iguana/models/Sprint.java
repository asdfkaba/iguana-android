package iguana.iguana.models;

/**
 * Created by moritz on 19.09.17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Sprint {

    @SerializedName("seqnum")
    @Expose
    private Integer seqnum;
    @SerializedName("project")
    @Expose
    private String project;
    @SerializedName("startdate")
    @Expose
    private String startdate;
    @SerializedName("enddate")
    @Expose
    private Object enddate;
    @SerializedName("issues")
    @Expose
    private List<String> issues = null;

    public Integer getSeqnum() {
        return seqnum;
    }

    public void setSeqnum(Integer seqnum) {
        this.seqnum = seqnum;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public Object getEnddate() {
        return enddate;
    }

    public void setEnddate(Object enddate) {
        this.enddate = enddate;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

}