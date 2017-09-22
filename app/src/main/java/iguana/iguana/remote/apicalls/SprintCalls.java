package iguana.iguana.remote.apicalls;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import iguana.iguana.R;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.issue_changed;
import iguana.iguana.events.project_changed;
import iguana.iguana.events.sprint_changed;
import iguana.iguana.fragments.calls.FragmentCalls;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;
import iguana.iguana.models.Sprint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moritz on 20.09.17.
 */

public class SprintCalls extends ApiCalls {

    public SprintCalls(View view) {
        super(view);
    }

    public void createSprint(Project proj) {
        final Project project = proj;
        get_api_service(rootView).createSprint(project.getNameShort()).enqueue(new Callback<Sprint>() {
            @Override
            public void onResponse(Call<Sprint> call, Response<Sprint> response) {
                if (response.isSuccessful()) {
                    List<Sprint> sprints = project.getSprints();
                    sprints.add(response.body());
                    project.setSprints(sprints);
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                    calls.ProjectBase(project);
                    calls.ProjectSprints(project);
                }
            }
            @Override
            public void onFailure(Call<Sprint> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    public void patchSprint(Project proj ,String sprint, HashMap body) {
        final Project project = proj;
        get_api_service(rootView).patchSprint(project.getNameShort(), sprint, body).enqueue(new Callback<Sprint>() {
            @Override
            public void onResponse(Call<Sprint> call, Response<Sprint> response) {
                if (response.isSuccessful()) {
                    List<Sprint> sprints = project.getSprints();
                    for (Sprint spr:sprints) {
                        if (spr.getSeqnum() == response.body().getSeqnum()) {
                            if (response.body().getEnddate() != null) {
                                sprints.remove(spr);
                                if (response.body().getStartdate() != null && project.getCurrentsprint().split("-")[1].equals(response.body().getSeqnum().toString()))
                                    project.setCurrentsprint(null);

                            } else if (response.body().getStartdate() != null) {
                                project.setCurrentsprint("Sprint-" + response.body().getSeqnum().toString());
                            }
                            break;
                        }
                    }
                    project.setSprints(sprints);
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                    calls.ProjectBase(project);
                    calls.ProjectSprints(project);
                }
            }

            @Override
            public void onFailure(Call<Sprint> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

}
