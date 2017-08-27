package iguana.iguana.fragments.project;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import iguana.iguana.R;
import iguana.iguana.adapters.ProjectAdapter;
import iguana.iguana.events.issue_changed;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.base.ApiScrollFragment;
import iguana.iguana.models.Project;
import iguana.iguana.models.ProjectResult;

import java.util.HashMap;
import java.util.Map;

import iguana.iguana.remote.apicalls.ProjectCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProjectsFragment extends ApiScrollFragment implements ProjectAdapter.OnViewHolderClick<Project>, ProjectAdapter.OnViewHolderLongClick<Project>{
    private TextView mResponseTv;
    private Context context;
    private ProjectAdapter adapter;
    private Project selected;
    private int selected_pos;
    private ProjectCalls api;


    public ProjectsFragment() {}

    @Override
    public boolean onLongClick(View view, int position, Project item) {
        if (!item.getManager().contains(getActivity().getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", null)))
            return false;
        if (selected == null ) {
            selected = item;
            selected_pos = position;
            item.toggleSelected();
        } else if (selected == item) {
            item.toggleSelected();
            selected = null;
            selected_pos = -1;
        } else {
            selected.toggleSelected();
            adapter.notifyItemChanged(selected_pos);
            item.toggleSelected();
            selected = item;
            selected_pos = position;
        }
        adapter.notifyItemChanged(position);
        return true;    }

    @Override
    public void onClick(View view, int position, Project item) {
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(project_changed event) {
        System.out.println("event");
        if (adapter != null)
            adapter.replace_item(event.getProject());
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        View view = getView();
        api = new ProjectCalls(view);

        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        if (adapter == null) {
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
            adapter = new ProjectAdapter(getActivity(), this, this);
            api.getProjects(current_page, adapter);
        }

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress = (ProgressBar) getView().findViewById(R.id.progressBar);
                progress.setVisibility(View.VISIBLE);
                adapter.clear();
                current_page = 1;
                progress.setVisibility(View.VISIBLE);
                api.getProjects(current_page, adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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




}
