package iguana.iguana.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import iguana.iguana.R;
import iguana.iguana.common.CommonMethods;
import iguana.iguana.fragments.timelog.TimelogDetailFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Timelog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TimelogAdapter extends BaseAdapter<Timelog> {

    public TimelogAdapter(Context context, OnViewHolderClick listener) {
        super(context, listener, null);
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.timelog_list_entry, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(Timelog item, BaseAdapter.ViewHolder viewHolder) {
        if (item != null) {
            TextView time = (TextView) viewHolder.getView(R.id.time);
            TextView created_at = (TextView) viewHolder.getView(R.id.created_at);
            TextView issue = (TextView) viewHolder.getView(R.id.issue);
            time.setText(item.getTime());
            created_at.setText(item.getCreatedAt());
            issue.setText(item.getIssue().split(" ")[0] );
        }
    }


}
