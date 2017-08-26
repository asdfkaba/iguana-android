package iguana.iguana.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import iguana.iguana.fragments.comment.CommentsFragment;
import iguana.iguana.fragments.issue.IssueDetailFragment;
import iguana.iguana.fragments.timelog.TimelogsFragment;
import iguana.iguana.models.Issue;

/**
 * Created by moritz on 21.08.17.
 */

public class IssueDetailFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 5;
    private String tabTitles[] = new String[] { "Details", "Comments", "Timelogs", "Commits", "Attachments" };
    private Context context;
    private Issue issue;

    public IssueDetailFragmentAdapter(FragmentManager fm, Context context, Issue issue) {
        super(fm);
        this.context = context;
        this.issue = issue;
    }


    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        Bundle d = new Bundle();
        switch (position) {
            case 0:
                frag = new IssueDetailFragment();
                d.putParcelable("issue", issue);
                frag.setArguments(d);
                return frag;
            case 1:
                frag = new CommentsFragment();
                d.putParcelable("issue", issue);
                frag.setArguments(d);
                return frag;
            case 2:
                frag = new TimelogsFragment();
                d.putParcelable("issue", issue);
                frag.setArguments(d);
                return frag;

        }
        frag = new IssueDetailFragment();
        d.putParcelable("issue", issue);
        frag.setArguments(d);
        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    public int getItemPosition(Object object) {

            // POSITION_NONE means something like: this fragment is no longer valid
            // triggering the ViewPager to re-build the instance of this fragment.
            return POSITION_NONE;

    }
}
