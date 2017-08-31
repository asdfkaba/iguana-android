package iguana.iguana.events;

import iguana.iguana.models.Issue;

/**
 * Created by moritz on 25.08.17.
 */

public class issue_arrived {
    public final Issue issue;
    public Issue getIssue(){return this.issue;}
    public issue_arrived(Issue issue) {
        this.issue = issue;
    }
}
