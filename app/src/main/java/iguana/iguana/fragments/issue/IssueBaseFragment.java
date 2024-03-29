package iguana.iguana.fragments.issue;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import iguana.iguana.R;
import iguana.iguana.adapters.IssueDetailFragmentAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.common.view.SlidingTabLayout;
import iguana.iguana.events.comment_changed;
import iguana.iguana.events.issue_arrived;
import iguana.iguana.events.issue_changed;
import iguana.iguana.events.rtoken_invalid;
import iguana.iguana.fragments.calls.FragmentCalls;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Token;
import iguana.iguana.remote.ApiUtils;
import iguana.iguana.remote.apicalls.IssueCalls;
import retrofit2.Call;

public class IssueBaseFragment extends Fragment {
    private Issue issue;
    private String issue_str;
    private SlidingTabLayout mSlidingTabLayout;
    private IssueDetailFragmentAdapter adapter;
    private ViewPager mViewPager;
    private FragmentCalls calls;

    public Issue getIssue() {
        return this.issue;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (issue != null)
            outState.putParcelable("issue", issue);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (issue == null)
                issue = savedInstanceState.getParcelable("issue");
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(issue_changed event) {
        Issue new_issue = event.getIssue();
        if (issue.getNumber().equals(new_issue.getNumber()) && issue.getProjectShortName().equals(new_issue.getProjectShortName()))
            issue = new_issue;
            adapter.set_issue(issue);

    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        calls = new FragmentCalls(getActivity());
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // show order by menu entry; set default order
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.edit_option).setVisible(true);
        menu.findItem(R.id.edit_option).setTitle("Edit " + issue.getProjectShortName()+"-"+issue.getNumber());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_option:
                calls.IssueEdit(issue);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swipe_tabs_base, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (issue == null)
            issue = getArguments().getParcelable("issue");
        mViewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        adapter = new IssueDetailFragmentAdapter(getChildFragmentManager(), getActivity(), issue);
        mViewPager.setAdapter(adapter);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        if (getArguments().getBoolean("comments"))
            mViewPager.setCurrentItem(1);
    }
}
