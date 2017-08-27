package iguana.iguana.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collection;

import iguana.iguana.R;
import iguana.iguana.fragments.issue.IssueEditFragment;
import iguana.iguana.fragments.project.ProjectEditFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;


public class ProjectAdapter extends BaseAdapter<Project> {


    public ProjectAdapter(Context context, OnViewHolderClick listener, OnViewHolderLongClick long_listener) {
        super(context, listener, long_listener);
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.project_list_entry, viewGroup, false);
        return view;
    }

    public void replace_item(Project item) {
        int idx = -1;
        int i = 0;
        for (Project project:this.items)   {
            if (project.getNameShort().equals(item.getNameShort())) {
                idx = i;
                break;
            }
            i++;
        }
        if (idx >= 0) {
            System.out.println("REPLACE");
            items.set(idx, item);
            notifyItemChanged(idx);
        } else {
            items.add(0, item);
            notifyItemInserted(0);
        }
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

            ImageButton edit = (ImageButton) viewHolder.getView(R.id.edit);
            final Project project = item;

            if (item.isSelected()) {
                edit.setVisibility(View.VISIBLE);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager frag;
                        frag = ((Activity) v.getContext()).getFragmentManager();
                        ProjectEditFragment fragment = new ProjectEditFragment();
                        Bundle d = new Bundle();
                        d.putParcelable("project", project);
                        fragment.setArguments(d);
                        FragmentTransaction ft = frag.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, "issue_edit");
                        ft.addToBackStack("issue_edit");
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                });
            } else {
                edit.setVisibility(View.GONE);
                edit.setOnClickListener(null);
            }
        }
    }

}
