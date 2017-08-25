package iguana.iguana.fragments.issue;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import iguana.iguana.R;
import iguana.iguana.adapters.IssueAdapter;
import iguana.iguana.fragments.ApiFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.IssueResult;
import iguana.iguana.remote.APIService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssuesFragment extends ApiFragment implements IssueAdapter.OnViewHolderClick<Issue>, IssueAdapter.OnViewHolderLongClick<Issue> {
    private APIService mAPIService;
    private TextView mResponseTv;
    private Context context;
    private IssueAdapter adapter;
    private String project;
    private Integer current_page;
    private RecyclerView recyclerView;
    private String adapter_order = "number";
    private boolean adapter_reverse = false;
    ProgressBar progress;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout last_clicked;
    private int scroll_y;
    private int scroll_y2;
    private boolean refresh=false;
    private View view;

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

    private void do_changes(LinearLayout issue_item, Issue issue) {
        final Issue item = issue;
        issue_item.setRotation(2.0f);
        ImageButton edit = (ImageButton) issue_item.findViewById(R.id.edit);
        edit.setVisibility(View.VISIBLE);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager frag;
                frag = ((Activity)v.getContext()).getFragmentManager();
                IssueEditFragment fragment= new IssueEditFragment();
                Bundle d = new Bundle();
                d.putParcelable("issue", item);
                fragment.setArguments(d);
                FragmentTransaction ft = frag.beginTransaction();
                ft.replace(R.id.content_frame, fragment, "issue_edit");
                ft.addToBackStack("issue_edit");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

    }

    private void undo_changes(LinearLayout issue_item) {
        issue_item.setRotation(0.0f);
        ImageButton edit = (ImageButton) issue_item.findViewById(R.id.edit);
        edit.setOnClickListener(null);
        edit.setVisibility(View.GONE);
    }

    public boolean onLongClick(View view, int position, Issue item) {
        if (last_clicked != null ) {
            undo_changes(last_clicked);
        }
        if (last_clicked != view) {
            last_clicked = (LinearLayout) view;
            do_changes(last_clicked, item);
        } else {
            last_clicked = null;
        }
        return true;
    }

    public void onPause() {
        super.onPause();
        scroll_y = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        View startView = recyclerView.getChildAt(0);
        scroll_y2 = 0;
        //(startView == null) ? 0 : (startView.getTop() - recyclerView.getPaddingTop());

    }

    public void onResume() {
        super.onResume();
        if (scroll_y!= -1) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(scroll_y, scroll_y2);
        }

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("adapter_order", adapter_order);
        outState.putBoolean("adapter_reverse", adapter_reverse);
        outState.putInt("scroll", scroll_y);

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
                adapter_order  = savedInstanceState.getString("adapter_order");
                adapter_reverse = savedInstanceState.getBoolean("adapter_reverse");
                scroll_y = savedInstanceState.getInt("scroll");

            }
    }

    public void replace_item(Issue item) {
        adapter.replace_item(item);
    }

    public void onStart() {
        super.onStart();

        view = getView();
        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);

        current_page = 1;
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.scrollToPosition(scroll_y);
        if (adapter == null) {
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
            adapter_reverse = getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("issue_list_reverse" + (project != null ? project : ""), false);
            adapter_order = getActivity().getPreferences(Context.MODE_PRIVATE).getString("issue_list_order" + (project != null ? project : ""), "number");
            adapter = new IssueAdapter(getActivity(), this, this);
            adapter.set_order(adapter_order);
            adapter.set_reverse(adapter_reverse);
            if (project.equals("None"))
                getIssues(current_page);
            else
                getProjectIssues(project, current_page);
        }
        recyclerView.setAdapter(adapter);



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress = (ProgressBar) getView().findViewById(R.id.progressBar);
                progress.setVisibility(View.VISIBLE);
                adapter.clear();
                current_page = 1;
                if (project.equals("None"))
                    getIssues(current_page);
                else
                    getProjectIssues(project, current_page);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // show order by menu entry; set default order
        menu.findItem(R.id.menuSort).setVisible(true);
        if (adapter != null) {
            switch (adapter_order) {
                case "number":
                    menu.findItem(R.id.menuSortNumber).setChecked(true); break;
                case "priority":
                    menu.findItem(R.id.menuSortPriority).setChecked(true); break;
                case "storypoints":
                    menu.findItem(R.id.menuSortStorypoints).setChecked(true); break;
                case "title":
                    menu.findItem(R.id.menuSortTitle).setChecked(true); break;
                case "type":
                    menu.findItem(R.id.menuSortType).setChecked(true); break;
            }
            if (adapter_reverse)
                menu.findItem(R.id.menuSortReverse).setChecked(true);


        }
        // in project context show add icon
        if (!project.equals("None"))
            menu.findItem(R.id.add).setVisible(true);


        super.onCreateOptionsMenu(menu, inflater);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        switch (item.getItemId()) {


            // create new issue; start IssueCreateFragment
            case R.id.add:
                IssueCreateFragment fragment= new IssueCreateFragment();
                Bundle d = new Bundle();
                d.putString("project", project);
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
                if (item.isChecked()) {
                    item.setChecked(false);
                    adapter_reverse = false;
                }
                else {
                    item.setChecked(true);
                    adapter_reverse = true;
                }
                adapter.do_notify();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        editor.putString("issue_list_order" + (project != null ? project : ""), adapter_order);
        editor.putBoolean("issue_list_reverse" + (project != null ? project : ""), adapter_reverse);
        editor.commit();
        return true;
    }


    public IssuesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        project =  getArguments().getString("project");
        return inflater.inflate(R.layout.recyclerview_list, container, false);
    }


    public void getIssues(Integer page) {
        Map options = new HashMap<String,String>();
        options.put("page", page);

        get_api_service().getIssues(options).enqueue(new Callback<IssueResult>() {
            @Override
            public void onResponse(Call<IssueResult> call, Response<IssueResult> response) {
                if(response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getIssues(++current_page);
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.do_notify();
                        recyclerView.setVisibility(View.VISIBLE);

                    }
                }
                else {
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<IssueResult> call, Throwable t) {
                Toast.makeText(getActivity(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

        public void getProjectIssues(String project, Integer page) {
            Map options = new HashMap<String,String>();
            options.put("page", page);

            get_api_service().getProjectIssues(project,options).enqueue(new Callback<IssueResult>() {
                @Override
                public void onResponse(Call<IssueResult> call, Response<IssueResult> response) {
                    if(response.isSuccessful()) {
                        adapter.addAll(response.body().getResults());

                        if (response.body().getNext() != null) {
                            getProjectIssues(response.body().getResults().get(0).getProjectShortName(), ++current_page);
                        } else {
                            progress.setVisibility(View.GONE);
                            adapter.do_notify();
                            recyclerView.setVisibility(View.VISIBLE);

                        }

                    }

                }

                @Override
                public void onFailure(Call<IssueResult> call, Throwable t) {
                    Toast.makeText(getActivity(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_LONG).show();
                    progress.setVisibility(View.GONE);
                }
            });
        }
}
