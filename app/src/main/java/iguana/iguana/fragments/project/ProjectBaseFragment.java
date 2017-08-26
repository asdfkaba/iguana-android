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

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iguana.iguana.R;
import iguana.iguana.adapters.ProjectDetailFragmentAdapter;
import iguana.iguana.common.view.SlidingTabLayout;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;

/**
 * A basic sample which shows how to use {@link com.example.android.common.view.SlidingTabLayout}
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class ProjectBaseFragment extends Fragment {

    private Project project;
    private SlidingTabLayout mSlidingTabLayout;
    private ProjectDetailFragmentAdapter adapter;
    private ViewPager mViewPager;

    public Project getProject() {
        return this.project;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("project", project);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            project = savedInstanceState.getParcelable("project");
        }
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

        mViewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

        adapter = new ProjectDetailFragmentAdapter(getChildFragmentManager(), getActivity(), project);
        mViewPager.setAdapter(adapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
