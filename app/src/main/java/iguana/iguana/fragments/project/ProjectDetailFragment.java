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
    private APIService mAPIService;
    private String name_short;
    private TextView description;
    private TextView title;
    private ListView drawer;
    private Project project;

    public ProjectDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("PDF onStart");

        mAPIService = ((MainActivity) getActivity()).get_api_service();        View view = getView();
        title = (TextView) view.findViewById(R.id.project_name);
        description = (TextView) view.findViewById(R.id.project_description);
        if (project == null) {
            getProject(name_short);
        } else {
            title.setText(project.getName() + "("+project.getNameShort()+")");
            description.setText(project.getDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (project != null) {
            outState.putParcelable("project", project);
        }
        if (name_short != null)
            outState.putString("name_short", name_short);

    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            title = (TextView) getActivity().findViewById(R.id.project_name);
            description = (TextView) getActivity().findViewById(R.id.project_description);

            if(name_short == null)
                name_short = savedInstanceState.getString("name_short");

            if(project == null)
                project = savedInstanceState.getParcelable("project");
        }
    }

    public void getProject(String name_short) {
        mAPIService.getProject(name_short).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if(response.isSuccessful()) {
                    project = response.body();
                    title.setText(project.getName() + "("+project.getNameShort()+")");
                    description.append(project.getDescription());
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
            }
        });
    }


    public void setProject(String name_short) {
        this.name_short = name_short;
    }
    public String getProject() {
        return this.name_short;
    }


}