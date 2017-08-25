package iguana.iguana.fragments.issue;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iguana.iguana.R;
import iguana.iguana.adapters.IssueDetailFragmentAdapter;
import iguana.iguana.common.view.SlidingTabLayout;
import iguana.iguana.models.Issue;

public class IssueBaseFragment extends Fragment {
    private Issue issue;
    private SlidingTabLayout mSlidingTabLayout;
    private IssueDetailFragmentAdapter adapter;
    private ViewPager mViewPager;

    public Issue getIssue() {
        return this.issue;
    }

    public void onSaveInstanceState(Bundle outState) {
        System.out.println("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (issue != null)
            outState.putParcelable("issue", issue);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        System.out.println("onActivityCreated");
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
        mViewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        if (issue == null)
            issue = getArguments().getParcelable("issue");
        adapter = new IssueDetailFragmentAdapter(getChildFragmentManager(), getActivity(), issue);
        mViewPager.setAdapter(adapter);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
