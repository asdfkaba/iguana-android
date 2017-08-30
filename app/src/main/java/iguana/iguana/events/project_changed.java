package iguana.iguana.events;

import iguana.iguana.models.Project;

/**
 * Created by moritz on 25.08.17.
 */

public class project_changed {
    public final Project project;
    public Project getProject(){return this.project;}
    public final boolean delete;
    public boolean deleted(){return this.delete;}
    public project_changed(Project project, boolean delete) {
        this.project = project;
        this.delete = delete;
    }
}
