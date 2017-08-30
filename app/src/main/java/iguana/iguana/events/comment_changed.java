package iguana.iguana.events;

import iguana.iguana.models.Comment;
import iguana.iguana.models.Issue;

/**
 * Created by moritz on 25.08.17.
 */

public class comment_changed {
    public final Comment comment;
    public Comment getComment(){return this.comment;}
    public final boolean delete;
    public boolean deleted(){return this.delete;}
    public comment_changed(Comment comment, boolean delete) {
        this.comment = comment;
        this.delete = delete;
    }
}
