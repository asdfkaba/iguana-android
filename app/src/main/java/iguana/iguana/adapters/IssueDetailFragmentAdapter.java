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
    private String tabTitles[] = new String[] { "Detail", "Comments", "Timelogs", "Commits", "Attachments" };
    private Context context;
    private Issue issue;

    public IssueDetailFragmentAdapter(FragmentManager fm, Context context, Issue issue) {
        super(fm);
        this.context = context;
        this.issue = issue;

    }

    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // get the tags set by FragmentPagerAdapter

                System.out.println(createdFragment.getTag());

        // ... save the tags somewhere so you can reference them later
        return createdFragment;
    }

    private static String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
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
