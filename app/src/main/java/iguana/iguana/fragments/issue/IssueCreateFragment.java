package iguana.iguana.fragments.issue;


import android.app.Fragment;
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
import iguana.iguana.fragments.ApiFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.remote.APIService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class IssueCreateFragment extends ApiFragment {
    private EditText title, storypoints, description, due_date;
    private Spinner priority, type;
    private String project;
    private APIService mAPIService;
    private CommonMethods common;

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
        mAPIService = ((MainActivity) getActivity()).get_api_service();
        View view = getView();
        Button button = (Button) view.findViewById(R.id.send);
        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        due_date = (EditText) view.findViewById(R.id.due_date);

        priority = (Spinner) view.findViewById(R.id.priority);
        type = (Spinner) view.findViewById(R.id.type);

        storypoints = (EditText) view.findViewById(R.id.storypoints);
        project = getArguments().getString("project");
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


                get_api_service().createIssue(project, body).enqueue(new Callback<Issue>() {
                                                                   @Override
                                                                   public void onResponse(Call<Issue> call, Response<Issue> response) {
                                                                       if (response.isSuccessful()) {
                                                                           IssueBaseFragment fragment= new IssueBaseFragment();
                                                                           Bundle d = new Bundle();
                                                                           d.putParcelable("issue", response.body());
                                                                           fragment.setArguments(d);
                                                                           d.putString("project", project);
                                                                           fragment.setArguments(d);
                                                                           FragmentTransaction ft;
                                                                           if (getParentFragment() == null)
                                                                                ft = getFragmentManager().beginTransaction();
                                                                           else
                                                                                ft = getParentFragment().getFragmentManager().beginTransaction();
                                                                           ft.addToBackStack(null);
                                                                           ft.replace(R.id.content_frame, fragment, "visible_fragment");
                                                                           ft.commit();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_issue_create, container, false);
    }


}