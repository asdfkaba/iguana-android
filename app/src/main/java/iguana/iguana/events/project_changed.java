package iguana.iguana.events;

import iguana.iguana.models.Project;

/**
 * Created by moritz on 25.08.17.
 */

public class project_changed {
    public final Project project;
    public Project getProject(){return this.project;}
    public project_changed(Project project) {
        this.project = project;
    }
}
