package iguana.iguana.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import iguana.iguana.fragments.issue.IssueCreateFragment;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.fragments.project.ProjectDetailFragment;
import iguana.iguana.models.Project;

/**
 * Created by moritz on 21.08.17.
 */

public class ProjectDetailFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Detail", "Issues", "Create" };
    private Context context;
    private Project project;

    public ProjectDetailFragmentAdapter(FragmentManager fm, Context context, Project project) {
        super(fm);
        this.context = context;
        this.project = project;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // get the tags set by FragmentPagerAdapter

                System.out.println(createdFragment.getTag());

        // ... save the tags somewhere so you can reference them later
        return createdFragment;
    }

    private static String makeFragmentName(int viewPagerId, int index) {
        if (index == 1)
            return "issuelist";
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
                System.out.println("adapter case 0");
                frag = new ProjectDetailFragment();
                ((ProjectDetailFragment)frag).setProject(project.getNameShort());
                return frag;
            case 1:
                System.out.println("adapter case 1");
                frag = new IssuesFragment();
                d.putString("project", project.getNameShort());
                frag.setArguments(d);
                return frag;
            case 2:
                System.out.println("adapter case 3");

                frag = new IssueCreateFragment();
                d.putString("project", project.getNameShort());
                frag.setArguments(d);
                return frag;
        }
        frag = new ProjectDetailFragment();
        ((ProjectDetailFragment)frag).setProject(project.getNameShort());
        return new ProjectDetailFragment();
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
