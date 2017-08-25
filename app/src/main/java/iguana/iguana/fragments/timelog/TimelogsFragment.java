package iguana.iguana.fragments.timelog;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.adapters.TimelogAdapter;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Timelog;
import iguana.iguana.models.TimelogResult;
import iguana.iguana.remote.APIService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimelogsFragment extends Fragment implements TimelogAdapter.OnViewHolderClick<Timelog>{
    private APIService mAPIService;
    private List<Timelog> timelogs;
    private ArrayList<String>  project_names;
    private TextView mResponseTv;
    private Context context;
    private TimelogAdapter adapter;
    private Issue issue;

    @Override
    public void onClick(View view, int position, Timelog item) {
        return;
    }


    private RecyclerView recyclerView;
    private Integer current_page;
    ProgressBar progress;
    SwipeRefreshLayout swipeRefreshLayout;


    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called
        View view = getView();

        mAPIService = ((MainActivity) getActivity()).get_api_service();

        current_page = 1;

        if (getArguments() != null)
            issue = getArguments().getParcelable("issue");



        if (adapter == null) {
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
            adapter = new TimelogAdapter(getActivity(), this);
            if (issue == null)
                getTimelogs(current_page);
            else
                getTimelogsForIssue(current_page, issue);
        }


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);



        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                current_page = 1;
                progress.setVisibility(View.VISIBLE);
                if (issue == null)
                    getTimelogs(current_page);
                else
                    getTimelogsForIssue(current_page, issue);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (issue != null)
            menu.findItem(R.id.add).setVisible(true);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                TimelogCreateFragment fragment = new TimelogCreateFragment();
                Bundle d = new Bundle();
                d.putParcelable("issue", issue);
                fragment.setArguments(d);
                FragmentTransaction ft = getParentFragment().getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "visible_fragment");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public TimelogsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.timelog_list, container, false);
    }


    public void getTimelogs(Integer page) {
        Map options = new HashMap<String, String>();
        options.put("page", page);
        mAPIService.getTimelogs(options).enqueue(new Callback<TimelogResult>() {
            @Override
            public void onResponse(Call<TimelogResult> call, Response<TimelogResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getTimelogs(++current_page);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<TimelogResult> call, Throwable t) {
                Toast.makeText(getActivity(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });
    }
    public void getTimelogsForIssue(Integer page, Issue issue) {
        Map options = new HashMap<String, String>();
        options.put("page", page);
        mAPIService.getTimelogsForIssue(issue.getProjectShortName(), issue.getNumber(), options).enqueue(new Callback<TimelogResult>() {
            @Override
            public void onResponse(Call<TimelogResult> call, Response<TimelogResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getTimelogs(++current_page);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<TimelogResult> call, Throwable t) {
                Toast.makeText(getActivity(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });
    }



}
