package iguana.iguana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import iguana.iguana.R;
import iguana.iguana.common.CommonMethods;
import iguana.iguana.models.Issue;


public class IssueAdapter extends BaseAdapter<Issue> implements AdapterOrderBy{
    private CommonMethods common = new CommonMethods();

    public void do_notify() {
        common.order_by(this.order, this.items);
        if (this.rev)
            reverse();
        notifyDataSetChanged();
    }

    public IssueAdapter(Context context, OnViewHolderClick listener, OnViewHolderLongClick long_listener) {
        super(context, listener, long_listener);
        this.order = "number";
        this.rev = false;
    }

    public void replace_item(Issue item) {
        int idx = 0;
        int i = 0;
        for (Issue iss:this.items)   {
            if (iss.getProjectShortName().equals(item.getProjectShortName()) && iss.getNumber().equals(item.getNumber())) {
                idx = i;
                break;
            }
            i++;
        }
        items.set(idx, item);
        notifyItemChanged(idx);
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.issue_list_entry, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(Issue item, BaseAdapter.ViewHolder viewHolder) {
        if (item != null) {
            TextView title = (TextView) viewHolder.getView(R.id.title);
            TextView project = (TextView) viewHolder.getView(R.id.project);
            ImageView priority = (ImageView) viewHolder.getView(R.id.priority);

            title.setText(item.getTitle() + " (" + item.getType() + ")");
            priority.setImageResource(common.getPriorityImage(item.getPriority()));
            project.setText(item.getProjectShortName() + "-" + item.getNumber().toString());
        }
    }

}
