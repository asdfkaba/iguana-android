package iguana.iguana.fragments.issue;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import iguana.iguana.app.MainActivity;
import iguana.iguana.events.issue_changed;
import iguana.iguana.events.new_token;
import iguana.iguana.fragments.base.ApiFragment;
import iguana.iguana.fragments.project.ProjectBaseFragment;
import iguana.iguana.R;
import iguana.iguana.common.CommonMethods;
import iguana.iguana.models.Issue;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueEditFragment extends ApiFragment {
    private EditText title, storypoints, description, due_date;
    private Spinner priority, type;
    private CommonMethods common;
    private Issue issue;
    public Issue getIssue() {
        return this.issue;
    }

    public IssueEditFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (issue != null)
            outState.putParcelable("issue", issue);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && issue == null) {
                issue = savedInstanceState.getParcelable("issue");
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.menuSort).setVisible(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called
        common = new CommonMethods();
        View view = getView();
        Button button = (Button) view.findViewById(R.id.send);

        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        due_date = (EditText) view.findViewById(R.id.due_date);
        priority = (Spinner) view.findViewById(R.id.priority);
        type = (Spinner) view.findViewById(R.id.type);
        storypoints = (EditText) view.findViewById(R.id.storypoints);

        issue = getArguments().getParcelable("issue");

        title.setText(issue.getTitle());
        description.setText(issue.getDescription());
        due_date.setText(issue.getDueDate());
        priority.setSelection(common.getIndex(priority, common.map_priority_to_string(issue.getPriority())));
        storypoints.setText(String.valueOf(issue.getStorypoints()));
        type.setSelection(common.getIndex(type, issue.getType()));
        priority.setPrompt("Choose priority");
        type.setPrompt("Choose type");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // remove keyboard if still visible
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String body_title = title.getText().toString();
                String body_storypoints = storypoints.getText().toString();
                String body_description = description.getText().toString();
                String body_priority = priority.getSelectedItem().toString();
                String body_type = type.getSelectedItem().toString();
                String body_due_date = due_date.getText().toString();

                HashMap body = new HashMap<>();
                body.put("title", title.getText().toString());
                if (body_storypoints.length() > 0)
                    body.put("storypoints", body_storypoints);
                body.put("description", body_description);
                body.put("priority", common.map_priority(body_priority));
                body.put("type", body_type);
                if (body_due_date.length() > 0)
                    body.put("due_date", body_due_date);

                get_api_service().editIssue(issue.getProjectShortName(), issue.getNumber(), body).enqueue(new Callback<Issue>() {
                                                                         @Override
                                                                         public void onResponse(Call<Issue> call, Response<Issue> response) {
                                                                             if (response.isSuccessful()) {
                                                                                 EventBus.getDefault().postSticky(new issue_changed(response.body()));
                                                                                 getFragmentManager().popBackStack();
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
                                                                     }
                );
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issue_edit, container, false);
    }


}