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
import java.util.Map;

import iguana.iguana.R;
import iguana.iguana.adapters.IssueAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.issue_changed;
import iguana.iguana.models.Issue;
import iguana.iguana.models.IssueResult;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moritz on 27.08.17.
 */

public class IssueCalls {
    private View rootView;

    public IssueCalls(View view) {
        this.rootView = view;
    }

    private APIService get_api_service(View view) {
        return ((MainActivity) view.getContext()).get_api_service();
    }

    public void getProjectIssues(String proj, Integer pa, IssueAdapter ada) {
        final IssueAdapter adapter = ada;
        final String project = proj;
        final Integer page = pa;
        final View view = rootView;
        final ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressBar);

        Map options = new HashMap<String, String>();
        options.put("page", page);

        get_api_service(view).getProjectIssues(project, options).enqueue(new Callback<IssueResult>() {
            @Override
            public void onResponse(Call<IssueResult> call, Response<IssueResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());

                    if (response.body().getNext() != null) {
                        getProjectIssues(response.body().getResults().get(0).getProjectShortName(), new Integer(page + 1), adapter);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.do_notify();
                    }
                }
            }

            @Override
            public void onFailure(Call<IssueResult> call, Throwable t) {
                Toast.makeText(view.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
            }
        });
    }

    public void getIssues(Integer pa, IssueAdapter ada) {
        final IssueAdapter adapter = ada;
        final Integer page = pa;
        final View view = rootView;
        final ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressBar);

        Map options = new HashMap<String, String>();
        options.put("page", page);

        get_api_service(view).getIssues(options).enqueue(new Callback<IssueResult>() {
            @Override
            public void onResponse(Call<IssueResult> call, Response<IssueResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getIssues(new Integer(page + 1), adapter);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.do_notify();
                    }
                } else {
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<IssueResult> call, Throwable t) {
                Toast.makeText(view.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    public void editIssue(Issue iss, HashMap body) {
        final Issue issue = iss;
        final EditText title = (EditText) rootView.findViewById(R.id.title);
        final EditText description = (EditText) rootView.findViewById(R.id.description);
        final EditText storypoints = (EditText) rootView.findViewById(R.id.storypoints);
        final EditText due_date = (EditText) rootView.findViewById(R.id.due_date);


        get_api_service(rootView).editIssue(issue.getProjectShortName(), issue.getNumber(), body).enqueue(new Callback<Issue>() {
            @Override
            public void onResponse(Call<Issue> call, Response<Issue> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new issue_changed(response.body()));
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                } else {
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Iterator<?> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (key.equals("title")) {
                                title.setError(obj.get(key).toString());
                            } else if (key.equals("description")) {
                                description.setError(obj.get(key).toString());
                            } else if (key.equals("storypoints")) {
                                storypoints.setError(obj.get(key).toString());
                            } else if (key.equals("due_date")) {
                                due_date.setError(obj.get(key).toString());
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Issue> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void createIssue(String project, HashMap body) {
        final EditText title = (EditText) rootView.findViewById(R.id.title);
        final EditText description = (EditText) rootView.findViewById(R.id.description);
        final EditText storypoints = (EditText) rootView.findViewById(R.id.storypoints);
        final EditText due_date = (EditText) rootView.findViewById(R.id.due_date);


        get_api_service(rootView).createIssue(project, body).enqueue(new Callback<Issue>() {
            @Override
            public void onResponse(Call<Issue> call, Response<Issue> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new issue_changed(response.body()));
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                } else {
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Iterator<?> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (key.equals("title")) {
                                title.setError(obj.get(key).toString());
                            } else if (key.equals("description")) {
                                description.setError(obj.get(key).toString());
                            } else if (key.equals("storypoints")) {
                                storypoints.setError(obj.get(key).toString());
                            } else if (key.equals("due_date")) {
                                due_date.setError(obj.get(key).toString());
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Issue> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
