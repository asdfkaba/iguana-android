package iguana.iguana.events;

import iguana.iguana.models.Comment;
import iguana.iguana.models.Timelog;

/**
 * Created by moritz on 25.08.17.
 */

public class timelog_changed {
    public final Timelog timelog;
    public Timelog getTimelog(){return this.timelog;}
    public final boolean delete;
    public boolean deleted(){return this.delete;}

    public timelog_changed(Timelog timelog, boolean delete) {
        this.timelog = timelog;
        this.delete = delete;
    }
}
