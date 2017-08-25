package iguana.iguana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;

import iguana.iguana.R;
import iguana.iguana.models.Project;


public class ProjectAdapter extends BaseAdapter<Project> {


    public ProjectAdapter(Context context, OnViewHolderClick listener) {
        super(context, listener, null);
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.project_list_entry, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(Project item, BaseAdapter.ViewHolder viewHolder) {
        if (item != null) {
            TextView name = (TextView) viewHolder.getView(R.id.comment_info);
            TextView created_at = (TextView) viewHolder.getView(R.id.created_at);
            TextView members = (TextView) viewHolder.getView(R.id.members);

            name.setText(item.getName()+ " ("+ item.getNameShort()+")");
            created_at.setText(item.getCreatedAt());
            Collection<String> dev = item.getDeveloper();
            Collection<String> man = item.getManager();
            for (String x:man) {
                if (!dev.contains(x))
                    dev.add(x);
            }
            int count = dev.size();
            members.setText(count + (count > 1 ? " Members" :" Member"));
        }
    }

}
