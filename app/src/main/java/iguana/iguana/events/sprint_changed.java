package iguana.iguana.events;

import iguana.iguana.models.Issue;
import iguana.iguana.models.Sprint;

/**
 * Created by moritz on 22.09.17.
 */

public class sprint_changed {

        public final Sprint sprint;
        public Sprint getSprint(){return this.sprint;}
        public final boolean delete;
        public boolean deleted(){return this.delete;}
        public sprint_changed(Sprint sprint, Boolean delete) {
            this.sprint = sprint;
            this.delete =  delete;
        }

}
