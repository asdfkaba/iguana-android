/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package iguana.iguana.fragments.project;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import iguana.iguana.R;
import iguana.iguana.adapters.ProjectDetailFragmentAdapterBoard;
import iguana.iguana.adapters.ProjectDetailFragmentAdapterSprint;
import iguana.iguana.common.view.SlidingTabLayout;
import iguana.iguana.events.project_changed;
import iguana.iguana.events.sprint_changed;
import iguana.iguana.fragments.calls.FragmentCalls;
import iguana.iguana.models.Project;
import iguana.iguana.models.Sprint;
import iguana.iguana.remote.apicalls.SprintCalls;

/**
 * A basic sample which shows how to use {@link com.example.android.common.view.SlidingTabLayout}
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class SprintBaseFragment extends Fragment {

    private Project project;
    private SlidingTabLayout mSlidingTabLayout;
    private ProjectDetailFragmentAdapterSprint adapter;
    private ViewPager mViewPager;
    private String sprint;
    private SprintCalls api;
    private Menu menu;
    private List<Sprint> current;

    public Project getProject() {
        return this.project;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("project", project);
        outState.putString("sprint", sprint);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            project = savedInstanceState.getParcelable("project");
            sprint = savedInstanceState.getString("sprint");
        }
    }

    public ProjectDetailFragmentAdapterSprint getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swipe_tabs_base, container, false);
    }

    private void initSprintMenu(Menu menu, Integer remove, Sprint new_sprint) {
        Menu sprintmenu = menu.findItem(R.id.selectSprint).getSubMenu();
        menu.findItem(R.id.selectSprint).setTitle("Sprint ▼");
        Integer running = -1;
        if (project.getCurrentsprint() != null)
            running = Integer.parseInt(project.getCurrentsprint().split("-")[1]);
        System.out.println(project.getSprints());
        for (int i = 0; i < project.getSprints().size(); i++) {
            Integer nr = project.getSprints().get(i).getSeqnum();
            sprintmenu.add(R.id.sprint_group, 87456, 0, "Sprint " + nr + (running != -1 && nr == running ? " (running)" : "" ));
        }
        sprintmenu.add(R.id.sprint_group, 87457, 0, "New Sprint" );

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // show order by menu entry; set default order
        super.onCreateOptionsMenu(menu, inflater);
        if (getUserVisibleHint()) {
            this.menu = menu;
            if (project != null && sprint != null) {
                menu.findItem(R.id.selectSprint).setVisible(true);
                initSprintMenu(menu, null, null);
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(sprint_changed event) {
        if (event.getSprint().getEnddate() != null) {
        } else if (event.getSprint().getStartdate() != null) {
            initSprintMenu(menu, null, null);
        } else {
            initSprintMenu(menu, null, event.getSprint());
        }
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 87456:
                String number = item.getTitle().toString().split(" ")[1];
                adapter = new ProjectDetailFragmentAdapterSprint(getChildFragmentManager(), getActivity(), project, number);
                mViewPager.setAdapter(adapter);
                mSlidingTabLayout.setViewPager(mViewPager);
                break;
            case 87457:
                api.createSprint(project);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        if (project == null)
            project = getArguments().getParcelable("project");

        sprint = "dummy";
        if (project.getCurrentsprint() != null)
            sprint = project.getCurrentsprint().split("-")[1];
        else if (project.getSprints().size() > 0 )
            sprint = project.getSprints().get(0).getSeqnum().toString();
        System.out.println(sprint);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        api = new SprintCalls(view);

        current = project.getSprints();

        adapter = new ProjectDetailFragmentAdapterSprint(getChildFragmentManager(), getActivity(), project, sprint);
        mViewPager.setAdapter(adapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        // mSlidingTabLayout.setOnTouchListener(new OnTouch());
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
