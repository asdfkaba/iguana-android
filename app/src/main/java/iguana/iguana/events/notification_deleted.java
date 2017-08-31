package iguana.iguana.events;

import iguana.iguana.models.Issue;
import iguana.iguana.models.Notification;

/**
 * Created by moritz on 25.08.17.
 */

public class notification_deleted {
    public final String notification;
    public String getNotification(){return this.notification;}
    public notification_deleted(String notification) {
        this.notification =  notification;
    }
}
