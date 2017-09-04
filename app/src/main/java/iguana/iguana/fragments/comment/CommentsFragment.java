package iguana.iguana.fragments.comment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iguana.iguana.R;
import iguana.iguana.adapters.CommentAdapter;

import iguana.iguana.adapters.TimelogAdapter;
import iguana.iguana.events.comment_changed;
import iguana.iguana.events.issue_changed;
import iguana.iguana.fragments.base.ApiScrollFragment;
import iguana.iguana.models.Comment;
import iguana.iguana.models.CommentResult;
import iguana.iguana.models.Issue;

import iguana.iguana.models.Notification;
import iguana.iguana.remote.apicalls.CommentCalls;
import iguana.iguana.remote.apicalls.NotificationCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommentsFragment extends ApiScrollFragment implements CommentAdapter.OnViewHolderClick<Comment>, CommentAdapter.OnViewHolderLongClick<Comment>{
    private List<Comment> comments;
    private TextView mResponseTv;
    private Context context;
    private CommentAdapter adapter;
    private Issue issue;
    private Comment selected;
    private int selected_pos;
    private CommentCalls api;


    public CommentsFragment() {}

    public boolean onLongClick(View view, int position, Comment item) {
        if (!item.getCreator().equals(getActivity().getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", null)))
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
        return true;
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

    @Override
    public void onClick(View view, int position, Comment item) {}

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(comment_changed event) {
        comment_changed stickyEvent = EventBus.getDefault().removeStickyEvent(comment_changed.class);
        if (adapter != null && stickyEvent != null)
            adapter.replace_item(event.getComment(), event.deleted());
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
        api = new CommentCalls(view);

        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        if(getArguments() != null)
            issue = getArguments().getParcelable("issue");

        // clear  notifications for this issue;
        NotificationCalls noti_api = new NotificationCalls(getView());
        noti_api.deleteNotification(issue.getProjectShortName()+"-"+issue.getNumber());

        if (adapter == null) {
            progress.setVisibility(View.VISIBLE);
            adapter = new CommentAdapter(getActivity(), this, this);
            api.getComments(current_page, issue.getProjectShortName(), issue.getNumber(),  adapter);
        }

        recyclerView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress.setVisibility(View.VISIBLE);
                adapter.clear();
                current_page = 1;
                api.getComments(current_page, issue.getProjectShortName(), issue.getNumber(),  adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.add).setVisible(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                calls.CommentCreate(issue);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (issue != null)
            outState.putParcelable("issue", issue);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            issue = savedInstanceState.getParcelable("issue");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView.setAdapter(new CommentAdapter(getActivity(), this, this));
        return rootView;
    }


}
