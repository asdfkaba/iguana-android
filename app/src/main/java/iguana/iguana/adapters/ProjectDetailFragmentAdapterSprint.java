package iguana.iguana.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.flipboard.bottomsheet.BottomSheetLayout;

import iguana.iguana.R;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.models.Project;

/**
 * Created by moritz on 21.08.17.
 */

public class ProjectDetailFragmentAdapterSprint extends FragmentStatePagerAdapter {
    final int PAGE_COUNT;
    private String [] tabTitles;
    private Context context;
    private Project project;
    private String sprint;
    private Fragment mCurrentFragment;
    private Fragment mLastFragment;
    private int save_position;

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (mLastFragment != null) {
            BottomSheetLayout view = (BottomSheetLayout) mLastFragment.getView().findViewById(R.id.bottomsheet);
            if (view != null && view.isSheetShowing())
                view.dismissSheet();
        }
        mLastFragment = mCurrentFragment;
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
            ((IssuesFragment) mCurrentFragment).update();
        }

        save_position = position;
        super.setPrimaryItem(container, position, object);
    }

    public ProjectDetailFragmentAdapterSprint(FragmentManager fm, Context context, Project project, String sprint) {
        super(fm);
        this.context = context;
        this.project = project;
        this.sprint = sprint;
        if (sprint != null && sprint != "dummy") {
            this.tabTitles = new String[2];
            this.tabTitles[0] = "Backlog";
            this.tabTitles[1] = "Sprint " + sprint;
            this.PAGE_COUNT = 2;
        }
        else {
            this.tabTitles = new String[1];
            this.tabTitles[0] = "Backlog";
            this.PAGE_COUNT = 1;
        }
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
                frag = new IssuesFragment();
                d.putParcelable("project", project);
                d.putString("sprint", sprint);
                d.putString("sprintview", "no");
                frag.setArguments(d);
                return frag;
            case 1:
                frag = new IssuesFragment();
                d.putParcelable("project", project);
                d.putString("sprint", sprint);
                d.putString("sprintview", "yes");
                frag.setArguments(d);
                return frag;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public int getItemPosition(Object object) {
            return POSITION_NONE;
    }
}
