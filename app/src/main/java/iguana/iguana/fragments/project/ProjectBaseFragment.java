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

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.ActionMenuItem;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import iguana.iguana.R;
import iguana.iguana.adapters.ProjectDetailFragmentAdapter;
import iguana.iguana.common.view.SlidingTabLayout;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.calls.FragmentCalls;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;


public class ProjectBaseFragment extends Fragment {

    private Project project;
    private SlidingTabLayout mSlidingTabLayout;
    private ProjectDetailFragmentAdapter adapter;
    private ViewPager mViewPager;
    private FragmentCalls calls;

    public ProjectDetailFragmentAdapter getAdapter() {
        return adapter;
    }

    public Project getProject() {
        return this.project;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("project", project);
        super.onSaveInstanceState(outState);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            project = savedInstanceState.getParcelable("project");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(project_changed event) {
        Project new_project = event.getProject();
        if (new_project.getNameShort().equals(project.getNameShort()))
            this.project = new_project;
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        calls = new FragmentCalls(getActivity());
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!project.getManager().contains(getActivity().getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", null)))
            return;

        menu.findItem(R.id.sprints).setVisible(true);
        menu.findItem(R.id.edit_option).setVisible(true);
        menu.findItem(R.id.edit_option).setTitle("Edit " + project.getNameShort());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_option:
                calls.ProjectEdit(project);
                break;
            case R.id.sprints:
                calls.ProjectSprints(project);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swipe_tabs_base, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (project == null)
            project = getArguments().getParcelable("project");

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        adapter = new ProjectDetailFragmentAdapter(getChildFragmentManager(), getActivity(), project);
        mViewPager.setAdapter(adapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);

        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
