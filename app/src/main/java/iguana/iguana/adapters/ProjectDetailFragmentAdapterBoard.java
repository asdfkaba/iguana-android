package iguana.iguana.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.flipboard.bottomsheet.BottomSheetLayout;

import iguana.iguana.R;
import iguana.iguana.fragments.issue.IssueCreateFragment;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.fragments.project.BoardBaseFragment;
import iguana.iguana.fragments.project.ProjectDetailFragment;
import iguana.iguana.models.Project;

/**
 * Created by moritz on 21.08.17.
 */

public class ProjectDetailFragmentAdapterBoard extends FragmentPagerAdapter {
    final int PAGE_COUNT;
    private String tabTitles[];
    private Context context;
    private Project project;
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

    public ProjectDetailFragmentAdapterBoard(FragmentManager fm, Context context, Project project) {
        super(fm);
        this.context = context;
        this.project = project;
        this.tabTitles = project.getKanbancol().toArray(new String[0]);
        this.PAGE_COUNT = project.getKanbancol().size();
        System.out.println(project.getKanbancol());
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
            default:
                frag = new IssuesFragment();
                d.putParcelable("project", project);
                d.putString("status", tabTitles[position]);
                frag.setArguments(d);
                return frag;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public int getItemPosition(Object object) {
            return POSITION_NONE;
    }
}
