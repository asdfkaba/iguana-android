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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import iguana.iguana.R;
import iguana.iguana.adapters.ProjectDetailFragmentAdapter;
import iguana.iguana.adapters.ProjectDetailFragmentAdapterBoard;
import iguana.iguana.common.view.SlidingTabLayout;
import iguana.iguana.models.Project;

/**
 * A basic sample which shows how to use {@link com.example.android.common.view.SlidingTabLayout}
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class BoardBaseFragment extends Fragment {

    private Project project;
    private SlidingTabLayout mSlidingTabLayout;
    private ProjectDetailFragmentAdapterBoard adapter;
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

    public ProjectDetailFragmentAdapterBoard getAdapter() {
        return adapter;
    }

    public void notify_adapters() {
        adapter.setRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board_base, container, false);
    }

    private class OnTouch implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (project == null)
            project = getArguments().getParcelable("project");

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        adapter = new ProjectDetailFragmentAdapterBoard(getChildFragmentManager(), getActivity(), project);
        mViewPager.setAdapter(adapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        // mSlidingTabLayout.setOnTouchListener(new OnTouch());
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
