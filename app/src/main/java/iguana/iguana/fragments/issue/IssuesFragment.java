package iguana.iguana.fragments.issue;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import iguana.iguana.R;
import iguana.iguana.adapters.IssueAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.issue_changed;
import iguana.iguana.fragments.base.ApiScrollFragment;
import iguana.iguana.fragments.project.BoardBaseFragment;
import iguana.iguana.fragments.project.ProjectBaseFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;
import iguana.iguana.remote.apicalls.IssueCalls;


public class IssuesFragment
        extends ApiScrollFragment
        implements
        IssueAdapter.OnViewHolderClick<Issue>,
        IssueAdapter.OnViewHolderLongClick<Issue> {

    private IssueAdapter adapter;
    private Project project;
    private String adapter_order = "number";
    private boolean adapter_reverse = false;
    private Issue selected;
    private int selected_pos;
    private IssueCalls api;
    private String status;
    private Menu menu;

    public IssuesFragment() {}

    public void update() {
        if (adapter != null) {
            Boolean new_reverse = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("issue_list_reverse" + (project != null ? project.getNameShort() : "") + (status != null ? "board": "!!!"), false);
            String new_order = getActivity().getPreferences(Context.MODE_PRIVATE).getString("issue_list_order" + (project != null ? project.getNameShort() : "") + (status != null ? "board": "!!!"), "number");
            if (new_reverse != adapter_reverse || new_order != adapter_order) {
                adapter_order = new_order;
                adapter_reverse = new_reverse;
                adapter.set_order(new_order);
                adapter.set_reverse(new_reverse);
                adapter.do_notify();
            }
        }
    }

    @Override
    public void onClick(View view, int position, Issue item) {
        FragmentManager frag = ((Activity)view.getContext()).getFragmentManager();
        IssueBaseFragment fragment= new IssueBaseFragment();
        Bundle d = new Bundle();
        d.putParcelable("issue", item);
        fragment.setArguments(d);
        FragmentTransaction ft = frag.beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack("visible_fragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public boolean onLongClick(View view, int position, Issue item) {
        if (selected == null ) {
            selected = item;
            selected_pos = position;
            item.toggleSelected();
        } else if (selected == item) {
            item.toggleSelected();
            selected = null;
            selected_pos = -1;
        } else {
            selected.toggleSelected();
            adapter.notifyItemChanged(selected_pos);
            item.toggleSelected();
            selected = item;
            selected_pos = position;
        }
        adapter.notifyItemChanged(position);
        return true;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("adapter_order", adapter_order);
        outState.putBoolean("adapter_reverse", adapter_reverse);
        outState.putParcelable("project", project);

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
                adapter_order  = savedInstanceState.getString("adapter_order");
                adapter_reverse = savedInstanceState.getBoolean("adapter_reverse");
                project  = savedInstanceState.getParcelable("project");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(issue_changed event) {
        if (adapter != null)
            adapter.replace_item(event.getIssue(), ((MainActivity) getActivity()).get_user());
    }

    @Override
    public void onPause() {
        super.onPause();
        invalidate();
    }

    public void invalidate() {
        if (selected != null) {
            selected.toggleSelected();
            selected = null;
            adapter.notifyItemChanged(selected_pos);
            selected_pos = -1;
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
        View view = getView();
        api = new IssueCalls(view);

        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        if (project == null)
            project = getArguments().getParcelable("project");
        if  (status == null)
            status = getArguments().getString("status");

        if (adapter == null) {
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
            adapter_reverse = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("issue_list_reverse" + (project != null ? project.getNameShort() : "!!!") + (status != null ? "board": "!!!"), false);
            adapter_order = getActivity().getPreferences(Context.MODE_PRIVATE).getString("issue_list_order" + (project != null ? project.getNameShort() : "!!!") + (status != null ? "board": "!!!"), "number");
            adapter = new IssueAdapter(getActivity(), this, this, project, status, view);
            adapter.set_order(adapter_order);
            adapter.set_reverse(adapter_reverse);
            if (project == null)
                api.getIssues(current_page, adapter);
            else
                api.getProjectIssues(project, current_page, adapter, status);
        }

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress = (ProgressBar) getView().findViewById(R.id.progressBar);
                progress.setVisibility(View.VISIBLE);
                adapter.clear();
                current_page = 1;
                if (project == null)
                    api.getIssues(current_page, adapter);
                else
                    api.getProjectIssues(project, current_page, adapter, status);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

   private void set_menu_items(Menu menu, String adapter_order, boolean adapter_reverse) {
       switch (adapter_order) {
           case "number":
               menu.findItem(R.id.menuSortNumber).setChecked(true);
               break;
           case "priority":
               menu.findItem(R.id.menuSortPriority).setChecked(true);
               break;
           case "storypoints":
               menu.findItem(R.id.menuSortStorypoints).setChecked(true);
               break;
           case "title":
               menu.findItem(R.id.menuSortTitle).setChecked(true);
               break;
           case "type":
               menu.findItem(R.id.menuSortType).setChecked(true);
               break;
       }
       if (adapter_reverse)
           menu.findItem(R.id.menuSortReverse).setChecked(true);

   }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // show order by menu entry; set default order
        super.onCreateOptionsMenu(menu, inflater);
        if(getUserVisibleHint()) {
            menu.findItem(R.id.menuSort).setVisible(true);
            System.out.println(adapter_order + adapter_reverse);
            System.out.println(this + this.status);
            if (adapter != null) {
                set_menu_items(menu, adapter_order, adapter_reverse);
            }
            // in project context show add icon
            if (project != null && status == null)
                menu.findItem(R.id.add).setVisible(true);
        }



    }

    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        switch (item.getItemId()) {
            // create new issue; start IssueCreateFragment
            case R.id.add:
                IssueCreateFragment fragment= new IssueCreateFragment();
                Bundle d = new Bundle();
                d.putParcelable("project", project);
                fragment.setArguments(d);
                FragmentTransaction ft = getParentFragment().getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "visible_fragment");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                return true;
            // set adapter order type; notify_adapter to reorder
            case R.id.menuSortTitle:
                adapter.set_order("title");
                adapter_order = "title";
                item.setChecked(true);
                adapter.do_notify();
                break;
            case R.id.menuSortType:
                adapter.set_order("type");
                adapter_order = "type";
                item.setChecked(true);
                adapter.do_notify();
                break;
            case R.id.menuSortPriority:
                adapter.set_order("priority");
                adapter_order = "priority";
                item.setChecked(true);
                adapter.do_notify();
                break;
            case R.id.menuSortStorypoints:
                adapter.set_order("storypoints");
                adapter_order = "storypoints";
                item.setChecked(true);
                adapter.do_notify();
                break;
            case R.id.menuSortNumber:
                adapter.set_order("number");
                adapter_order = "number";
                item.setChecked(true);
                adapter.do_notify();
                break;
            case R.id.menuSortReverse:
                adapter.toggleReverse();
                adapter_reverse = !adapter_reverse;
                if (item.isChecked()) {
                    item.setChecked(false);
                }
                else {
                    item.setChecked(true);
                }
                adapter.do_notify();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        System.out.println("issue_list_order" + (project != null ? project.getNameShort() : "!!!") + (status != null ? "board": "!!!"));
        editor.putString("issue_list_order" + (project != null ? project.getNameShort() : "!!!") + (status != null ? "board": "!!!"), adapter_order);
        editor.putBoolean("issue_list_reverse" + (project != null ? project.getNameShort() : "!!!") + (status != null ? "board": "!!!"), adapter_reverse);
        editor.commit();
        return true;
    }

}
