package iguana.iguana.fragments.issue;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import iguana.iguana.R;
import iguana.iguana.adapters.IssueAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.events.issue_changed;
import iguana.iguana.fragments.base.ApiScrollFragment;
import iguana.iguana.fragments.calls.FragmentCalls;
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
    private IssueCalls api;
    private FragmentCalls calls;
    private String status;
    private Menu menu;
    protected BottomSheetLayout bottomSheetLayout;


    public IssuesFragment() {}

    public void update() {
        if (adapter != null) {
            Boolean new_reverse = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("issue_list_reverse" + (project != null ? project.getNameShort() : ""), false);
            String new_order = getActivity().getPreferences(Context.MODE_PRIVATE).getString("issue_list_order" + (project != null ? project.getNameShort() : ""), "number");
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
        calls.IssueBase(item);
    }

    public boolean onLongClick(View view, int position, Issue item) {
        showMenuSheet(MenuSheetView.MenuType.LIST, item);
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
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void showMenuSheet(final MenuSheetView.MenuType menuType, Issue item) {
        final Issue issue = item;
        final String curr_user = ((MainActivity) getActivity()).get_user();
        MenuSheetView menuSheetView =
                new MenuSheetView(getActivity(), menuType, item.getProjectShortName()+"-" + item.getNumber()+ " " + item.getTitle(), new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        HashMap body = new HashMap();
                        List<String> assignees, paricipants;
                        switch(item.getItemId()) {
                            case R.id.menu_issue_move_left_column:
                                body.put("kanbancol", project.getKanbancol().get(project.getKanbancol().indexOf(issue.getKanbancol()) - 1));
                                api.patchIssue(issue, body);
                                break;
                            case R.id.menu_issue_move_right_column:
                                body.put("kanbancol", project.getKanbancol().get(project.getKanbancol().indexOf(issue.getKanbancol()) + 1));
                                api.patchIssue(issue, body);
                                break;
                            case R.id.menu_issue_add_to_sprint:
                                body.put("sprint", project.getCurrentsprint().toString().split("-")[1]);
                                api.patchIssue(issue, body);
                                break;
                            case R.id.menu_issue_remove_from_sprint:
                                body.put("sprint", null);
                                api.patchIssue(issue, body);
                                break;
                            case R.id.menu_issue_assign_to_me:
                                assignees = issue.getAssignee();
                                assignees.add(curr_user);
                                body.put("assignee", assignees.toArray());
                                api.patchIssue(issue, body);
                                break;
                            case R.id.menu_issue_remove_from_me:
                                assignees = issue.getAssignee();
                                assignees.remove(curr_user);
                                body.put("assignee", assignees.toArray());
                                api.patchIssue(issue, body);
                                break;
                            case R.id.menu_issue_unfollow:
                                paricipants = issue.getParticipant();
                                paricipants.remove(curr_user);
                                System.out.println(paricipants.toString());
                                body.put("participant", paricipants.toArray());

                                api.patchIssue(issue, body);
                                break;
                            case R.id.menu_issue_follow:
                                paricipants = issue.getParticipant();
                                paricipants.add(curr_user);
                                body.put("participant", paricipants.toArray());
                                api.patchIssue(issue, body);
                                break;
                            case R.id.menu_issue_edit:
                                calls.IssueEdit(issue);
                                break;
                            case R.id.menu_issue_log_time:
                                calls.TimelogCreate(issue);
                                break;
                        }


                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.menu_issue);
        Menu menu = menuSheetView.getMenu();
        menu.findItem(R.id.menu_issue_edit).setTitle("Edit "+item.getProjectShortName()+"-" + item.getNumber());

        if (item.getAssignee().contains(curr_user))
            menu.findItem(R.id.menu_issue_remove_from_me).setVisible(true);
        else
            menu.findItem(R.id.menu_issue_assign_to_me).setVisible(true);
        if (item.getParticipant().contains(curr_user))
            menu.findItem(R.id.menu_issue_unfollow).setVisible(true);
        else
            menu.findItem(R.id.menu_issue_follow).setVisible(true);


        if (project != null && project.getCurrentsprint() != null) {
            if (item.getSprint() != null && item.getSprint().equals(project.getCurrentsprint()))
                menu.findItem(R.id.menu_issue_remove_from_sprint).setVisible(true);
            else
                menu.findItem(R.id.menu_issue_add_to_sprint).setVisible(true);
        }
        if(project != null && status != null) {
            String curr_column = item.getKanbancol();
            int idx = -1;

            for (int i = 0; i < project.getKanbancol().size(); i++) {
                if (project.getKanbancol().get(i).equals(item.getKanbancol())) {
                    idx = i;
                    break;
                }
            }
            if (idx > 0) {
                MenuItem left = menu.findItem(R.id.menu_issue_move_left_column);
                left.setTitle("Move to column " + project.getKanbancol().get(idx - 1));
                left.setVisible(true);
            }

            if (idx >= 0 && idx < project.getKanbancol().size()-1) {
                MenuItem right = menu.findItem(R.id.menu_issue_move_right_column);
                right.setTitle("Move to column " + project.getKanbancol().get(idx + 1));
                right.setVisible(true);
            }
        }
        menuSheetView.updateMenu();
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    public void onStart() {
        super.onStart();
        calls = new FragmentCalls(getActivity());
        EventBus.getDefault().register(this);
        View view = getView();
        api = new IssueCalls(view);
        bottomSheetLayout = (BottomSheetLayout) view.findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);



        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        if (project == null)
            project = getArguments().getParcelable("project");
        System.out.println(project);
        if  (status == null)
            status = getArguments().getString("status");

        if (adapter == null) {
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
            adapter_reverse = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("issue_list_reverse" + (project != null ? project.getNameShort() : "!!!"), false);
            adapter_order = getActivity().getPreferences(Context.MODE_PRIVATE).getString("issue_list_order" + (project != null ? project.getNameShort() : "!!!"), "number");
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
                calls.IssueCreate(project);
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
        editor.putString("issue_list_order" + (project != null ? project.getNameShort() : "!!!"), adapter_order);
        editor.putBoolean("issue_list_reverse" + (project != null ? project.getNameShort() : "!!!"), adapter_reverse);
        editor.commit();
        return true;
    }

}
