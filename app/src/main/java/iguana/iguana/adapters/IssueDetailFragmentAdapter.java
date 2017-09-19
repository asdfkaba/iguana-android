package iguana.iguana.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import iguana.iguana.app.MainActivity;
import iguana.iguana.fragments.calls.FragmentCalls;
import iguana.iguana.fragments.comment.CommentsFragment;
import iguana.iguana.fragments.issue.IssueDetailFragment;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.fragments.main.DashboardFragment;
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
    private Fragment mLastFragment;
    private Fragment mCurrentFragment;
    private int save_position;
    private FragmentManager fm;

    public IssueDetailFragmentAdapter(FragmentManager fm, Context context, Issue issue) {
        super(fm);
        this.context = context;
        this.issue = issue;
        this.fm = fm;
    }

    public void set_issue(Issue issue) {
        this.issue = issue;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mLastFragment = mCurrentFragment;
        if (mCurrentFragment != object) {
            mCurrentFragment = ((Fragment) object);
        }
        if(save_position != position && mLastFragment != null) {
            if (mLastFragment instanceof TimelogsFragment)
                ((TimelogsFragment) mLastFragment).invalidate();
            if (mLastFragment instanceof CommentsFragment)
                ((CommentsFragment) mLastFragment).invalidate();
        }
        save_position = position;
        super.setPrimaryItem(container, position, object);
    }


    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        switch (position) {
            case 0:
                frag = new IssueDetailFragment();
                break;
            case 1:
                frag = new CommentsFragment();
                break;
            case 2:
                frag = new TimelogsFragment();
                break;
            default:
                frag = new DashboardFragment();
                break;
        }
        Bundle d = new Bundle();
        d.putParcelable("issue", issue);
        frag.setArguments(d);
        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public int getItemPosition(Object object) {
            return POSITION_NONE;
    }
}
