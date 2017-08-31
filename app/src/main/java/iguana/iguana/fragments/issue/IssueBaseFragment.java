package iguana.iguana.fragments.issue;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
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
            issue = savedInstanceState.getParcelable("issue");
        }
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
