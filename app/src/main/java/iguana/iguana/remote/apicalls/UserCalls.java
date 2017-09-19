package iguana.iguana.remote.apicalls;

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
import java.util.List;
import java.util.Map;

import iguana.iguana.R;
import iguana.iguana.adapters.ProjectAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.common.view.MultipleSpinner;
import iguana.iguana.events.project_changed;
import iguana.iguana.models.Project;
import iguana.iguana.models.ProjectResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moritz on 27.08.17.
 */

public class UserCalls extends ApiCalls {

    public UserCalls(View view) {
        super(view);
    }


    public void createProject(HashMap body) {
        final View view = rootView;

        get_api_service(view).getUsers(body).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful()) {

                } else {
                }

            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                Toast.makeText(view.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
