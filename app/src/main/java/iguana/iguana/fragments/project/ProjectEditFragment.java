package iguana.iguana.fragments.project;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import iguana.iguana.R;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.issue_changed;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.base.ApiFragment;
import iguana.iguana.models.Project;
import iguana.iguana.remote.APIService;
import iguana.iguana.remote.apicalls.ProjectCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectEditFragment extends ApiFragment {
    private EditText name;
    private EditText name_short;
    private EditText description;
    private Project project;
    private ProjectCalls api;

    public ProjectEditFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("project", project);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (project == null)
                project = savedInstanceState.getParcelable("project");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        api = new ProjectCalls(view);
        if (project == null)
            project = getArguments().getParcelable("project");

        Button button = (Button) view.findViewById(R.id.send);
        name = (EditText) view.findViewById(R.id.name);
        description = (EditText) view.findViewById(R.id.description);

        name.setText(project.getName());
        description.setText(project.getDescription());

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_name = name.getText().toString();
                String body_description = description.getText().toString();
                HashMap body = new HashMap<>();
                body.put("name", body_name);
                if (body_description.length() > 0)
                    body.put("description", body_description);

                api.editProject(project, body);
            }
        });

        Button delete = (Button) view.findViewById(R.id.delete);

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        api.deleteProject(project);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setMessage("Are you sure you want  to delete this object?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_edit, container, false);
    }



}