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
import iguana.iguana.adapters.TimelogAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.comment_changed;
import iguana.iguana.events.timelog_changed;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Timelog;
import iguana.iguana.models.TimelogResult;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moritz on 27.08.17.
 */

public class TimelogCalls {
    private View rootView;

    public TimelogCalls(View view) {
        this.rootView = view;
    }
    private APIService get_api_service(View view) {
        return ((MainActivity) view.getContext()).get_api_service();
    }

    public void getTimelogs(Integer pa, TimelogAdapter ada) {
        final TimelogAdapter adapter = ada;
        final Integer page = pa;
        final ProgressBar progress = (ProgressBar) rootView.findViewById(R.id.progressBar);

        Map options = new HashMap<String, String>();
        options.put("page", page);
        get_api_service(rootView).getTimelogs(options).enqueue(new Callback<TimelogResult>() {
            @Override
            public void onResponse(Call<TimelogResult> call, Response<TimelogResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getTimelogs(new Integer(page + 1), adapter);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<TimelogResult> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });

    }

    public void getTimelogsForIssue(Integer pa, Issue iss,  TimelogAdapter ada) {
        final TimelogAdapter adapter = ada;
        final Integer page = pa;
        final ProgressBar progress = (ProgressBar) rootView.findViewById(R.id.progressBar);
        final Issue issue = iss;

        Map options = new HashMap<String, String>();
        options.put("page", page);
        get_api_service(rootView).getTimelogsForIssue(issue.getProjectShortName(), issue.getNumber(), options).enqueue(new Callback<TimelogResult>() {
            @Override
            public void onResponse(Call<TimelogResult> call, Response<TimelogResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getTimelogsForIssue(new Integer(page + 1), issue, adapter);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<TimelogResult> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });
    }
    public void createTimelog(Issue iss, HashMap body) {
        final Issue issue = iss;
        final EditText text = (EditText) rootView.findViewById(R.id.time);

        get_api_service(rootView).createTimelog(issue.getProjectShortName(), issue.getNumber(), body).enqueue(new Callback<Timelog>() {
            @Override
            public void onResponse(Call<Timelog> call, Response<Timelog> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new timelog_changed(response.body(), false));
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                } else {
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Iterator<?> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (key.equals("time")) {
                                text.setError(obj.get(key).toString());
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Timelog> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editTimelog(Timelog log, HashMap body) {
        final Timelog timelog = log;
        final EditText text = (EditText) rootView.findViewById(R.id.time);

        get_api_service(rootView).editTimelog(timelog.getNameShort(), timelog.getIssueNumber(), timelog.getNumber(), body).enqueue(new Callback<Timelog>() {
            @Override
            public void onResponse(Call<Timelog> call, Response<Timelog> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new timelog_changed(response.body(), false));
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();

                } else {
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Iterator<?> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (key.equals("time")) {
                                text.setError(obj.get(key).toString());
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Timelog> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteTimelog(Timelog log) {
        final Timelog timelog = log;
        final EditText text = (EditText) rootView.findViewById(R.id.time);

        get_api_service(rootView).deleteTimelog(timelog.getNameShort(), timelog.getIssueNumber(), timelog.getNumber()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new timelog_changed(timelog, true));
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                } else {
                    rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
