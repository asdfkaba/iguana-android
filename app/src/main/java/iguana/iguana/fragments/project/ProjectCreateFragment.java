package iguana.iguana.fragments.project;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.fragments.base.BaseFragment;
import iguana.iguana.models.Project;
import iguana.iguana.remote.APIService;
import iguana.iguana.remote.apicalls.ProjectCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectCreateFragment extends BaseFragment {
    private EditText name;
    private EditText name_short;
    private EditText description;
    private ProjectCalls api;

    public ProjectCreateFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name.getText().toString());
        outState.putString("name_short", name_short.getText().toString());
        outState.putString("description", description.getText().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (name != null)
                name.setText(savedInstanceState.getString("title"));
            if (description != null)
                description.setText(savedInstanceState.getString("description"));
            if (name_short != null)
                name_short.setText(savedInstanceState.getString("name_short"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        api = new ProjectCalls(view);
        Button button = (Button) view.findViewById(R.id.send);
        name = (EditText) view.findViewById(R.id.name);
        description = (EditText) view.findViewById(R.id.description);
        name_short = (EditText) view.findViewById(R.id.name_short);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_name = name.getText().toString();
                String body_name_short = name_short.getText().toString();
                String body_description = description.getText().toString();
                HashMap body = new HashMap<>();
                body.put("name", body_name);
                body.put("name_short", body_name_short);
                body.put("description", body_description);
                api.createProject(body);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_create, container, false);
    }



}