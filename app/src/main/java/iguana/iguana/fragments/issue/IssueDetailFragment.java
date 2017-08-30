package iguana.iguana.fragments.issue;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.common.CommonMethods;
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

    @Override
    public void onStart() {
        super.onStart();
        View v = getView();
        common = new CommonMethods();

        if (issue == null)
            issue = getArguments().getParcelable("issue");

        View view = getView();
        issue_short = (TextView) getView().findViewById(R.id.issue_short);
        issue_short.append(issue.getProjectShortName()+"-"+issue.getNumber());

        priority = (TextView) getView().findViewById(R.id.issue_priority);
        priority.append(common.map_priority_to_string(issue.getPriority()));
        title = (TextView) getView().findViewById(R.id.issue_title);
        title.setText(issue.getTitle());
        MarkdownView desc = (MarkdownView) getView().findViewById(R.id.issue_description);
        if (issue.getDescription().length() > 0) {
            desc.loadMarkdown(issue.getDescription());
        } else {
            desc.setVisibility(View.GONE);
            getView().findViewById(R.id.description_bottom_line).setVisibility(View.GONE);
        }
        status = (TextView) getView().findViewById(R.id.issue_status);
        status.append(issue.getKanbancol());
        type = (TextView) getView().findViewById(R.id.issue_type);
        type.append(issue.getType());
        storypoints = (TextView) getView().findViewById(R.id.issue_storypoints);
        storypoints.append(issue.getStorypoints().toString());
        assignee = (TextView) getView().findViewById(R.id.issue_assignees);
        if (issue.getAssignee().size() == 1) {
            assignee.append(issue.getAssignee().get(0) + " is currently working on this issue.");
        }
        else if (issue.getAssignee().size() > 1) {
            for (String user : issue.getAssignee()) {
                assignee.append(user);
                assignee.append(", ");
            }
            assignee.append("are currently working on this issue");
        }
        else {
            assignee.append("Nobody is currently working on this issue");
        }





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
