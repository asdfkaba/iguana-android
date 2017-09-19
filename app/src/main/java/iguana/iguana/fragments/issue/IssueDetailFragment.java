package iguana.iguana.fragments.issue;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.common.CommonMethods;
import iguana.iguana.events.issue_changed;
import iguana.iguana.models.Issue;
import iguana.iguana.remote.APIService;
import us.feras.mdv.MarkdownView;


public class IssueDetailFragment extends Fragment {
    TextView title, status, type, storypoints, priority, assignee, creator, issue_short;
    MarkdownView desc;
    Issue issue;
    ProgressBar progress;
    CommonMethods common;

    public IssueDetailFragment() {}


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(issue_changed event) {
        Issue new_issue = event.getIssue();
        if (issue.getNumber().equals(new_issue.getNumber()) && issue.getProjectShortName().equals(new_issue.getProjectShortName())) {
            this.issue = new_issue;
            init();
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void init() {
        issue_short.setText(issue.getProjectShortName()+"-"+issue.getNumber());
        priority.setText("Priority: "+ common.map_priority_to_string(issue.getPriority()));
        title.setText(issue.getTitle());
        if (issue.getDescription().length() > 0) {
            desc.loadMarkdown(issue.getDescription());
        } else {
            desc.setVisibility(View.GONE);
            getView().findViewById(R.id.description_bottom_line).setVisibility(View.GONE);
        }
        status.setText("Status: "+ issue.getKanbancol());
        type.setText("Type: "+ issue.getType());
        assignee = (TextView) getView().findViewById(R.id.issue_assignees);
        if (issue.getAssignee().size() == 1) {
            assignee.setText("");
            assignee.append(issue.getAssignee().get(0) + " is currently working on this issue.");
        }
        else if (issue.getAssignee().size() > 1) {
            assignee.setText("");
            for (String user : issue.getAssignee()) {
                assignee.append(user);
                assignee.append(", ");
            }
            assignee.append("are currently working on this issue");
        }
        else {
            assignee.setText("");
            assignee.append("Nobody is currently working on this issue");
        }
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        View v = getView();
        common = new CommonMethods();

        if (issue == null)
            issue = getArguments().getParcelable("issue");

        issue_short = (TextView) getView().findViewById(R.id.issue_short);
        priority = (TextView) getView().findViewById(R.id.issue_priority);
        title = (TextView) getView().findViewById(R.id.issue_title);
        desc = (MarkdownView) getView().findViewById(R.id.issue_description);
        status = (TextView) getView().findViewById(R.id.issue_status);
        type = (TextView) getView().findViewById(R.id.issue_type);
        storypoints = (TextView) getView().findViewById(R.id.issue_storypoints);
        assignee = (TextView) getView().findViewById(R.id.issue_assignees);

        init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (issue != null) {
            outState.putParcelable("issue", issue);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            if (issue == null)
                issue = savedInstanceState.getParcelable("issue");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issue_detail, container, false);
    }

}
