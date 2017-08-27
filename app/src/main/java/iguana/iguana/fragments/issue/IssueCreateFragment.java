package iguana.iguana.fragments.issue;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.common.CommonMethods;
import iguana.iguana.common.view.MultipleSpinner;
import iguana.iguana.fragments.base.ApiFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;
import iguana.iguana.remote.APIService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import iguana.iguana.remote.apicalls.IssueCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueCreateFragment extends ApiFragment {
    private EditText title, storypoints, description, due_date;
    private Spinner priority, type;
    private MultipleSpinner assignees;
    private CommonMethods common;
    private IssueCalls api;
    private Project project;


    public IssueCreateFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (title != null)
            outState.putString("title", title.getText().toString());
        if (priority != null)
            outState.putString("priority", priority.getSelectedItem().toString());
        if (storypoints != null)
            outState.putString("storypoints", storypoints.getText().toString());
        if (description != null)
            outState.putString("description", description.getText().toString());
        outState.putParcelable("project", project);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (title != null)
                title.setText(savedInstanceState.getString("title"));
            if (description != null)
                description.setText(savedInstanceState.getString("description"));
            if (storypoints != null)
                storypoints.setText(savedInstanceState.getString("storypoints"));
            if (priority != null)
                priority.setSelection(common.getIndex(priority, savedInstanceState.getString("priority")));
            if (project == null)
                project = savedInstanceState.getParcelable("project");
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
        api = new IssueCalls(view);
        if (project == null)
            project = getArguments().getParcelable("project");
        Button button = (Button) view.findViewById(R.id.send);
        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        due_date = (EditText) view.findViewById(R.id.due_date);

        priority = (Spinner) view.findViewById(R.id.priority);
        type = (Spinner) view.findViewById(R.id.type);
        assignees = (MultipleSpinner) view.findViewById(R.id.assignees);
        assignees.setItems(project.getMembers());
        assignees.setSelection(new ArrayList<String>());

        storypoints = (EditText) view.findViewById(R.id.storypoints);
        priority.setSelection(2);
        priority.setPrompt("Choose priority");
        type.setPrompt("Choose type");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_title = title.getText().toString();
                String body_storypoints = storypoints.getText().toString();
                String body_description = description.getText().toString();
                String body_priority = priority.getSelectedItem().toString();
                String body_type = type.getSelectedItem().toString();
                String body_due_date = due_date.getText().toString();

                HashMap body = new HashMap<>();
                body.put("title", title.getText().toString());
                if (body_storypoints.length()>0)
                    body.put("storypoints", body_storypoints);
                body.put("description", body_description);
                body.put("priority", common.map_priority(body_priority));
                body.put("type", body_type);
                if (body_due_date.length()>0)
                    body.put("due_date", body_due_date);
                body.put("assignee", assignees.getSelectedStrings().toArray());

                api.createIssue(project.getNameShort(), body);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issue_create, container, false);
    }

}