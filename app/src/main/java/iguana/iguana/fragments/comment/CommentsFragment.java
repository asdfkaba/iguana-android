package iguana.iguana.fragments.comment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.adapters.CommentAdapter;

import iguana.iguana.models.Comment;
import iguana.iguana.models.CommentResult;
import iguana.iguana.models.Issue;

import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsFragment extends Fragment implements CommentAdapter.OnViewHolderClick<Comment>{
    private APIService mAPIService;
    private List<Comment> comments;
    private TextView mResponseTv;
    private Context context;
    private CommentAdapter adapter;
    private Issue issue;

    private RecyclerView recyclerView;
    private Integer current_page;
    ProgressBar progress;
    SwipeRefreshLayout swipeRefreshLayout;


    public void onStart() {
        super.onStart();

        View view = getView();
        setHasOptionsMenu(true); // makes sure onCreateOptionsMenu() gets called

        if(getArguments() != null)
            issue = getArguments().getParcelable("issue");

        mAPIService = ((MainActivity) getActivity()).get_api_service();

        current_page = 1;

        if (adapter == null) {
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
            adapter = new CommentAdapter(getActivity(), this);
            getComments(current_page, issue.getProjectShortName(), issue.getNumber());
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("ASD");
                progress = (ProgressBar) getView().findViewById(R.id.progressBar);
                progress.setVisibility(View.VISIBLE);
                adapter.clear();
                current_page = 1;
                getComments(current_page, issue.getProjectShortName(), issue.getNumber());
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.add).setVisible(true);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                CommentCreateFragment fragment = new CommentCreateFragment();
                Bundle d = new Bundle();
                d.putParcelable("issue", issue);
                fragment.setArguments(d);
                FragmentTransaction ft = getParentFragment().getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, "visible_fragment");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public CommentsFragment() {
        // Required empty public constructor
    }

    public void onSaveInstanceState(Bundle outState) {
        System.out.println("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (issue != null)
            outState.putParcelable("issue", issue);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        System.out.println("onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            issue = savedInstanceState.getParcelable("issue");
        }
    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.comment_list, container, false);
    }


    public void getComments(Integer page, String project, Integer issue_number) {
        Map options = new HashMap<String, String>();
        options.put("page", page);
        mAPIService.getComments(project, issue_number, options).enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getResults());
                    if (response.body().getNext() != null) {
                        getComments(++current_page, issue.getProjectShortName(), issue.getNumber());
                    } else {
                        progress.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResult> call, Throwable t) {
                Toast.makeText(getActivity(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view, int position, Comment item) {
    }
}
