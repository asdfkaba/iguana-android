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

public class ProjectDetailFragmentAdapterBoard extends FragmentPagerAdapter {
    final int PAGE_COUNT;
    private String tabTitles[];
    private Context context;
    private Project project;

    public ProjectDetailFragmentAdapterBoard(FragmentManager fm, Context context, Project project) {
        super(fm);
        this.context = context;
        this.project = project;
        this.tabTitles = project.getKanbancol().toArray(new String[0]);
        this.PAGE_COUNT = project.getKanbancol().size();
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
        // Generate title based on item position
        return tabTitles[position];
    }

    public int getItemPosition(Object object) {

            // POSITION_NONE means something like: this fragment is no longer valid
            // triggering the ViewPager to re-build the instance of this fragment.
            return POSITION_NONE;

    }
}
