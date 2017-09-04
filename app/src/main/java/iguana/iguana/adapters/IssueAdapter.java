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
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import iguana.iguana.R;
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

    public void do_notify() {
        common.order_by(this.order, this.items);
        if (this.rev)
            reverse();
        notifyDataSetChanged();
    }

    public IssueAdapter(Context context, OnViewHolderClick listener, OnViewHolderLongClick long_listener, Project project, String status, View rootView) {
        super(context, listener, long_listener);
        this.order = "number";
        this.rev = false;
        this.project = project;
        this.status = status;
        this.api = new IssueCalls(rootView);
        this.context = context;
        view = rootView;
    }

    public void replace_item(Issue item, String curr_user) {
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

        // case user issue list;
        if (project == null && status == null) {
            if (idx >= 0) {
                if (item.getAssignee().contains(curr_user)) {
                    items.set(idx, item);
                    do_notify = true;
                } else {
                    items.remove(idx);
                    notifyItemRemoved(idx);
                }
            } else {
                if (item.getAssignee().contains(curr_user)) {
                    items.add(item);
                    do_notify = true;
                }
            }
        }

        // case backlog
        if (project != null && status == null && item.getProjectShortName().equals(project.getNameShort()) && project.getCurrentsprint() != null) {
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
        if (project != null && status == null && item.getProjectShortName().equals(project.getNameShort()) && project.getCurrentsprint() == null) {
            if (idx >= 0) {
                items.set(idx, item);
                do_notify = true;

            } else {
                items.add(item);
                do_notify = true;
            }
        }

        // case column in sprintboard
        if (project != null && status != null && item.getProjectShortName().equals(project.getNameShort()) && project.getCurrentsprint() != null) {
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

        // case column in board
        if (project != null && status != null && item.getProjectShortName().equals(project.getNameShort()) && project.getCurrentsprint() == null) {
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
            TextView title = (TextView) viewHolder.getView(R.id.title);
            TextView proj = (TextView) viewHolder.getView(R.id.project);
            ImageView priority = (ImageView) viewHolder.getView(R.id.priority);

            title.setText(item.getTitle() + " (" + item.getType() + ")");
            priority.setImageResource(common.getPriorityImage(item.getPriority()));
            proj.setText(item.getProjectShortName() + "-" + item.getNumber().toString());
            ImageButton edit = (ImageButton) viewHolder.getView(R.id.edit);
            ImageButton left = (ImageButton) viewHolder.getView(R.id.left);
            ImageButton right = (ImageButton) viewHolder.getView(R.id.right);
            Button sprint_add = (Button) viewHolder.getView(R.id.sprint_plus);
            Button sprint_remove = (Button) viewHolder.getView(R.id.sprint_minus);
            Space space = (Space) viewHolder.getView(R.id.space);
            LinearLayout issue_buttons = (LinearLayout) viewHolder.getView(R.id.issue_buttons);

            if (item.isSelected()) {
                issue_buttons.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager frag;
                        frag = ((Activity) v.getContext()).getFragmentManager();
                        IssueEditFragment fragment = new IssueEditFragment();
                        Bundle d = new Bundle();
                        d.putParcelable("issue", issue);
                        fragment.setArguments(d);
                        FragmentTransaction ft = frag.beginTransaction();
                        ft.replace(R.id.content_frame, fragment, "issue_edit");
                        ft.addToBackStack("issue_edit");
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                });
                if (status != null) {
                    if (project.getKanbancol().indexOf(issue.getKanbancol()) > 0)
                        left.setVisibility(View.VISIBLE);
                    if (project.getKanbancol().indexOf(issue.getKanbancol()) < project.getKanbancol().size() - 1 && project.getKanbancol().indexOf(issue.getKanbancol()) != -1)
                        right.setVisibility(View.VISIBLE);
                    if (project.getCurrentsprint() == null)
                        space.setVisibility(View.GONE);

                    left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HashMap body = new HashMap();
                            body.put("kanbancol", project.getKanbancol().get(project.getKanbancol().indexOf(issue.getKanbancol()) - 1));
                            api.patchIssue(issue, body);
                        }
                    });

                    right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("CLICK");
                            HashMap body = new HashMap();
                            body.put("kanbancol", project.getKanbancol().get(project.getKanbancol().indexOf(issue.getKanbancol()) + 1));
                            System.out.println(body);
                            api.patchIssue(issue, body);
                        }
                    });
                }
                if (project != null && project.getCurrentsprint() != null && status == null) {
                    sprint_add.setVisibility(View.VISIBLE);

                    sprint_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("CLICK");
                            HashMap body = new HashMap();
                            body.put("sprint", project.getCurrentsprint().toString().split("-")[1]);
                            System.out.println(body);
                            api.patchIssue(issue, body);
                        }
                    });
                }
                if (project != null && project.getCurrentsprint() != null && status != null) {
                    sprint_remove.setVisibility(View.VISIBLE);
                    sprint_remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("CLICK");
                            HashMap body = new HashMap();
                            body.put("sprint", null);
                            System.out.println(body);
                            api.patchIssue(issue, body);
                        }
                    });
                }
                if (project == null || (project.getCurrentsprint() == null && status == null))
                    space.setVisibility(View.GONE);


            } else {
                edit.setVisibility(View.GONE);
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                sprint_add.setVisibility(View.GONE);
                sprint_remove.setVisibility(View.GONE);
                issue_buttons.setVisibility(View.GONE);
                edit.setOnClickListener(null);
            }

        }
    }


}
