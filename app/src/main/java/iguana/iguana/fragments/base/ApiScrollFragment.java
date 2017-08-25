package iguana.iguana.fragments.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import iguana.iguana.R;

public class ApiScrollFragment extends ApiFragment {
    protected int scroll_y;
    protected RecyclerView recyclerView;
    protected int current_page;
    protected  ProgressBar progress;
    protected SwipeRefreshLayout swipeRefreshLayout;


    public void onPause() {
        super.onPause();
        scroll_y = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        View startView = recyclerView.getChildAt(0);
    }

    public void onResume() {
        super.onResume();
        if (scroll_y!= -1) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(scroll_y, 0);
        }
    }

    public void onStart() {
        super.onStart();
        current_page = 1;
        View view = getView();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.scrollToPosition(scroll_y);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);


    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scroll", scroll_y);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            scroll_y = savedInstanceState.getInt("scroll");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview_list, container, false);
    }
}
