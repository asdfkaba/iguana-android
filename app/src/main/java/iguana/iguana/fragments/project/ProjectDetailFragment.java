package iguana.iguana.fragments.project;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.models.Project;
import iguana.iguana.remote.APIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectDetailFragment extends Fragment {
    private TextView title, description;
    private Project project;

    public ProjectDetailFragment() {    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (project == null && getArguments() != null)
            project = getArguments().getParcelable("project");
        title = (TextView) view.findViewById(R.id.project_name);
        description = (TextView) view.findViewById(R.id.project_description);
        if (project != null) {
            title.setText(project.getName());
            description.setText(project.getDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (project != null) {
            outState.putParcelable("project", project);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            if(project == null)
                project = savedInstanceState.getParcelable("project");
        }
    }
}