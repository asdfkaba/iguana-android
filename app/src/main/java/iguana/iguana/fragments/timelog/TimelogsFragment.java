package iguana.iguana.fragments.timelog;

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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import iguana.iguana.R;
import iguana.iguana.adapters.CommentAdapter;
import iguana.iguana.adapters.TimelogAdapter;
import iguana.iguana.events.comment_changed;
import iguana.iguana.events.timelog_changed;
import iguana.iguana.fragments.base.ApiScrollFragment;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Timelog;
import iguana.iguana.models.TimelogResult;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TimelogsFragment extends ApiScrollFragment implements TimelogAdapter.OnViewHolderClick<Timelog>, TimelogAdapter.OnViewHolderLongClick<Timelog>{
    private TimelogAdapter adapter;
    private Issue issue;
    private Timelog selected;
    private int selected_pos;

    public TimelogsFragment() {}

    public boolean onLongClick(View view, int position, Timelog item) {
        if (!item.getUser().equals(getActivity().getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", null)))
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
    public void onClick(View view, int position, Timelog item) {}

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(timelog_changed event) {
        if (adapter != null)
            adapter.replace_item(event.getTimelog());
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        if (getArguments() != null)
            issue = getArguments().getParcelable("issue");

        if (adapter == null) {
            if (issue == null)
                getTimelogs(current_page);
            else
                getTimelogsForIssue(current_page, issue);
            adapter = new TimelogAdapter(getActivity(), this, this);
        }
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress.setVisibility(View.VISIBLE);
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

    public void getTimelogs(Integer page) {
        Map options = new HashMap<String, String>();
        options.put("page", page);
        get_api_service().getTimelogs(options).enqueue(new Callback<TimelogResult>() {
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
        get_api_service().getTimelogsForIssue(issue.getProjectShortName(), issue.getNumber(), options).enqueue(new Callback<TimelogResult>() {
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView.setAdapter(new TimelogAdapter(getActivity(), this, this));
        return rootView;
    }


}
