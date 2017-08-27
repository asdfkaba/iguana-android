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
import java.util.List;
import java.util.Map;

import iguana.iguana.R;
import iguana.iguana.adapters.ProjectAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.common.view.MultipleSpinner;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.project.ProjectBaseFragment;
import iguana.iguana.models.Project;
import iguana.iguana.models.ProjectResult;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moritz on 27.08.17.
 */

public class ProjectCalls {
    private View rootView;

    public ProjectCalls(View view) {
        this.rootView = view;
    }
    private APIService get_api_service(View view) {
        return ((MainActivity) view.getContext()).get_api_service();
    }

    public void createProject(HashMap body) {
        final View view = rootView;
        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText name_short = (EditText) view.findViewById(R.id.name_short);

        final EditText description = (EditText) view.findViewById(R.id.description);

        get_api_service(view).createProject(body).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful()) {
                    ProjectBaseFragment fragment = new ProjectBaseFragment();
                    Bundle d = new Bundle();
                    d.putParcelable("project", response.body());
                    fragment.setArguments(d);
                    FragmentTransaction ft = ((MainActivity) view.getContext()).getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment, "visible_fragment");
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                } else {
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Iterator<?> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (key.equals("name")) {
                                name.setError(obj.get(key).toString());
                            } else if (key.equals("name_short")) {
                                name_short.setError(obj.get(key).toString());
                            } else if (key.equals("description")) {
                                description.setError(obj.get(key).toString());
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                Toast.makeText(view.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void editProject(Project proj, HashMap body) {
        final View view = rootView;
        final Project project = proj;
        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText description = (EditText) view.findViewById(R.id.description);

        get_api_service(view).editProject(project.getNameShort(), body).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new project_changed(response.body()));
                    ((MainActivity) view.getContext()).getFragmentManager().popBackStack();
                } else {
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Iterator<?> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (key.equals("name")) {
                                name.setError(obj.get(key).toString());
                            } else if (key.equals("description")) {
                                description.setError(obj.get(key).toString());
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                Toast.makeText(view.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getProjects(Integer pa, ProjectAdapter ada) {
        final ProjectAdapter adapter = ada;
        final Integer page = pa;
        final View view = rootView;
        final ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressBar);

        Map options = new HashMap<String, String>();
        options.put("page", page);
        get_api_service(view).getProjects().enqueue(new Callback<ProjectResult>() {
            @Override
            public void onResponse(Call<ProjectResult> call, Response<ProjectResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getProjects(new Integer(page + 1), adapter);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectResult> call, Throwable t) {
                Toast.makeText(view.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);

            }
        });
    }

    public void getProject(String name_short, MultipleSpinner ass, List<String> curr) {
        final MultipleSpinner assignee = ass;
        final List<String> current = curr;
        get_api_service(rootView).getProject(name_short).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful()) {
                    assignee.setItems(response.body().getMembers());
                    assignee.setSelection(current);
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {


            }
        });
    }

}