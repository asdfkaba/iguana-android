package iguana.iguana.remote.apicalls;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import iguana.iguana.R;
import iguana.iguana.adapters.CommentAdapter;
import iguana.iguana.adapters.IssueAdapter;
import iguana.iguana.adapters.ProjectAdapter;
import iguana.iguana.adapters.TimelogAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.comment_changed;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.project.ProjectBaseFragment;
import iguana.iguana.models.Comment;
import iguana.iguana.models.CommentResult;
import iguana.iguana.models.Issue;
import iguana.iguana.models.IssueResult;
import iguana.iguana.models.Project;
import iguana.iguana.models.ProjectResult;
import iguana.iguana.models.TimelogResult;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moritz on 27.08.17.
 */

public class ApiCalls {

    protected APIService get_api_service(View view) {
        return ((MainActivity) view.getContext()).get_api_service();
    }





}
