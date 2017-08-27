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
import iguana.iguana.adapters.CommentAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.comment_changed;
import iguana.iguana.models.Comment;
import iguana.iguana.models.CommentResult;
import iguana.iguana.models.Issue;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by moritz on 27.08.17.
 */

public class CommentCalls {
    private View rootView;

    public CommentCalls(View view) {
        this.rootView = view;
    }
    private APIService get_api_service(View view) {
        return ((MainActivity) view.getContext()).get_api_service();
    }

    public void getComments(Integer pa, String proj, Integer iss_number, CommentAdapter ada) {
        final CommentAdapter adapter = ada;
        final Integer page = pa;
        final ProgressBar progress = (ProgressBar) rootView.findViewById(R.id.progressBar);
        final String project = proj;
        final Integer issue_number = iss_number;

        Map options = new HashMap<String, String>();
        options.put("page", page);

        get_api_service(rootView).getComments(project, issue_number, options).enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getComments(new Integer(page + 1), project, issue_number, adapter);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
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
            public void onFailure(Call<CommentResult> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });
    }






    public void createComment(Issue iss, HashMap body) {
        final Issue issue = iss;
        final EditText text = (EditText) rootView.findViewById(R.id.text);

        get_api_service(rootView).createComment(issue.getProjectShortName(), issue.getNumber(), body).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new comment_changed(response.body()));
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();
                } else {
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Iterator<?> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (key.equals("text")) {
                                text.setError(obj.get(key).toString());
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editComment(Comment comm, HashMap body) {
        final Comment comment = comm;
        final EditText text = (EditText) rootView.findViewById(R.id.text);

        get_api_service(rootView).editComment(comment.getNameShort(), comment.getIssueNumber(), comment.getSeqnum(), body).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new comment_changed(response.body()));
                    ((MainActivity) rootView.getContext()).getFragmentManager().popBackStack();

                } else {
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Iterator<?> keys = obj.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (key.equals("text")) {
                                text.setError(obj.get(key).toString());
                            }
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(rootView.getContext(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
