package iguana.iguana.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import iguana.iguana.fragments.issue.IssueCreateFragment;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.fragments.project.BoardBaseFragment;
import iguana.iguana.fragments.project.ProjectDetailFragment;
import iguana.iguana.models.Project;

/**
 * Created by moritz on 21.08.17.
 */

public class ProjectDetailFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Details", "Issues", "Board"  };
    private Context context;
    private Project project;
    private Fragment mCurrentFragment;
    private Fragment mLastFragment;
    private int save_position;

    public ProjectDetailFragmentAdapter(FragmentManager fm, Context context, Project project) {
        super(fm);
        this.context = context;
        this.project = project;
        if (project.getCurrentsprint() != null) {
            tabTitles[1] = "Backlog";
            tabTitles[2] = "Sprintboard";
        }
    }



    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mLastFragment = mCurrentFragment;
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        if(save_position != position && mLastFragment != null &&  mLastFragment instanceof IssuesFragment)
            ((IssuesFragment) mLastFragment).invalidate();

        save_position = position;
        super.setPrimaryItem(container, position, object);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        return createdFragment;
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
                frag = new ProjectDetailFragment();
                d.putParcelable("project", project);
                frag.setArguments(d);
                return frag;
            case 1:
                frag = new IssuesFragment();
                d.putParcelable("project", project);
                frag.setArguments(d);
                return frag;
            case 2:
                frag = new BoardBaseFragment();
                d.putParcelable("project", project);
                frag.setArguments(d);
                return frag;

            default:
                frag = new ProjectDetailFragment();
                d.putParcelable("project", project);
                frag.setArguments(d);
                return new ProjectDetailFragment();
        }

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
