package iguana.iguana.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import iguana.iguana.R;
import iguana.iguana.app.MainActivity;
import iguana.iguana.common.CommonMethods;
import iguana.iguana.fragments.issue.IssueEditFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;
import iguana.iguana.remote.apicalls.IssueCalls;


public class IssueAdapter extends BaseAdapter<Issue> implements AdapterOrderBy {
    private CommonMethods common = new CommonMethods();
    private Project project;
    private String status;
    private IssueCalls api;
    private Context context;
    private View view;
    private String sprint;
    private String sprintview;

    public void do_notify() {
        common.order_by(this.order, this.items);
        if (this.rev)
            reverse();
        notifyDataSetChanged();
    }

    public IssueAdapter(Context context, OnViewHolderClick listener, OnViewHolderLongClick long_listener, Project project, String status, View rootView, String sprint, String sprintview) {
        super(context, listener, long_listener);
        this.order = "number";
        this.rev = false;
        this.project = project;
        this.status = status;
        this.api = new IssueCalls(rootView);
        this.context = context;
        this.sprint = sprint;
        this.view = rootView;
        this.sprintview = sprintview;
    }

    public void replace_item(Issue item, String curr_user) {
        System.out.println("replace" + context);
        int idx = -1;
        int i = 0;
        System.out.println(project + status);
        for (Issue iss : this.items) {
            if (iss.getProjectShortName().equals(item.getProjectShortName()) && iss.getNumber().equals(item.getNumber())) {
                idx = i;
                break;
            }
            i++;
        }
        boolean do_notify = false;
        System.out.println(i);

        // case user issue list;
        if (project == null && status == null && sprint == null && sprintview == null) {

            if (idx >= 0) {
                if (item.getAssignee().contains(curr_user)) {
                    System.out.println("replace");
                    items.set(idx, item);
                    notifyItemChanged(idx);
                    do_notify = true;
                } else {
                    System.out.println("remove");
                    items.remove(idx);
                    notifyItemRemoved(idx);
                }
            } else {
                if (item.getAssignee().contains(curr_user)) {
                    System.out.println("add");
                    items.add(item);
                    notifyItemInserted(idx);
                    do_notify = true;
                }
            }
        }

        // case backlog
        if (project != null && status == null && item.getProjectShortName().equals(project.getNameShort()) && project.getCurrentsprint() != null && sprint == null && sprintview == null) {
            if (idx >= 0) {
                if (item.getSprint() != null) {
                    items.remove(idx);
                    notifyItemRemoved(idx);
                } else {
                    items.set(idx, item);
                    do_notify = true;
                }
            } else {
                if (item.getSprint() == null) {
                    items.add(item);
                    do_notify = true;
                }
            }
        }

        // case issuelist (nosprint)
        if (project != null && status == null && item.getProjectShortName().equals(project.getNameShort()) && project.getCurrentsprint() == null && sprint == null && sprintview == null) {
            if (idx >= 0) {
                items.set(idx, item);
                do_notify = true;

            } else {
                items.add(item);
                do_notify = true;

            }
        }

        // case column in sprintboard
        if (project != null && status != null && item.getProjectShortName().equals(project.getNameShort()) && project.getCurrentsprint() != null && sprintview == null) {
            if (idx >= 0) {
                if (item.getKanbancol().equals(status)) {
                    if (item.getSprint() != null) {
                        items.set(idx, item);
                        do_notify = true;
                    } else {
                        items.remove(idx);
                        notifyItemRemoved(idx);
                    }
                } else {
                    items.remove(idx);
                    notifyItemRemoved(idx);
                }

            } else {
                if (item.getKanbancol().equals(status)) {
                    items.add(item);
                    do_notify = true;
                }
            }
        }

        System.out.println(sprintview);
        // case column in board
        if (project != null && status != null && item.getProjectShortName().equals(project.getNameShort()) && project.getCurrentsprint() == null && sprintview == null) {
            if (idx >= 0) {
                if (item.getKanbancol().equals(status)) {
                    items.set(idx, item);
                    do_notify = true;
                } else {
                    items.remove(idx);
                    notifyItemRemoved(idx);
                }
            } else {
                if (item.getKanbancol().equals(status)) {
                    items.add(item);
                    do_notify = true;
                }
            }
        }

        // case Sprintmanager backlog
        if (sprint != null && sprintview.equals("no") && item.getProjectShortName().equals(project.getNameShort())) {
            if(idx >= 0) {
                if (item.getSprint() != null) {
                    System.out.println("IDX: "+idx);
                    items.remove(idx);
                    notifyItemRemoved(idx);
                } else {
                    items.set(idx, item);
                    do_notify = true;
                }
            } else {
                if (item.getSprint() == null) {
                    items.add(item);
                    do_notify = true;
                }
            }
        }

        // case Sprintmanager sprint
        if (sprint != null && sprintview.equals("yes") && item.getProjectShortName().equals(project.getNameShort())) {
            if(idx >= 0) {
                if (item.getSprint() == null) {
                    items.remove(idx);
                    notifyItemRemoved(idx);
                } else if (item.getSprint().split("-")[1].equals(sprint)){
                    items.set(idx, item);
                    do_notify = true;
                }
            } else {
                System.out.println(item.getSprint());
                System.out.println(sprint);
                if (item.getSprint() != null && item.getSprint().split("-")[1].equals(sprint)) {
                    items.add(item);
                    do_notify = true;
                }
            }
        }

        if (do_notify)
            do_notify();
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.issue_list_entry, viewGroup, false);
        return view;
    }

    private int getIndex(List<String> cols, String status) {
        return cols.indexOf(status);
    }

    @Override
    protected void bindView(Issue item, BaseAdapter.ViewHolder viewHolder) {
        if (item != null) {
            final Issue issue = item;
            TextView proj = (TextView) viewHolder.getView(R.id.project);
            ImageView notifications = (ImageView) viewHolder.getView(R.id.notification_icon);
            ImageView assigned = (ImageView) viewHolder.getView(R.id.assigned_icon);
            ImageView priority = (ImageView) viewHolder.getView(R.id.priority);

            if (item.getAssignee().contains(((MainActivity) context).get_user()))
                assigned.setVisibility(View.VISIBLE);
            else
                assigned.setVisibility(View.GONE);

            if (item.getParticipant().contains(((MainActivity) context).get_user()))
                notifications.setVisibility(View.VISIBLE);
            else
                notifications.setVisibility(View.GONE);


            TextView title = new TextView(getContext());
            title.setText(item.getTitle());

            priority.setImageResource(R.drawable.priority_critical_24dp);
            priority.setVisibility(View.VISIBLE);
            switch (item.getPriority()) {
                case 0:
                    priority.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
                    priority.setRotation(180);
                    break;
                case 1:
                    priority.setColorFilter(ContextCompat.getColor(getContext(), R.color.brown));
                    priority.setRotation(135);
                    break;
                case 2:
                    priority.setVisibility(View.GONE);
                    break;
                case 3:
                    priority.setColorFilter(ContextCompat.getColor(getContext(), R.color.orange));
                    priority.setRotation(45);
                    break;
                case 4:
                    priority.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                    priority.setRotation(0);
                    break;
            }

            proj.setText(item.getProjectShortName() + "-" + item.getNumber().toString());
            GradientDrawable back = (GradientDrawable) ContextCompat.getDrawable(getContext() ,R.drawable.igu_identifier_background);
            switch (item.getType()) {
                case "Story":
                    back.setColor(getContext().getResources().getColor(R.color.story));
                    proj.setTextColor(getContext().getResources().getColor(R.color.cardview_dark_background));
                    break;
                case "Bug":
                    back.setColor(getContext().getResources().getColor(R.color.bug));
                    proj.setTextColor(getContext().getResources().getColor(R.color.cardview_light_background));
                    break;
                case "Task":
                    back.setColor(getContext().getResources().getColor(R.color.task));
                    proj.setTextColor(getContext().getResources().getColor(R.color.cardview_dark_background));

                    break;
            }
            proj.setBackground(back);


            FlexboxLayout tags = (FlexboxLayout) viewHolder.getView(R.id.titlebox);
            tags.removeAllViews();
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 15, 15);
            tags.addView(title, params);
            params = new FlexboxLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 15, 15, 0);

            TextView insert = null;
            for (String tag: item.getTags()) {
                String color = null, text_color = null;
                for (List<String> t: item.getProjectTags()) {
                    if (t.contains(tag)) {
                        color = t.get(1);
                        text_color = t.get(2);
                        break;
                    }
                }
                insert = new TextView(getContext());
                insert.setText(tag);
                insert.setPadding(5,5,5,5);


                switch (text_color) {
                    case "000":
                        text_color = "000000";
                        break;
                    case "fff":
                        text_color = "ffffff";
                        break;
                }
                GradientDrawable back_tag = (GradientDrawable) ContextCompat.getDrawable(getContext() ,R.drawable.rounded_corners);
                back_tag.setColor(Color.parseColor("#"+color));
                insert.setBackground(back_tag);
                insert.setTextColor(Color.parseColor("#"+text_color));
                tags.addView(insert, params);
            }

            ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            p.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            p.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            if (insert == null)
                p.topToBottom = R.id.titlebox;
            else
                p.topToBottom = insert.getId();

            proj.setLayoutParams(p);

        }
    }


}
