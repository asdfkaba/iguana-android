package iguana.iguana.fragments.main;

import android.app.Activity;
import android.app.FragmentManager;
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
import iguana.iguana.adapters.NotificationAdapter;
import iguana.iguana.adapters.ProjectAdapter;
import iguana.iguana.events.comment_changed;
import iguana.iguana.events.issue_arrived;
import iguana.iguana.events.issue_changed;
import iguana.iguana.events.notification_deleted;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.base.ApiScrollFragment;
import iguana.iguana.fragments.issue.IssueBaseFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Notification;
import iguana.iguana.models.Project;
import iguana.iguana.models.ProjectResult;

import java.util.HashMap;
import java.util.Map;

import iguana.iguana.remote.apicalls.IssueCalls;
import iguana.iguana.remote.apicalls.NotificationCalls;
import iguana.iguana.remote.apicalls.ProjectCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationFragment extends ApiScrollFragment implements NotificationAdapter.OnViewHolderClick<Notification>{
    private NotificationAdapter adapter;
    private Notification selected;
    private int selected_pos;
    private NotificationCalls api;
    private IssueCalls issue_api;

    public NotificationFragment() {}

    @Override
    public void onClick(View view, int position, Notification item) {
        String issue_str = item.getIssue();
        issue_str = issue_str.split(" ")[0];
        issue_api.getIssue(issue_str.split("-")[0], issue_str.split("-")[1]);
        getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.recycler_view).setVisibility(View.GONE);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(issue_arrived event) {
        issue_arrived stickyEvent = EventBus.getDefault().removeStickyEvent(issue_arrived.class);
        Issue issue = event.getIssue();
        FragmentManager frag = getActivity().getFragmentManager();
        IssueBaseFragment fragment= new IssueBaseFragment();
        Bundle d = new Bundle();
        d.putParcelable("issue", issue);
        d.putBoolean("comments", true);
        fragment.setArguments(d);
        FragmentTransaction ft = frag.beginTransaction();
        ft.replace(R.id.content_frame, fragment, "issue_detail");
        ft.addToBackStack("issue_detail");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(notification_deleted event) {
        notification_deleted stickyEvent = EventBus.getDefault().removeStickyEvent(notification_deleted.class);
        adapter.replace_item(event.getNotification());
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public void onPause() {
        super.onPause();
        invalidate();
    }

    public void invalidate() {
        if (selected != null) {
            selected.toggleSelected();
            selected = null;
            adapter.notifyItemChanged(selected_pos);
            selected_pos = -1;
        }
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        View view = getView();
        api = new NotificationCalls(view);
        issue_api = new IssueCalls(view);

        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        if (adapter == null) {
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
            adapter = new NotificationAdapter(getActivity(), this, null);
            api.getNotifications(current_page, adapter);
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
                api.getNotifications(current_page, adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
