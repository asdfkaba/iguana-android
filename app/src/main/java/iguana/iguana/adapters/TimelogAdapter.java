package iguana.iguana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import iguana.iguana.R;
import iguana.iguana.models.Timelog;


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
