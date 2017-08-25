package iguana.iguana.fragments.timelog;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.adapters.TimelogAdapter;
import iguana.iguana.models.Timelog;
import iguana.iguana.remote.APIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelogDetailFragment extends Fragment {
    private APIService mAPIService;
    private String project_name_short;
    private int issue_number;
    private int timelog_number;
    private Timelog timelog;
    private TimelogAdapter adapter;

    public TimelogDetailFragment() {
        // Required empty public constructor
    }
    @Override
    public void onStart() {
        super.onStart();
        mAPIService = ((MainActivity) getActivity()).get_api_service();
        getTimelog(project_name_short, issue_number, timelog_number);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }

    public void getTimelog(String name_short, int issue_number, int timelog_number) {
        mAPIService.getProjectSpecificIssueSpecificTimelog(name_short, issue_number, timelog_number).enqueue(new Callback<Timelog>() {
            @Override
            public void onResponse(Call<Timelog> call, Response<Timelog> response) {
                if(response.isSuccessful()) {
                    timelog = response.body();
                    View view = getView();
                    TextView title = (TextView) view.findViewById(R.id.project_name);
                    title.setText(timelog.getIssue());
                    getActivity().getActionBar().setTitle("asd");

                }
            }

            @Override
            public void onFailure(Call<Timelog> call, Throwable t) {
            }
        });
    }


    public void setTimelog(String name_short, int issue_number, int timelog_number) {
        this.project_name_short = name_short;
        this.issue_number = issue_number;
        this.timelog_number = timelog_number;
    }

}