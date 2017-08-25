package iguana.iguana.fragments.project;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import iguana.iguana.R;
import iguana.iguana.adapters.ProjectAdapter;
import iguana.iguana.fragments.ApiFragment;
import iguana.iguana.models.Project;
import iguana.iguana.models.ProjectResult;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectsFragment extends ApiFragment implements ProjectAdapter.OnViewHolderClick<Project>{
    private TextView mResponseTv;
    private Context context;
    private ProjectAdapter adapter;
    ProgressBar progress;
    SwipeRefreshLayout swipeRefreshLayout;
    private Integer current_page;

    @Override
    public void onClick(View view, int position, Project item) {
        //ProjectDetailFragment fragment= new ProjectDetailFragment();
        //fragment.setProject(item.getNameShort());
        ProjectBaseFragment fragment = new ProjectBaseFragment();
        Bundle d = new Bundle();
        d.putParcelable("project", item);
        fragment.setArguments(d);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack("visible_fragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }

    private RecyclerView recyclerView;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void onStart() {
        super.onStart();

        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called
        View v = getView();
        progress = (ProgressBar) v.findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);
        current_page = 1;
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                current_page = 1;
                progress.setVisibility(View.VISIBLE);
                getProjects(current_page);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getProjects(current_page);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ProjectAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);

    }

    public ProjectsFragment() {
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        menu.findItem(R.id.add).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                ProjectCreateFragment fragment= new ProjectCreateFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "visible_fragment");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview_list, container, false);
    }


    public void getProjects(int page) {
        Map options = new HashMap<String,String>();
        options.put("page", page);
        get_api_service().getProjects().enqueue(new Callback<ProjectResult>() {
            @Override
            public void onResponse(Call<ProjectResult> call, Response<ProjectResult> response) {
                if(response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getProjects(++current_page);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectResult> call, Throwable t) {
                Toast.makeText(getActivity(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);

            }
        });
    }

}
