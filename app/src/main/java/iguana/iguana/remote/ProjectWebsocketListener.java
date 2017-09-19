package iguana.iguana.remote;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import iguana.iguana.app.MainActivity;
import iguana.iguana.events.comment_changed;
import iguana.iguana.events.issue_changed;
import iguana.iguana.events.project_changed;
import iguana.iguana.events.timelog_changed;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;
import iguana.iguana.models.Timelog;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by moritz on 06.09.17.
 */

public class ProjectWebsocketListener extends WebSocketListener{
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private MainActivity activity;
    private boolean reconnect_tried = false;
    private boolean new_token = false;


    public ProjectWebsocketListener(MainActivity act) {
        super();
        this.activity = act;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        System.out.println("SUCESSFULL WEBSOCKET OPEN");
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("Receiving string : " + text);
        JSONObject json = null;
        try {
            System.out.println(text)    ;
            json = new JSONObject(text);
            String type = json.keys().next();
            Gson gson = new Gson();
            switch (type) {
                case "issue":
                    Issue issue = gson.fromJson(json.get(type).toString(), Issue.class);
                    EventBus.getDefault().postSticky(new issue_changed(issue));
                    break;
                case "project":
                    Project project = gson.fromJson(json.get(type).toString(), Project.class);
                    EventBus.getDefault().postSticky(new project_changed(project, false));
                    break;
                case "timelog":
                    Timelog timelog = gson.fromJson(json.get(type).toString(), Timelog.class);
                    EventBus.getDefault().postSticky(new timelog_changed(timelog, false));
                    break;
                case "comment":
                    Comment comment = gson.fromJson(json.get(type).toString(), Comment.class);
                    EventBus.getDefault().postSticky(new comment_changed(comment, false));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("Receiving bytes : " + bytes.hex());
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        System.out.println("Closing : " + code + " / " + reason);
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println("Error : " + t.getMessage());
/*        if (reconnect_tried == false) {
            activity.init_socket();
            reconnect_tried = true;
            return;
        }
        if (new_token)
            return;

        String token = activity.refresh_token();
        if (token != null) {
            reconnect_tried = false;
        } else {
            new_token = true;
        }

        activity.init_socket();*/

    }
}
